package br.ufla.copa.core.model;

public class Partida {
    private int id;
    private Selecao timeA;
    private Selecao timeB;
    private String grupo;
    private String data;
    private String hora;
    private boolean finalizada;

    public Partida(int id, Selecao timeA, Selecao timeB, String grupo, String data, String hora) {
        this.id = id;
        this.timeA = timeA;
        this.timeB = timeB;
        this.grupo = grupo;
        this.data = data;
        this.hora = hora;
        this.finalizada = false;
    }

    public int getId() {
        return id;
    }

    public Selecao getTimeA() {
        return timeA;
    }

    public Selecao getTimeB() {
        return timeB;
    }

    public String getGrupo() {
        return grupo;
    }
    
    public void finalizarPartida() {
        this.finalizada = true;
    }
    
    public boolean isFinalizada() {
        return finalizada;
    }

    @Override
    public String toString() {
        return String.format("Jogo %d [%s] %s x %s (%s às %s)", 
                id, grupo, timeA.getNome(), timeB.getNome(), data, hora);
    }
}