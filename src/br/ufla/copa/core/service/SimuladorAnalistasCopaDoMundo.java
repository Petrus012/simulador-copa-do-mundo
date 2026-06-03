package br.ufla.copa.core.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

import br.ufla.copa.core.data.CarregadorDeDados;
import br.ufla.copa.core.model.ClassificacaoComparator;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Palpite;
import br.ufla.copa.core.model.Partida;

/**
 * Código de exemplo da classe Princial que trata a Regra de Negócio
 * (Domínio) do projeto
 */
public class SimuladorAnalistasCopaDoMundo {
    // Lista de estádios da copa
    private List<Estadio> estadios;
    // Instância da própria classe (Padrão de Projeto Singleton)
    private static SimuladorAnalistasCopaDoMundo instancia;

    private Map<String, Grupo> gruposCopa;
    private Map<Integer, Palpite> palpitesAnalista;

    /**
     * Construtor da classe
     * Importante: construtor é privado para uso do padrão de projeto Singleton
     */
    private SimuladorAnalistasCopaDoMundo() {
        // cria a a lista de estádios
        estadios = new ArrayList<>();

        // apenas como exemplo, carrega alguns estádios
        estadios.add(new Estadio("Mineirao", 70000));
        estadios.add(new Estadio("Maracana", 85000));

        CarregadorDeDados carregador = new CarregadorDeDados();
        this.gruposCopa = carregador.carregarPartidas();
        this.palpitesAnalista = new HashMap<>();
    }

    /**
     * Retorna a instância única da classe (Padrão de Projeto Singleton)
     * @return a instância da classe
     */
    public static SimuladorAnalistasCopaDoMundo getInstance() {
        if (instancia == null) {
            instancia = new SimuladorAnalistasCopaDoMundo();
        }
        return instancia;
    }

    /**
     * Retorna uma versão não modificável da coleção de estádios
     * @return coleção de estádios
     */
    public List<Estadio> buscarEstadios() {
        return Collections.unmodifiableList(estadios);
    }

    public Grupo buscarGrupo(String letraGrupo) {
        if (gruposCopa == null) {
            return null;
        }
        return gruposCopa.get(letraGrupo);
    }

    public Partida buscarPartidaPorId(int id) {
        if (gruposCopa != null) {
            for (Grupo grupo : gruposCopa.values()) {
                for (int i = 0; i < grupo.getPartidas().size(); i++) {
                    Partida p = grupo.getPartidas().get(i);
                    if (p.getId() == id) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public int importarPalpites(String caminhoArquivo) {
        CarregadorDeDados carregador = new CarregadorDeDados();
        List<Palpite> novosPalpites = carregador.carregarPalpites(caminhoArquivo, this);
        
        int quantidadeImportada = 0;
        
        for (int i = 0; i < novosPalpites.size(); i++) {
            Palpite p = novosPalpites.get(i);
            palpitesAnalista.put(p.getPartida().getId(), p);
            Partida partida = buscarPartidaPorId(p.getPartida().getId());
            if (partida != null) {
                partida.setPalpite(p);
            }
            
            quantidadeImportada++;
        }
        
        return quantidadeImportada;
    }

    public List<EstatisticasSelecao> obterClassificacaoGrupo(String nomeGrupo) {
        Grupo grupoEncontrado = null;
        if (gruposCopa != null) {
            for (String chave : gruposCopa.keySet()) {
                if (chave.equalsIgnoreCase(nomeGrupo)) {
                    grupoEncontrado = gruposCopa.get(chave);
                    break;
                }
            }
        }

        if (grupoEncontrado == null) {
            throw new IllegalArgumentException("Grupo não encontrado: " + nomeGrupo);
        }

        List<EstatisticasSelecao> classificacao = grupoEncontrado.calcularEstatisticasPelosPalpites();
        Collections.sort(classificacao, new ClassificacaoComparator(grupoEncontrado.getPartidas()));

        return classificacao;
    }
}