package br.ufla.copa.core.contracts;

/** 
 * Interface que define o contrato para uma regra de pontuação de um
 * palpite em uma partida. 
 */
public interface RegraDePontuacaoDePalpite {
    /**
     * @return o identificador da regra (ex.: "R1")
     */
    String getIdentificador();

    /** 
     * Retorna uma descrição da regra de pontuação:
     * Ex.: "Acertou o saldo de gols do vencedor"
     * @return a descrição da pontuação
     */ 
    String getDescricao();

    /** 
     * Calcula a pontuação de acordo com a regra
     * @param palpiteGolsTimeA Gols do time A no palpite 
     * @param palpiteGolsTimeB Gols do time B no palpite 
     * @param oficialGolsTimeA Gols do time A no resultado real 
     * @param oficialGolsTimeB Gols do time B no resultado real 
     * @return A pontuação obtida nesta regra específica.
     */
    int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB, 
        int oficialGolsTimeA, int oficialGolsTimeB);
}
