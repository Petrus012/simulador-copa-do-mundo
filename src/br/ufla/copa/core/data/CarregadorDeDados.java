package br.ufla.copa.core.data;

import br.ufla.copa.core.model.Analista;
import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Palpite;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.ResultadoImportacaoPalpites;
import br.ufla.copa.core.model.Selecao;
import br.ufla.copa.core.model.StatusPartida;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Responsável por toda leitura/escrita de dados: CSV de partidas, palpites e resultados,
// persistência binária de analistas e sincronização HTTP dos resultados oficiais (H1/H2/H4/H8/H10)
public class CarregadorDeDados {
    private static final String CAMINHO_PARTIDAS = "src/resources/partidas.csv";
    private static final String CAMINHO_ANALISTAS_BIN = "src/resources/analistas.dat";

    // URL e cache local para a sincronização online (H10)
    private static final String URL_RESULTADOS_WEB = "https://raw.githubusercontent.com/caburu/atualizador-resultados-partidas/main/resultados.csv";
    private static final String CAMINHO_WEB_CACHE = "src/resources/resultados_web_cache.csv";

    private List<Grupo> grupos;
    private List<Estadio> estadios;
    private List<Selecao> selecoes;
    private List<Partida> partidas;
    // Snapshot dos resultados antes da sync web; null quando sync está desativada
    private Map<Integer, int[]> snapshotResultados;

    public CarregadorDeDados() {
        this.grupos = new ArrayList<>();
        this.estadios = new ArrayList<>();
        this.selecoes = new ArrayList<>();
        this.partidas = new ArrayList<>();

        importarPartidas(CAMINHO_PARTIDAS);
    }

    // -----------------------------------------------------------------------
    // Sincronização online (H10)
    // -----------------------------------------------------------------------

