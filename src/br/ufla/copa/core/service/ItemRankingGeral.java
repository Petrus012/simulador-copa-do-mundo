package br.ufla.copa.core.service;

/**
 * DTO responsável por encapsular a pontuação de um analista (H09).
 * Implementa Comparable para permitir ordenação decrescente.
 */
public class ItemRankingGeral implements Comparable<ItemRankingGeral> {
    private final String nomeAnalista;
    private final int pontuacaoTotal;

    public ItemRankingGeral(String nomeAnalista, int pontuacaoTotal) {
        this.nomeAnalista = nomeAnalista;
        this.pontuacaoTotal = pontuacaoTotal;
    }

    public String getNomeAnalista() {
        return nomeAnalista;
    }

    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    @Override
    public int compareTo(ItemRankingGeral outro) {
        // Ordenação Primária: Pontuação decrescente (do maior para o menor)
        int comparacaoPontos = Integer.compare(outro.pontuacaoTotal, this.pontuacaoTotal);
        
        // Critério de desempate: Ordem alfabética do nome do analista
        if (comparacaoPontos == 0) {
            return this.nomeAnalista.compareToIgnoreCase(outro.nomeAnalista);
        }
        return comparacaoPontos;
    }

    @Override
    public String toString() {
        return nomeAnalista + " (" + pontuacaoTotal + " pts)";
    }
}