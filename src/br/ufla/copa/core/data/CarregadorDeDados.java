package br.ufla.copa.core.data;

import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.Selecao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
}