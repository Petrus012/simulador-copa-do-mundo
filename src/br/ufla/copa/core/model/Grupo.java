package br.ufla.copa.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um grupo da fase de grupos da Copa do Mundo.
 * Agrega as seleções, partidas e calcula a classificação com desempate correto (H3).
 */
public class Grupo {
    private final char nome;
    private final List<Selecao> selecoes;
    private final List<Partida> partidas;
    private final List<EstatisticasSelecao> classificacao;

    public Grupo(char nome) {
        this.nome = nome;
        this.selecoes = new ArrayList<>();
        this.partidas = new ArrayList<>();
        this.classificacao = new ArrayList<>();
    }

    public char getNome() {
        return nome;
    }

    public List<Selecao> getSelecoes() {
        return selecoes;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    /**
     * Retorna a tabela de classificação calculada e ordenada.
     * Usa resultados oficiais quando disponíveis; palpites caso contrário (H3/H4).
     */
    public List<EstatisticasSelecao> getClassificacao() {
        computarClassificacao();
        classificacao.sort(new ClassificacaoComparator());
        return classificacao;
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

    /**
     * Reconstrói a lista de estatísticas do zero a cada chamada,
     * garantindo que mudanças nos palpites ou resultados sejam refletidas.
     * Registra tanto as estatísticas gerais quanto as de confronto direto,
     * necessárias para o desempate correto (critérios a, b, c do enunciado H3).
     */
    private void computarClassificacao() {
        classificacao.clear();
        for (Selecao s : selecoes) {
            classificacao.add(new EstatisticasSelecao(s));
        }

        for (Partida p : partidas) {
            EstatisticasSelecao casa = buscarEstatisticas(p.getTimeDaCasa());
            EstatisticasSelecao visitante = buscarEstatisticas(p.getTimeVisitante());

            if (casa == null || visitante == null) continue;

            int golsCasa;
            int golsVisitante;
            boolean temDados;

            if (p.getStatus() == StatusPartida.FINALIZADA) {
                golsCasa = p.getGolsTimeCasaOficial();
                golsVisitante = p.getGolsTimeVisitanteOficial();
                temDados = true;
            } else if (p.temPalpite()) {
                golsCasa = p.getPalpite().getGolsTimeCasa();
                golsVisitante = p.getPalpite().getGolsTimeVisitante();
                temDados = true;
            } else {
                temDados = false;
                golsCasa = 0;
                golsVisitante = 0;
            }

            if (temDados) {
                // Estatísticas gerais do grupo
                casa.registrarResultado(golsCasa, golsVisitante);
                visitante.registrarResultado(golsVisitante, golsCasa);

                // Confronto direto — necessário para o desempate correto (H3)
                casa.registrarConfrontoDirecto(p.getTimeVisitante(), golsCasa, golsVisitante);
                visitante.registrarConfrontoDirecto(p.getTimeDaCasa(), golsVisitante, golsCasa);
            }
        }
    }

    private EstatisticasSelecao buscarEstatisticas(Selecao selecao) {
        for (EstatisticasSelecao es : classificacao) {
            if (es.getSelecao().equals(selecao)) {
                return es;
            }
        }
        return null;
    }
}