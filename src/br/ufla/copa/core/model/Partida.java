package br.ufla.copa.core.model;

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

    public void setResultadoOficial(int golsCasa, int golsVisitante) {
        if (golsCasa < 0 || golsVisitante < 0) {
            return;
        }
        
        this.golsTimeCasaOficial = golsCasa;
        this.golsTimeVisitanteOficial = golsVisitante;
        this.status = StatusPartida.FINALIZADA;
    }

    public void setPalpite(Palpite palpite) {
        this.palpite = palpite; 
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