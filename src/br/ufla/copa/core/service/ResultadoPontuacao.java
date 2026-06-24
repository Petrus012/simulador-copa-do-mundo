package br.ufla.copa.core.service;

import br.ufla.copa.core.model.Partida;

import java.util.ArrayList;
import java.util.List;

// Resultado detalhado da pontuação de um palpite: pontos por regra e total acumulado (H5)
public class ResultadoPontuacao {

    private final Partida partida;

    // Listas paralelas: identificador, descrição e pontos de cada regra aplicada
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

    // Registra o resultado de uma regra; regras com 0 pontos também são incluídas para exibição
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
