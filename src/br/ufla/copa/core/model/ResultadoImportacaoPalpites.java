package br.ufla.copa.core.model;

import java.util.Collections;
import java.util.Map;

/**
 * Resultado detalhado de uma importação de palpites via CSV (H2).
 * Carrega os palpites lidos, a contagem de registrados e a de parciais,
 * evitando que o CSV precise ser relido para salvar no cofre do analista.
 */
public class ResultadoImportacaoPalpites {

    private final int registrados;
    private final int parciais;
    private final Map<Integer, Palpite> palpitesLidos;

    public ResultadoImportacaoPalpites(int registrados, int parciais, Map<Integer, Palpite> palpitesLidos) {
        this.registrados = registrados;
        this.parciais = parciais;
        this.palpitesLidos = palpitesLidos;
    }

    public int getRegistrados() {
        return registrados;
    }

    /** Linhas com exatamente um dos campos de gol preenchido — palpite incompleto. */
    public int getParciais() {
        return parciais;
    }

    /** Todos os palpites válidos lidos do CSV, incluindo os de partidas já finalizadas. */
    public Map<Integer, Palpite> getPalpitesLidos() {
        return Collections.unmodifiableMap(palpitesLidos);
    }
}
