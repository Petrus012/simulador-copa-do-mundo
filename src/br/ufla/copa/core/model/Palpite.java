package br.ufla.copa.core.model;

public class Palpite {
    private Partida partida;
    private int golsTimeA;
    private int golsTimeB;

    public Palpite(Partida partida, int golsTimeA, int golsTimeB) {
        this.partida = partida;
        setGols(golsTimeA, golsTimeB);
    }

    public Partida getPartida() {
        return partida;
    }

    public int getGolsTimeA() {
        return golsTimeA;
    }

    public int getGolsTimeB() {
        return golsTimeB;
    }

    public void setGols(int golsTimeA, int golsTimeB) {
        if (golsTimeA < 0 || golsTimeB < 0) {
            throw new IllegalArgumentException("O número de gols não pode ser negativo.");
        }
        this.golsTimeA = golsTimeA;
        this.golsTimeB = golsTimeB;
    }
}