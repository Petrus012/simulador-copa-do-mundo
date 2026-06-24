package br.ufla.copa.core.model;

public class Estadio {
    private String nome;
    private String pais;
    private int capacidade;

    public Estadio(String nome, int capacidade) {
        this.nome = nome;
        this.pais = "";
        this.capacidade = capacidade;
    }

    public Estadio(String nome, String pais) {
        this.nome = nome;
        this.pais = pais;
        this.capacidade = 0;
    }

    public String getNome() {
        return nome;
    }

    public String getPais() {
        return pais;
    }

    public int getCapacidade() {
        return capacidade;
    }
}
