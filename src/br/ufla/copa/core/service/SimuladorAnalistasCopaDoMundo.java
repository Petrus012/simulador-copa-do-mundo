package br.ufla.copa.core.service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import br.ufla.copa.core.data.CarregadorDeDados;
import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;

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
}