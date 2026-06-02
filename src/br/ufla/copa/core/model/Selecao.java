package br.ufla.copa.core.model;

import java.util.Objects;

public class Selecao {
    private String nome;

    public Selecao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        Selecao outraSelecao = (Selecao) obj;
        return Objects.equals(this.nome, outraSelecao.getNome());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}