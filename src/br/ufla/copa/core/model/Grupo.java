package br.ufla.copa.core.model;

import java.util.ArrayList;
import java.util.List;

public class Grupo {
    private String nome;
    private List<Partida> partidas;
    private List<Selecao> selecoes;

    public Grupo(String nome) {
        this.nome = nome;
        this.partidas = new ArrayList<>();
        this.selecoes = new ArrayList<>();
    }

    public void adicionarPartida(Partida partida) {
        this.partidas.add(partida);
        
        if (!selecoes.contains(partida.getTimeA())) {
            selecoes.add(partida.getTimeA());
        }
        
        if (!selecoes.contains(partida.getTimeB())) {
            selecoes.add(partida.getTimeB());
        }
    }

    public String getNome() {
        return nome;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public List<Selecao> getSelecoes() {
        return selecoes;
    }
}