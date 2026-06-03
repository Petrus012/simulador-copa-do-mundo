package br.ufla.copa.core.contracts;

import br.ufla.copa.core.contracts.RegraDePontuacaoDePalpite;

/**
 * R5 — Apostou em empate, mas não foi (4 pontos)
 * Concede pontos se o analista palpitou em empate,
 * mas a partida terminou com um vencedor.
 * É uma regra de consolação por ter apostado "quase certo" no equilíbrio.
 */
public class RegraApostouEmpateNaoFoi implements RegraDePontuacaoDePalpite {

    @Override
    public String getIdentificador() {
        return "R5";
    }

    @Override
    public String getDescricao() {
        return "Apostou em empate, mas a partida teve vencedor";
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        boolean palpitouEmpate = (palpiteGolsTimeA == palpiteGolsTimeB);
        boolean houvePlacar = (oficialGolsTimeA != oficialGolsTimeB);

        if (palpitouEmpate && houvePlacar) {
            return 4;
        }
        return 0;
    }
}