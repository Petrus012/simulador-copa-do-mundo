package br.ufla.copa.core.model;

import java.io.Serializable;

// Palpite imutável de uma partida — serializado junto com o Analista (H8)
public class Palpite implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int golsTimeCasa;
    private final int golsTimeVisitante;

    public Palpite(int golsTimeCasa, int golsTimeVisitante) {
        this.golsTimeCasa = golsTimeCasa;
        this.golsTimeVisitante = golsTimeVisitante;
    }

    public int getGolsTimeCasa() {
        return golsTimeCasa;
    }

    public int getGolsTimeVisitante() {
        return golsTimeVisitante;
    }
}
