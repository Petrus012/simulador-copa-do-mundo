package br.ufla.copa.core.rules;

// R2 — Saldo de Gols (5 pontos)
// Pontua se a diferença de gols com sinal (A−B) do palpite é igual à do resultado oficial
public class RegraSaldoDeGols extends RegraAbstrataDePalpite {

    public RegraSaldoDeGols() {
        super("R2", "Acertou o saldo de gols da partida");
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        // Sinal importa: 0x2 e 2x0 têm saldos opostos e não devem pontuar entre si
        int saldoPalpite = palpiteGolsTimeA - palpiteGolsTimeB;
        int saldoOficial = oficialGolsTimeA - oficialGolsTimeB;

        if (saldoPalpite == saldoOficial) {
            return 5;
        }
        return 0;
    }
}
