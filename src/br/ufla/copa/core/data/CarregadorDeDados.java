package br.ufla.copa.core.data;

import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Palpite;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.Selecao;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarregadorDeDados {

    private static final String CAMINHO_ARQUIVO_PARTIDAS = "src/resources/partidas.csv";
    
    private String lerCampoString(String[] campos, int posicao) {
        if (posicao < campos.length) {
            return campos[posicao].trim();
        }
        return "";
    }

    private int lerCampoInteiro(String[] campos, int posicao) {
        if (posicao < campos.length) {
            if (campos[posicao] != null && !campos[posicao].trim().isEmpty()) {
                return Integer.parseInt(campos[posicao].trim());
            }
        }
        return -1;
    }

    public Map<String, Grupo> carregarPartidas() {
        Map<String, Grupo> grupos = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CAMINHO_ARQUIVO_PARTIDAS), "UTF-8"))) {
            String linha = br.readLine(); 
            
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(",");

                int id = lerCampoInteiro(campos, 0);
                String data = lerCampoString(campos, 1);
                String hora = lerCampoString(campos, 2);
                Selecao timeA = new Selecao(lerCampoString(campos, 3));
                Selecao timeB = new Selecao(lerCampoString(campos, 4));
                String nomeGrupo = lerCampoString(campos, 7);

                Partida partida = new Partida(id, timeA, timeB, nomeGrupo, data, hora);

                if (!grupos.containsKey(nomeGrupo)) {
                    grupos.put(nomeGrupo, new Grupo(nomeGrupo));
                }
                grupos.get(nomeGrupo).adicionarPartida(partida);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo " + CAMINHO_ARQUIVO_PARTIDAS + ": " + e.getMessage());
        }

        return grupos;
    }

    public List<Palpite> carregarPalpites(String caminhoArquivo, SimuladorAnalistasCopaDoMundo simulador) {
        List<Palpite> palpitesValidos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(caminhoArquivo), "UTF-8"))) {
            String linha = br.readLine(); 
            
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(",");

                int idPartida = lerCampoInteiro(campos, 0);
                String strGolsA = lerCampoString(campos, 2);
                String strGolsB = lerCampoString(campos, 4);

                if (strGolsA.isEmpty() && strGolsB.isEmpty()) {
                    continue;
                }

                if (strGolsA.isEmpty() || strGolsB.isEmpty()) {
                    System.out.println("Palpite invalido (parcial) para o jogo ID " + idPartida + ". Ignorado.");
                    continue;
                }

                int golsA = Integer.parseInt(strGolsA);
                int golsB = Integer.parseInt(strGolsB);

                Partida partida = simulador.buscarPartidaPorId(idPartida);

                if (partida != null) {
                    if (partida.isFinalizada()) {
                        System.out.println("Partida ID " + idPartida + " ja esta finalizada. Palpite ignorado.");
                        continue;
                    }

                    try {
                        Palpite palpite = new Palpite(partida, golsA, golsB);
                        palpitesValidos.add(palpite);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erro no palpite do jogo ID " + idPartida + ": " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de palpites: " + e.getMessage());
        }

        return palpitesValidos;
    }
}