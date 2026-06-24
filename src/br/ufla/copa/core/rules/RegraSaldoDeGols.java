package br.ufla.copa.core.rules;

/**
 * R2 — Saldo de Gols (5 pontos)
 * Concede pontos se o analista acertou a diferença de gols entre as equipes.
 * Em caso de empate, acerta se também palpitou em empate (diferença = 0).
 * Exemplos: palpite 3x1 e resultado 2x0 — ambos têm diferença de 2 → acertou.
 */
public class RegraSaldoDeGols extends RegraAbstrataDePalpite {

    public RegraSaldoDeGols() {
        super("R2", "Acertou o saldo de gols da partida");
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        // Compara o saldo com sinal: palpite 0x2 vs resultado 2x0 têm diferença oposta
        // e não devem pontuar, mesmo que o valor absoluto seja igual.
        int saldoPalpite = palpiteGolsTimeA - palpiteGolsTimeB;
        int saldoOficial = oficialGolsTimeA - oficialGolsTimeB;

        if (saldoPalpite == saldoOficial) {
            return 5;
        }
        return 0;
    }
}