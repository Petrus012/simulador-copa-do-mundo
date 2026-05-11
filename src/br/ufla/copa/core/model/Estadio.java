package br.ufla.copa.core.model;

public class Estadio {
    private String nome;
    private int capacidade;

    public Estadio(String nome, int capacidade) {
        this.nome = nome;
        this.capacidade = capacidade;
    }

    public String getNome() { 
        return nome; 
    }
    
    public int getCapacidade() { 
        return capacidade; 
    }
}
