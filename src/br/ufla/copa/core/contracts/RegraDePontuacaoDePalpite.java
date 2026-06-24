package br.ufla.copa.core.contracts;

// Contrato para cada regra de pontuação de um palpite em uma partida (R1–R6)
public interface RegraDePontuacaoDePalpite {
    // Identificador curto da regra (ex.: "R1")
    String getIdentificador();

    // Descrição legível do critério avaliado
    String getDescricao();

    // Calcula e retorna os pontos obtidos com base nos gols do palpite e do resultado oficial
    int calcular(int palpiteGolsTimeA, int palpiteGolsTimeB,
        int oficialGolsTimeA, int oficialGolsTimeB);
}
