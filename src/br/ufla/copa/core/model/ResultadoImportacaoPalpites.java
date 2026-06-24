package br.ufla.copa.core.model;

import java.util.Collections;
import java.util.Map;

// Resultado detalhado de uma importação de palpites via CSV (H2)
public class ResultadoImportacaoPalpites {

    private final int registrados;
    private final int parciais;
    private final Map<Integer, Palpite> palpitesLidos;
    private int bloqueadosPorResultado;
    private int bloqueadosPorSnapshot;

    public ResultadoImportacaoPalpites(int registrados, int parciais, Map<Integer, Palpite> palpitesLidos) {
        this.registrados = registrados;
        this.parciais = parciais;
        this.palpitesLidos = palpitesLidos;
    }

    public int getRegistrados() {
        return registrados;
    }

    // Linhas com exatamente um dos campos de gol preenchido — palpite incompleto
    public int getParciais() {
        return parciais;
    }

    // Palpites válidos ignorados porque a partida já tem resultado oficial
    public int getBloqueadosPorResultado() {
        return bloqueadosPorResultado;
    }

    public void setBloqueadosPorResultado(int bloqueadosPorResultado) {
        this.bloqueadosPorResultado = bloqueadosPorResultado;
    }

    // Palpites válidos ignorados porque o jogo já ocorreu antes da sync web
    public int getBloqueadosPorSnapshot() {
        return bloqueadosPorSnapshot;
    }

    public void setBloqueadosPorSnapshot(int bloqueadosPorSnapshot) {
        this.bloqueadosPorSnapshot = bloqueadosPorSnapshot;
    }

    // Todos os palpites lidos do CSV, incluindo os de partidas já finalizadas
    public Map<Integer, Palpite> getPalpitesLidos() {
        return Collections.unmodifiableMap(palpitesLidos);
    }
}
