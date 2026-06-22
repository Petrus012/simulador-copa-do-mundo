package br.ufla.copa.core.model;

public class Palpite {
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