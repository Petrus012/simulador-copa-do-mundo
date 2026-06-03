package br.ufla.copa.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<EstatisticasSelecao> calcularEstatisticasPelosPalpites() {
        Map<Selecao, EstatisticasSelecao> mapaEstatisticas = new HashMap<>();

        for (Selecao selecao : selecoes) {
            mapaEstatisticas.put(selecao, new EstatisticasSelecao(selecao));
        }

        for (Partida partida : partidas) {
            if (partida.getPalpite() != null) {
                Palpite palpite = partida.getPalpite();
                int golsTimeA = palpite.getGolsTimeA();
                int golsTimeB = palpite.getGolsTimeB();

                EstatisticasSelecao estTimeA = mapaEstatisticas.get(partida.getTimeA());
                EstatisticasSelecao estTimeB = mapaEstatisticas.get(partida.getTimeB());

                estTimeA.registrarResultado(golsTimeA, golsTimeB);
                estTimeB.registrarResultado(golsTimeB, golsTimeA);
            }
        }

        List<EstatisticasSelecao> resultados = new ArrayList<>();
        for (EstatisticasSelecao estatistica : mapaEstatisticas.values()) {
            resultados.add(estatistica);
        }

        return resultados;
    }

    public List<EstatisticasSelecao> calcularEstatisticasPelosResultadosOficiais() {
        Map<Selecao, EstatisticasSelecao> mapaEstatisticas = new HashMap<>();

        for (Selecao selecao : selecoes) {
            mapaEstatisticas.put(selecao, new EstatisticasSelecao(selecao));
        }

        for (Partida partida : partidas) {
            if (partida.isFinalizada() && partida.getGolsOficiaisTimeA() != null && partida.getGolsOficiaisTimeB() != null) {
                int golsTimeA = partida.getGolsOficiaisTimeA();
                int golsTimeB = partida.getGolsOficiaisTimeB();

                EstatisticasSelecao estTimeA = mapaEstatisticas.get(partida.getTimeA());
                EstatisticasSelecao estTimeB = mapaEstatisticas.get(partida.getTimeB());

                estTimeA.registrarResultado(golsTimeA, golsTimeB);
                estTimeB.registrarResultado(golsTimeB, golsTimeA);
            }
        }

        List<EstatisticasSelecao> resultados = new ArrayList<>();
        for (EstatisticasSelecao estatistica : mapaEstatisticas.values()) {
            resultados.add(estatistica);
        }

        return resultados;
    }
}