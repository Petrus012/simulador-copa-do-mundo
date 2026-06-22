package br.ufla.copa.core.rules;

import br.ufla.copa.core.contracts.RegraDePontuacaoDePalpite;

/**
 * R6: Pontos se o analista apostou em empate e acertou o placar exato (10 pontos).
 */
public class RegraPlacarExatoEmpate implements RegraDePontuacaoDePalpite {

    @Override
    public int calcular(int palpiteCasa, int palpiteVisitante, int oficialCasa, int oficialVisitante) {
        // 1. O jogo real terminou empatado?
        if (oficialCasa != oficialVisitante) {
            return 0;
        }

        // 2. O palpite cravou exatamente o placar do empate? (ex: apostou 1x1 e o jogo foi 1x1)
        if (palpiteCasa == oficialCasa && palpiteVisitante == oficialVisitante) {
            return 10;
        }

        return 0;
    }

    @Override
    public String getIdentificador() {
        return "R6";
    }

    @Override
    public String getDescricao() {
        return "Placar exato de um empate";
    }
}