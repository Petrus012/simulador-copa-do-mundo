package br.ufla.copa.core.service;

import br.ufla.copa.core.model.Partida;

import java.util.ArrayList;
import java.util.List;

/**
 * Agrega o resultado detalhado da pontuação de um palpite em uma partida.
 * Mantém a pontuação obtida em cada regra individualmente (H5)
 * e o total acumulado.
 */
public class ResultadoPontuacao {

    private final Partida partida;

    // Listas paralelas: identificador da regra, descrição e pontos obtidos
    private final List<String> identificadores;
    private final List<String> descricoes;
    private final List<Integer> pontosObtidos;

    private int totalPontos;

    public ResultadoPontuacao(Partida partida) {
        this.partida = partida;
        this.identificadores = new ArrayList<>();
        this.descricoes = new ArrayList<>();
        this.pontosObtidos = new ArrayList<>();
        this.totalPontos = 0;
    }

    /**
     * Registra o resultado de uma regra. Regras com 0 pontos também são
     * registradas para permitir exibição completa ao usuário.
     */
    public void adicionarRegra(String identificador, String descricao, int pontos) {
        identificadores.add(identificador);
        descricoes.add(descricao);
        pontosObtidos.add(pontos);
        totalPontos += pontos;
    }

    public Partida getPartida() {
        return partida;
    }

    public int getTotalPontos() {
        return totalPontos;
    }

    public int getQuantidadeRegras() {
        return identificadores.size();
    }

    public String getIdentificador(int indice) {
        return identificadores.get(indice);
    }

    public String getDescricao(int indice) {
        return descricoes.get(indice);
    }

    public int getPontosObtidos(int indice) {
        return pontosObtidos.get(indice);
    }
}