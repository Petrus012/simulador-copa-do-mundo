package br.ufla.copa.core.rules;

// R1 — Vencedor ou Empate (10 pontos)
// Pontua se o analista acertou qual time venceu ou que a partida terminaria empatada
public class RegraVencedorOuEmpate extends RegraAbstrataDePalpite {

    public RegraVencedorOuEmpate() {
        super("R1", "Acertou o vencedor ou que seria empate");
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        int resultadoPalpite = Integer.compare(palpiteGolsTimeA, palpiteGolsTimeB);
        int resultadoOficial = Integer.compare(oficialGolsTimeA, oficialGolsTimeB);

        if (resultadoPalpite == resultadoOficial) {
            return 10;
        }
        return 0;
    }
}
