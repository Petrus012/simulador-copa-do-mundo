package br.ufla.copa.core.model;

import java.io.Serializable;

public class Palpite implements Serializable {
    private static final long serialVersionUID = 1L; // Blindagem de versão do arquivo binário

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