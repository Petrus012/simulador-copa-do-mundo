package br.ufla.copa.core.rules;

// R4 — Gols do Perdedor (2 pontos)
// Pontua se o analista acertou o número exato de gols do time perdedor;
// em caso de empate (sem perdedor) retorna 0
public class RegraGolsDoPerdedor extends RegraAbstrataDePalpite {

    public RegraGolsDoPerdedor() {
        super("R4", "Acertou os gols do perdedor");
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        // Empate: não há perdedor, regra não pontua
        if (oficialGolsTimeA == oficialGolsTimeB) {
            return 0;
        }

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
