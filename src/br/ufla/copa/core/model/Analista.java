package br.ufla.copa.core.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Perfil de analista com cofre isolado de palpites indexado pelo ID da partida (H8)
public class Analista implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    // chave = ID da partida | valor = palpite do analista
    private final Map<Integer, Palpite> palpites;

    public Analista(String nome) {
        this.nome = nome;
        this.palpites = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public Map<Integer, Palpite> getPalpites() {
        return Collections.unmodifiableMap(palpites);
    }

    public void registrarPalpite(int partidaId, Palpite palpite) {
        this.palpites.put(partidaId, palpite);
    }

    public Palpite getPalpitePara(int partidaId) {
        return this.palpites.get(partidaId);
    }

    @Override
    public String toString() {
        return nome + " (" + palpites.size() + " palpites guardados)";
    }
}
