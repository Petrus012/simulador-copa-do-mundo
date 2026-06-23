package br.ufla.copa.core.contracts;

/**
 * Regra de bônus pela posição final no grupo (H7).
 * Compara a posição prevista pelo analista com a posição oficial e
 * concede pontos caso acerte: 1º lugar = 15 pts, 2º = 10 pts, 3º = 5 pts.
 * O bônus só deve ser aplicado quando todas as partidas do grupo estiverem
 * finalizadas (verificação feita pelo chamador).
 */
public class RegraPosicaoFinalNoGrupo implements RegraDePontuacaoDeClassificacao {

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
