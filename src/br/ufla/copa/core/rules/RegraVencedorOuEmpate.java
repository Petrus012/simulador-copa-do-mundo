package br.ufla.copa.core.rules;

import br.ufla.copa.core.contracts.RegraDePontuacaoDePalpite;

/**
 * R1 — Vencedor ou Empate (10 pontos)
 * Concede pontos se o analista acertou qual time venceu,
 * ou se acertou que a partida terminaria empatada.
 */
public class RegraVencedorOuEmpate implements RegraDePontuacaoDePalpite {

    @Override
    public String getIdentificador() {
        return "R1";
    }

    @Override
    public String getDescricao() {
        return "Acertou o vencedor ou que seria empate";
    }

    @Override
    public int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
                        int oficialGolsTimeA, int oficialGolsTimeB) {

        // Determina o resultado do palpite
        int resultadoPalpite = Integer.compare(palpiteGolsTimeA, palpiteGolsTimeB);
        // Determina o resultado oficial
        int resultadoOficial = Integer.compare(oficialGolsTimeA, oficialGolsTimeB);

        // Acertou se ambos apontam para o mesmo lado (vitória A, empate ou vitória B)
        if (resultadoPalpite == resultadoOficial) {
            return 10;
        }
        return 0;
    }
}