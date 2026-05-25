package br.ufla.copa.core.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Partida {
    private int id;
    private LocalDate data;
    private LocalTime hora;
    private Selecao timeDaCasa;
    private Selecao timeVisitante;
    private Estadio estadio;
    private Grupo grupo;
    private int rodada;
    private String fase;
    private int golsTimeDaCasa;
    private int golsTimeVisitante;
    private StatusPartida status;

    public Partida(Selecao timeDaCasa, Selecao timeVisitante) {
        this(0, null, null, timeDaCasa, timeVisitante, null, null, 0, "");
    }

    public Partida(int id, LocalDate data, LocalTime hora, Selecao timeDaCasa, Selecao timeVisitante, Estadio estadio,
            Grupo grupo, int rodada, String fase) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.timeDaCasa = timeDaCasa;
        this.timeVisitante = timeVisitante;
        this.estadio = estadio;
        this.grupo = grupo;
        this.rodada = rodada;
        this.fase = fase;
        golsTimeDaCasa = 0;
        golsTimeVisitante = 0;
        this.status = StatusPartida.AGENDADA;
    }

    public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Selecao getTimeDaCasa() {
        return timeDaCasa;
    }

    public Selecao getTimeVisitante() {
        return timeVisitante;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public int getGolsTimeDaCasa() {
        return golsTimeDaCasa;
    }

    public int getGolsTimeVisitante() {
        return golsTimeVisitante;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public Estadio getEstadio() {
        return estadio;
    }

    public int getRodada() {
        return rodada;
    }

    public String getFase() {
        return fase;
    }

    public void iniciarPartida() {
        if (status == StatusPartida.AGENDADA) {
            status = StatusPartida.EM_ANDAMENTO;
        }
    }

    public void finalizarPartida() {
        if (status == StatusPartida.EM_ANDAMENTO) {
            status = StatusPartida.FINALIZADA;
        }
    }

    public void registrarGol(Selecao time) {
        if (status == StatusPartida.EM_ANDAMENTO) {
            if (time.equals(timeDaCasa)) {
                golsTimeDaCasa++;
            } else if (time.equals(timeVisitante)) {
                golsTimeVisitante++;
            }
        }
    }
}
