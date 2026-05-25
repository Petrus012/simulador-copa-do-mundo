package br.ufla.copa.core.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GerenciadorDeArquivos {
    private static final String CAMINHO_PARTIDAS = "src/resources/partidas.csv";

    private List<Grupo> grupos;
    private List<Estadio> estadios;
    private List<Selecao> selecoes;
    private List<Partida> partidas;

    public GerenciadorDeArquivos() {
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
                if (linha.isBlank()) {
                    continue;
                }

                String[] campos = linha.split(",", -1);
                if (campos.length < 10) {
                    continue;
                }

                Selecao timeDaCasa = buscarOuCriarSelecao(campos[3].trim());
                Selecao timeVisitante = buscarOuCriarSelecao(campos[4].trim());
                Estadio estadio = buscarOuCriarEstadio(campos[5].trim(), campos[6].trim());
                Grupo grupo = buscarOuCriarGrupo(campos[7].trim().charAt(0));
                Partida partida = new Partida(
                        Integer.parseInt(campos[0].trim()),
                        LocalDate.parse(campos[1].trim()),
                        LocalTime.parse(campos[2].trim()),
                        timeDaCasa,
                        timeVisitante,
                        estadio,
                        grupo,
                        Integer.parseInt(campos[8].trim()),
                        campos[9].trim());

                grupo.adicionarSelecao(timeDaCasa);
                grupo.adicionarSelecao(timeVisitante);
                grupo.adicionarPartida(partida);
                partidas.add(partida);
            }
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Nao foi possivel importar as partidas do arquivo: " + caminhoArquivo, e);
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
        for (Selecao selecao : selecoes) {
            if (selecao.getNome().equals(nome)) {
                return selecao;
            }
        }

        Selecao selecao = new Selecao(nome);
        selecoes.add(selecao);
        return selecao;
    }

    private Estadio buscarOuCriarEstadio(String nome, String pais) {
        for (Estadio estadio : estadios) {
            if (estadio.getNome().equals(nome)) {
                return estadio;
            }
        }

        Estadio estadio = new Estadio(nome, pais);
        estadios.add(estadio);
        return estadio;
    }

    private Grupo buscarOuCriarGrupo(char id) {
        for (Grupo grupo : grupos) {
            if (grupo.getId() == id) {
                return grupo;
            }
        }

        Grupo grupo = new Grupo(id);
        grupos.add(grupo);
        return grupo;
    }
}
