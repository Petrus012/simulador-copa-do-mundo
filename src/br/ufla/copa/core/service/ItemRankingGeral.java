package br.ufla.copa.core.service;

// DTO de um analista no ranking: encapsula nome e pontuação total (H9)
// Implementa Comparable para ordenação decrescente por pontuação
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
        // Ordenação primária: pontuação decrescente
        int comparacaoPontos = Integer.compare(outro.pontuacaoTotal, this.pontuacaoTotal);

        // Desempate: ordem alfabética do nome
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
