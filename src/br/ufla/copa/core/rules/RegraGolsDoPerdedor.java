package br.ufla.copa.core.rules;

import br.ufla.copa.core.contracts.RegraDePontuacaoDePalpite;

/**
 * R4 — Gols do Perdedor (2 pontos)
 * Concede pontos se o analista acertou o número exato de gols marcados
 * pela equipe perdedora. Em caso de empate, a regra não se aplica
 * (não há perdedor), retornando 0.
 */
public class RegraGolsDoPerdedor implements RegraDePontuacaoDePalpite {

    @Override
    public String getIdentificador() {
        return "R4";
    }

    @Override
    public String getDescricao() {
        return "Acertou os gols do perdedor";
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        // Empate: não há perdedor, regra não pontua
        if (oficialGolsTimeA == oficialGolsTimeB) {
            return 0;
        }

        // Determina quantos gols fez o perdedor no resultado oficial
        int golsPerdedorOficial;
        int golsPerdedorPalpite;

        if (oficialGolsTimeA > oficialGolsTimeB) {
            // Time A venceu, Time B perdeu — palpite também deve ter previsto vitória de A
            if (palpiteGolsTimeA <= palpiteGolsTimeB) return 0;
            golsPerdedorOficial = oficialGolsTimeB;
            golsPerdedorPalpite = palpiteGolsTimeB;
        } else {
            // Time B venceu, Time A perdeu — palpite também deve ter previsto vitória de B
            if (palpiteGolsTimeB <= palpiteGolsTimeA) return 0;
            golsPerdedorOficial = oficialGolsTimeA;
            golsPerdedorPalpite = palpiteGolsTimeA;
        }

        if (golsPerdedorPalpite == golsPerdedorOficial) {
            return 2;
        }
        return 0;
    }
}