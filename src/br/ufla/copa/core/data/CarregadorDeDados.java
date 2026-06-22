package br.ufla.copa.core.data;

import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Selecao;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.Palpite;
import br.ufla.copa.core.model.StatusPartida;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarregadorDeDados {
    private static final String CAMINHO_PARTIDAS = "src/resources/partidas.csv";
    
    private List<Grupo> grupos;
    private List<Estadio> estadios;
    private List<Selecao> selecoes;
    private List<Partida> partidas;

    public CarregadorDeDados() {
        this.grupos = new ArrayList<>();
        this.estadios = new ArrayList<>();
        this.selecoes = new ArrayList<>();
        this.partidas = new ArrayList<>();

        importarPartidas(CAMINHO_PARTIDAS);
    }

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

    public int importarPalpites(String caminhoArquivo) {
        Path caminho = Path.of(caminhoArquivo);
        int palpitesRegistrados = 0;

        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha = leitor.readLine(); // pula o cabeçalho
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) continue;

                String[] campos = linha.split(",", -1);
                
                // Prevenção de erro caso a linha não tenha colunas suficientes
                if (campos.length < 5) continue; 

                int idPartida = Integer.parseInt(campos[0].trim());
                
                // CORREÇÃO DOS ÍNDICES: 2 e 4
                String golsCasaStr = campos[2].trim(); 
                String golsVisStr = campos[4].trim();

                if (golsCasaStr.isEmpty() || golsVisStr.isEmpty()) continue;

                int golsCasa = Integer.parseInt(golsCasaStr);
                int golsVis = Integer.parseInt(golsVisStr);

                if (golsCasa < 0 || golsVis < 0) continue;

                Partida partida = buscarPartidaPorId(idPartida);
                if (partida != null) {
                    if (partida.getStatus() != StatusPartida.FINALIZADA) {
                        Palpite palpite = new Palpite(golsCasa, golsVis);
                        partida.setPalpite(palpite);
                        palpitesRegistrados++;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return palpitesRegistrados;
    }

    public void importarResultadosOficiais(String caminhoArquivo) {
        Path caminho = Path.of(caminhoArquivo);

        try (BufferedReader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha = leitor.readLine(); 
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) continue;

                String[] campos = linha.split(",", -1);
                if (campos.length < 5) continue;

                int idPartida = Integer.parseInt(campos[0].trim());

                // Mesmo formato do modelo_palpites.csv: id, timeA, gols_timeA, x, gols_timeB, timeB
                String golsCasaStr = campos[2].trim();
                String golsVisStr = campos[4].trim();

                if (golsCasaStr.isEmpty() || golsVisStr.isEmpty()) continue;

                int golsCasa = Integer.parseInt(golsCasaStr);
                int golsVis = Integer.parseInt(golsVisStr);

                Partida partida = buscarPartidaPorId(idPartida);
                if (partida != null) {
                    partida.setResultadoOficial(golsCasa, golsVis);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<Grupo> getGrupos() {
        return Collections.unmodifiableList(grupos);
    }

    public List<Estadio> getEstadios() {
        return Collections.unmodifiableList(estadios);
    }

    public List<Selecao> getSelecoes() {
        return Collections.unmodifiableList(selecoes);
    }

    public List<Partida> getPartidas() {
        return Collections.unmodifiableList(partidas);
    }

    private Selecao buscarOuCriarSelecao(String nome) {
        for (Selecao s : selecoes) {
            if (s.getNome().equals(nome)) {
                return s;
            }
        }
        Selecao nova = new Selecao(nome);
        selecoes.add(nova);
        return nova;
    }

    private Estadio buscarOuCriarEstadio(String nome, String pais) {
        for (Estadio e : estadios) {
            if (e.getNome().equals(nome)) {
                return e;
            }
        }
        Estadio novo = new Estadio(nome, pais);
        estadios.add(novo);
        return novo;
    }

    private Grupo buscarOuCriarGrupo(char id) {
        for (Grupo g : grupos) {
            if (g.getNome() == id) {
                return g;
            }
        }
        Grupo novo = new Grupo(id);
        grupos.add(novo);
        return novo;
    }

    private Partida buscarPartidaPorId(int id) {
        for (Partida p : partidas) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}