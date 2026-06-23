package br.ufla.copa.core.contracts;

/**
 * R6 — Placar exato de um empate (10 pontos)
 * Concede pontos se o analista palpitou em empate e acertou o placar exato.
 * Diferencia quem acertou o placar exato (25 pts com R1+R2+R6) de quem
 * acertou apenas que seria empate, mas errou o placar (15 pts com R1+R2).
 */
public class RegraPlacarExatoDeEmpate implements RegraDePontuacaoDePalpite {

    @Override
    public String getIdentificador() {
        return "R6";
    }

    @Override
    public String getDescricao() {
        return "Acertou o placar exato de um empate";
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        boolean palpitouEmpate = (palpiteGolsTimeA == palpiteGolsTimeB);
        boolean foidEmpate = (oficialGolsTimeA == oficialGolsTimeB);

        if (palpitouEmpate && foidEmpate
                && palpiteGolsTimeA == oficialGolsTimeA) {
            return 10;
        }
        return 0;
    }
}
