package br.ufla.copa.core.contracts;

// Contrato para a regra de bônus pela classificação de um país na fase de grupos (H7)
public interface RegraDePontuacaoDeClassificacao {
    // Retorna os pontos pelo acerto da posição (1º=15, 2º=10, 3º=5, qualquer outro=0)
    int calcularPontosPorPosicao(int palpitesPosicao, int oficialPosicao);
}
