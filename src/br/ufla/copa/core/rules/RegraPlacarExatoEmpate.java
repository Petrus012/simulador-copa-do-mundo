package br.ufla.copa.core.rules;

/**
 * R6 — Placar exato de empate (10 pontos).
 * Concede pontos se o analista apostou num empate e cravou o placar exato.
 */
public class RegraPlacarExatoEmpate extends RegraAbstrataDePalpite {

    public RegraPlacarExatoEmpate() {
        super("R6", "Placar exato de um empate");
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {
        if (oficialGolsTimeA != oficialGolsTimeB) {
            return 0;
        }
        if (palpiteGolsTimeA == oficialGolsTimeA && palpiteGolsTimeB == oficialGolsTimeB) {
            return 10;
        }
        return 0;
    }
}