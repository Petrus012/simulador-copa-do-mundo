package br.ufla.copa.core.model;

// Partida da fase de grupos com seu resultado oficial, palpite e status
public class Partida {
    private final int id;
    private final Selecao timeDaCasa;
    private final Selecao timeVisitante;

    private int golsTimeCasaOficial;
    private int golsTimeVisitanteOficial;

    private StatusPartida status;
    private Palpite palpite;

    public Partida(int id, Selecao timeDaCasa, Selecao timeVisitante) {
        this.id = id;
        this.timeDaCasa = timeDaCasa;
        this.timeVisitante = timeVisitante;
        this.status = StatusPartida.AGENDADA;
    }

    // Registra o placar oficial e muda o status para FINALIZADA
    public void setResultadoOficial(int golsCasa, int golsVisitante) {
        if (golsCasa < 0 || golsVisitante < 0) {
            return;
        }

        this.golsTimeCasaOficial = golsCasa;
        this.golsTimeVisitanteOficial = golsVisitante;
        this.status = StatusPartida.FINALIZADA;
    }

    // Rejeita palpites com gols negativos
    public void setPalpite(Palpite palpite) {
    if (palpite != null && (palpite.getGolsTimeCasa() < 0 || palpite.getGolsTimeVisitante() < 0)) {
        return;
    }
    this.palpite = palpite;
    }

    // Desfaz o resultado oficial, voltando a partida para AGENDADA (usado no snapshot de sync)
    public void limparResultadoOficial() {
        this.golsTimeCasaOficial = 0;
        this.golsTimeVisitanteOficial = 0;
        this.status = StatusPartida.AGENDADA;
    }

    public void limparPalpite() {
        this.palpite = null;
    }

    public Palpite getPalpite() {
        return palpite;
    }

    public boolean temPalpite() {
        return palpite != null;
    }

    public int getId() {
        return id;
    }

    public Selecao getTimeDaCasa() {
        return timeDaCasa;
    }

    public Selecao getTimeVisitante() {
        return timeVisitante;
    }

    public int getGolsTimeCasaOficial() {
        return golsTimeCasaOficial;
    }

    public int getGolsTimeVisitanteOficial() {
        return golsTimeVisitanteOficial;
    }

    public StatusPartida getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String placar = (status == StatusPartida.FINALIZADA)
                ? " " + golsTimeCasaOficial + " x " + golsTimeVisitanteOficial + " "
                : " x ";

        String infoPalpite = temPalpite()
                ? " [Palpite: " + palpite.getGolsTimeCasa() + " x " + palpite.getGolsTimeVisitante() + "]"
                : "";

        return "Partida " + id + ": " +
               timeDaCasa.getNome() + placar + timeVisitante.getNome() +
               " | Status: " + status + infoPalpite;
    }
}
