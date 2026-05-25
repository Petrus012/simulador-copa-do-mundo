package br.ufla.copa.core.model;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collections;

public class Grupo {
    private char id;
    private List<Selecao> selecoes;
    private List<Partida> partidas;

    public Grupo(char id) {
        this.id = id;
        this.selecoes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public char getId() {
        return id;
    }

    public List<Selecao> getSelecoes() {
        return Collections.unmodifiableList(selecoes);
    }

    public List<Partida> getPartidas() {
        return Collections.unmodifiableList(partidas);
    }

    public void adicionarSelecao(Selecao selecao) {
        if (!selecoes.contains(selecao)) {
            selecoes.add(selecao);
        }
    }

    public void adicionarPartida(Partida partida) {
        if (!partidas.contains(partida)) {
            partidas.add(partida);
        }
    }

    public String getSelecaoPorNome(String nome) {
        for (Selecao selecao : selecoes) {
            if (selecao.getNome().equals(nome)) {
                return selecao.getNome();
            }
        }
        return null;
    }

    public Partida getPartidaPorTimes(Selecao time1, Selecao time2) {
        for (Partida partida : partidas) {
            if ((partida.getTimeDaCasa().equals(time1) && partida.getTimeVisitante().equals(time2)) ||
                (partida.getTimeDaCasa().equals(time2) && partida.getTimeVisitante().equals(time1))) {
                return partida;
            }
        }
        return null;
    }

}
