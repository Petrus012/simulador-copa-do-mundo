package br.ufla.copa.core.rules;

// R5 — Apostou em empate, mas não foi (4 pontos)
// Regra de consolação: pontua se o analista palpitou em empate mas houve vencedor
public class RegraApostouEmpateNaoFoi extends RegraAbstrataDePalpite {

    public RegraApostouEmpateNaoFoi() {
        super("R5", "Apostou em empate, mas a partida teve vencedor");
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
