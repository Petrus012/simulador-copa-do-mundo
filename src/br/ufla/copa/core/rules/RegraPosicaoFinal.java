package br.ufla.copa.core.rules;

import br.ufla.copa.core.contracts.RegraDePontuacaoDeClassificacao;

/**
 * Regra de pontuação que bonifica o analista pelo acerto da posição exata
 * de uma seleção na classificação final do grupo (H07).
 * 1º lugar: 15 pontos | 2º lugar: 10 pontos | 3º lugar: 5 pontos.
 */
public class RegraPosicaoFinal implements RegraDePontuacaoDeClassificacao {

    @Override
    public int calcularPontosPorPosicao(int palpitesPosicao, int oficialPosicao) {
        // Se o analista errou a posição exata da seleção no grupo, ganha 0 pontos
        if (palpitesPosicao != oficialPosicao) {
            return 0;
        }

        // Se acertou, a pontuação é fixa baseada no edital (assumindo base 1: 1º, 2º, 3º)
        if (oficialPosicao == 1) {
            return 15;
        } else if (oficialPosicao == 2) {
            return 10;
        } else if (oficialPosicao == 3) {
            return 5;
        }

        // Se o analista acertou quem ficou em 4º lugar, o edital diz que ganha 0 pontos
        return 0;
    }
}