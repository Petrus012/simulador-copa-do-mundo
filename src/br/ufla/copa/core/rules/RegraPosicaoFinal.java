package br.ufla.copa.core.rules;

import br.ufla.copa.core.contracts.RegraDePontuacaoDeClassificacao;

// Bônus pela posição exata de uma seleção na classificação final do grupo (H7):
// 1º lugar = 15 pts | 2º lugar = 10 pts | 3º lugar = 5 pts | 4º lugar = 0 pts
public class RegraPosicaoFinal implements RegraDePontuacaoDeClassificacao {

    @Override
    public int calcularPontosPorPosicao(int palpitesPosicao, int oficialPosicao) {
        if (palpitesPosicao != oficialPosicao) {
            return 0;
        }

        if (oficialPosicao == 1) {
            return 15;
        } else if (oficialPosicao == 2) {
            return 10;
        } else if (oficialPosicao == 3) {
            return 5;
        }

        return 0;
    }
}