    // Baixa o CSV remoto e importa os resultados; timeout de 5 s para não travar o boot
    public void sincronizarResultadosOnline() {
        try {
            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(URL_RESULTADOS_WEB))
                    .GET()
                    .build();

            Path caminhoDestino = Path.of(CAMINHO_WEB_CACHE);

            HttpResponse<Path> resposta = cliente.send(
                    requisicao,
                    HttpResponse.BodyHandlers.ofFile(caminhoDestino)
            );

            if (resposta.statusCode() == 200) {
                importarResultadosOficiais(CAMINHO_WEB_CACHE);
            } else {
                System.err.println("Aviso H10: Servidor retornou código " + resposta.statusCode() + " na busca online.");
            }

        } catch (Exception e) {
            System.err.println("Aviso: Sem conexão com a internet para buscar placares da Web. Mantendo base local.");
        }
    }

    // Salva quais partidas estão FINALIZADA antes da sync web (base para o snapshot)
    public void salvarSnapshotResultados() {
        snapshotResultados = new HashMap<>();
        for (Partida p : partidas) {
            if (p.getStatus() == StatusPartida.FINALIZADA) {
                snapshotResultados.put(p.getId(), new int[]{p.getGolsTimeCasaOficial(), p.getGolsTimeVisitanteOficial()});
            }
        }
    }

    // Indica se a partida estava FINALIZADA antes da sync web (bloqueia palpites mesmo offline)
    public boolean isNoSnapshot(int idPartida) {
        return snapshotResultados != null && snapshotResultados.containsKey(idPartida);
    }

    // Desfaz a sync web: limpa todos os resultados e reaplica apenas os do snapshot
    public void restaurarSnapshotResultados() {
        for (Partida p : partidas) {
            p.limparResultadoOficial();
        }
        if (snapshotResultados != null) {
            for (Map.Entry<Integer, int[]> entrada : snapshotResultados.entrySet()) {
                Partida p = buscarPartidaPorId(entrada.getKey());
                if (p != null) {
                    p.setResultadoOficial(entrada.getValue()[0], entrada.getValue()[1]);
                }
            }
        }
        snapshotResultados = null;
    }

    // -----------------------------------------------------------------------
    // Persistência binária de analistas (H8)
    // -----------------------------------------------------------------------

    public void salvarAnalistasNoHD(List<Analista> analistas) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CAMINHO_ANALISTAS_BIN))) {
            oos.writeObject(analistas);
        } catch (IOException e) {
            System.err.println("Erro ao salvar Memory Card: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Analista> carregarAnalistasDoHD() {
        Path caminho = Path.of(CAMINHO_ANALISTAS_BIN);
        if (!Files.exists(caminho)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CAMINHO_ANALISTAS_BIN))) {
            return (List<Analista>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    // -----------------------------------------------------------------------
    // Importações CSV (H1, H2, H4)
    // -----------------------------------------------------------------------

    // Lê partidas.csv e reconstrói grupos, seleções e partidas do zero (H1)
    public void importarPartidas(String caminhoArquivo) {
        Path caminho = Path.of(caminhoArquivo);
        grupos.clear();
        estadios.clear();
        selecoes.clear();
        partidas.clear();

        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha = leitor.readLine();
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) continue;

                String[] campos = linha.split(",", -1);
                if (campos.length < 10) continue;

                int idPartida = Integer.parseInt(campos[0].trim());
                Selecao timeDaCasa = buscarOuCriarSelecao(campos[3].trim());
                Selecao timeVisitante = buscarOuCriarSelecao(campos[4].trim());
                buscarOuCriarEstadio(campos[5].trim(), campos[6].trim());

                char idGrupo = campos[7].trim().charAt(0);
                Grupo grupo = buscarOuCriarGrupo(idGrupo);

                Partida partida = new Partida(idPartida, timeDaCasa, timeVisitante);

                grupo.adicionarSelecao(timeDaCasa);
                grupo.adicionarSelecao(timeVisitante);
                grupo.adicionarPartida(partida);
                partidas.add(partida);
            }
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException(e);
        }
    }

    // Faz apenas o parsing do CSV de palpites, sem aplicar regras de negócio;
    // preenche parciais[0] com o total de linhas com apenas um gol informado (H2)
    public Map<Integer, Palpite> lerPalpitesDoArquivo(String caminhoArquivo, int[] parciais) {
        Map<Integer, Palpite> resultado = new HashMap<>();
        parciais[0] = 0;
        if (!Files.exists(Path.of(caminhoArquivo))) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + caminhoArquivo);
        }
        try (BufferedReader leitor = Files.newBufferedReader(Path.of(caminhoArquivo), StandardCharsets.UTF_8)) {
            leitor.readLine(); // pula cabeçalho
            String linha = leitor.readLine();
            while (linha != null) {
                if (!linha.isBlank()) {
                    String[] campos = linha.split(",", -1);
                    if (campos.length >= 5) {
                        String golsCasaStr = campos[2].trim();
                        String golsVisStr  = campos[4].trim();
                        boolean temCasa = !golsCasaStr.isEmpty();
                        boolean temVis  = !golsVisStr.isEmpty();
                        if (temCasa && temVis) {
                            int idPartida = Integer.parseInt(campos[0].trim());
                            int golsCasa  = Integer.parseInt(golsCasaStr);
                            int golsVis   = Integer.parseInt(golsVisStr);
                            if (golsCasa >= 0 && golsVis >= 0) {
                                resultado.put(idPartida, new Palpite(golsCasa, golsVis));
                            }
                        } else if (temCasa || temVis) {
                            parciais[0]++;
                        }
                    }
                }
                linha = leitor.readLine();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return resultado;
    }

    // Importa palpites do CSV e aplica nas partidas ainda não finalizadas (H2)
    public ResultadoImportacaoPalpites importarPalpites(String caminhoArquivo) {
        int[] parciais = new int[1];
        Map<Integer, Palpite> palpites = lerPalpitesDoArquivo(caminhoArquivo, parciais);
        int registrados = 0;
        for (Map.Entry<Integer, Palpite> entrada : palpites.entrySet()) {
            Partida partida = buscarPartidaPorId(entrada.getKey());
            if (partida != null && partida.getStatus() != StatusPartida.FINALIZADA) {
                partida.setPalpite(entrada.getValue());
                registrados++;
            }
        }
        return new ResultadoImportacaoPalpites(registrados, parciais[0], palpites);
    }

    // Importa resultados oficiais de forma autoritativa: limpa tudo antes de aplicar o CSV,
    // garantindo que partidas ausentes voltem para AGENDADA (H4)
    public void importarResultadosOficiais(String caminhoArquivo) {
        Path caminho = Path.of(caminhoArquivo);

        if (!Files.exists(caminho)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + caminhoArquivo);
        }

        for (Partida p : partidas) {
            p.limparResultadoOficial();
        }

        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha = leitor.readLine();
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) continue;

                String[] campos = linha.split(",", -1);
                if (campos.length < 5) continue;

                int idPartida = Integer.parseInt(campos[0].trim());
                String golsCasaStr = campos[2].trim();
                String golsVisStr = campos[4].trim();

                if (golsCasaStr.isEmpty() || golsVisStr.isEmpty()) continue;

                int golsCasa = Integer.parseInt(golsCasaStr);
                int golsVis = Integer.parseInt(golsVisStr);

                if (golsCasa < 0 || golsVis < 0) continue;

                Partida partida = buscarPartidaPorId(idPartida);
                if (partida != null) {
                    partida.setResultadoOficial(golsCasa, golsVis);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<Grupo> getGrupos() { return Collections.unmodifiableList(grupos); }
    public List<Estadio> getEstadios() { return Collections.unmodifiableList(estadios); }
    public List<Selecao> getSelecoes() { return Collections.unmodifiableList(selecoes); }
    public List<Partida> getPartidas() { return Collections.unmodifiableList(partidas); }

    private Selecao buscarOuCriarSelecao(String nome) {
        for (Selecao s : selecoes) {
            if (s.getNome().equals(nome)) return s;
        }
        Selecao nova = new Selecao(nome);
        selecoes.add(nova);
        return nova;
    }

    private Estadio buscarOuCriarEstadio(String nome, String pais) {
        for (Estadio e : estadios) {
            if (e.getNome().equals(nome)) return e;
        }
        Estadio novo = new Estadio(nome, pais);
        estadios.add(novo);
        return novo;
    }

    private Grupo buscarOuCriarGrupo(char id) {
        for (Grupo g : grupos) {
            if (g.getNome() == id) return g;
        }
        Grupo novo = new Grupo(id);
        grupos.add(novo);
        return novo;
    }

    private Partida buscarPartidaPorId(int id) {
        for (Partida p : partidas) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}
