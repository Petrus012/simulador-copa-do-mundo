package br.ufla.copa.core.rules;

// R3 — Gols do Vencedor (8 pontos)
// Pontua se o analista acertou o número exato de gols do time vencedor;
// em caso de empate (sem vencedor) retorna 0
public class RegraGolsDoVencedor extends RegraAbstrataDePalpite {

    public RegraGolsDoVencedor() {
        super("R3", "Acertou os gols do vencedor");
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        // Empate: não há vencedor, regra não pontua
        if (oficialGolsTimeA == oficialGolsTimeB) {
            return 0;
        }

        int golsVencedorOficial;
        int golsVencedorPalpite;

        if (oficialGolsTimeA > oficialGolsTimeB) {
            // Time A venceu — palpite também deve ter previsto vitória de A
            if (palpiteGolsTimeA <= palpiteGolsTimeB) return 0;
            golsVencedorOficial = oficialGolsTimeA;
            golsVencedorPalpite = palpiteGolsTimeA;
        } else {
            // Time B venceu — palpite também deve ter previsto vitória de B
            if (palpiteGolsTimeB <= palpiteGolsTimeA) return 0;
            golsVencedorOficial = oficialGolsTimeB;
            golsVencedorPalpite = palpiteGolsTimeB;
        }

        if (golsVencedorPalpite == golsVencedorOficial) {
            return 8;
        }
        return 0;
    }
}
