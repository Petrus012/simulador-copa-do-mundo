package br.ufla.copa.core.contracts;

/** 
 * Interface que define o contrato para uma regra de pontuação para a 
 * classificação de um país na fase de grupos
 */
public interface RegraDePontuacaoDeClassificacao {
    /**
     * Calcula a pontuação pela classificação de um país na fase de grupos
     * @param palpitePosicao posição do país de acordo com os palpites
     * @param oficialPosicao posição do país na classificação real
     * @return A pontuação obtida nesta regra.
     */
    int calcularPontosPorPosicao(int palpitesPosicao, int oficialPosicao);
}
