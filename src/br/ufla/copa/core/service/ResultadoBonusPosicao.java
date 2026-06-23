package br.ufla.copa.core.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Agrega o resultado do bônus de posição final de um grupo (H7).
 * Armazena, para cada posição bonificável (1º, 2º, 3º), o nome da
 * seleção, a posição prevista pelo analista e os pontos obtidos.
 */
public class ResultadoBonusPosicao {

    private final char nomeGrupo;

    // Listas paralelas: posição oficial (1-based), nome da seleção no ranking
    // oficial, posição que o analista previu para essa seleção, pontos ganhos.
    private final List<Integer> posicoesOficiais;
    private final List<String>  nomesSelecoesOficiais;
    private final List<Integer> posicoesNoPalpite;
    private final List<Integer> pontosPorPosicao;

    private int totalPontos;

    public ResultadoBonusPosicao(char nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
        this.posicoesOficiais      = new ArrayList<>();
        this.nomesSelecoesOficiais = new ArrayList<>();
        this.posicoesNoPalpite     = new ArrayList<>();
        this.pontosPorPosicao      = new ArrayList<>();
        this.totalPontos = 0;
    }

    /**
     * Registra o resultado de uma posição bonificável.
     *
     * @param posicaoOficial  posição real do time no grupo (1, 2 ou 3)
     * @param nomeSelecao     nome da seleção nessa posição oficial
     * @param posicaoPalpite  posição em que o analista previu essa seleção
     * @param pontos          pontos obtidos pela regra de posição
     */
    public void adicionarPosicao(int posicaoOficial, String nomeSelecao,
                                 int posicaoPalpite, int pontos) {
        posicoesOficiais.add(posicaoOficial);
        nomesSelecoesOficiais.add(nomeSelecao);
        posicoesNoPalpite.add(posicaoPalpite);
        pontosPorPosicao.add(pontos);
        totalPontos += pontos;
    }

    public char getNomeGrupo() {
        return nomeGrupo;
    }

    public int getTotalPontos() {
        return totalPontos;
    }

    public int getQuantidadePosicoes() {
        return posicoesOficiais.size();
    }

    public int getPosicaoOficial(int indice) {
        return posicoesOficiais.get(indice);
    }

    public String getNomeSelecao(int indice) {
        return nomesSelecoesOficiais.get(indice);
    }

    public int getPosicaoNoPalpite(int indice) {
        return posicoesNoPalpite.get(indice);
    }

    public int getPontos(int indice) {
        return pontosPorPosicao.get(indice);
    }
}
