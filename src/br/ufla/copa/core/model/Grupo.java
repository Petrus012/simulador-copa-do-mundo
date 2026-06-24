package br.ufla.copa.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa um grupo da fase de grupos da Copa do Mundo.
 * Agrega as seleções, partidas e calcula a classificação com desempate correto (H3).
 */
public class Grupo {
    private final char nome;
    private final List<Selecao> selecoes;
    private final List<Partida> partidas;

    public Grupo(char nome) {
        this.nome = nome;
        this.selecoes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public char getNome() {
        return nome;
    }

    public List<Selecao> getSelecoes() {
        return Collections.unmodifiableList(selecoes);
    }

    public List<Partida> getPartidas() {
        return Collections.unmodifiableList(partidas);
    }

    /**
     * Retorna a tabela de classificação calculada e ordenada.
     * Usa resultados oficiais quando disponíveis. Ignora palpites. (H3/H4).
     * Cada chamada retorna uma nova lista, sem risco de referências externas desatualizadas.
     */
    public List<EstatisticasSelecao> getClassificacao() {
        List<EstatisticasSelecao> resultado = computarClassificacao();
        resultado.sort(new ClassificacaoComparator());
        return resultado;
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
     * garantindo que mudanças nos resultados oficiais sejam refletidas.
     * Registra tanto as estatísticas gerais quanto as de confronto direto,
     * necessárias para o desempate correto (critérios a, b, c do enunciado H3).
     */
    private List<EstatisticasSelecao> computarClassificacao() {
        List<EstatisticasSelecao> stats = new ArrayList<>();
        for (Selecao s : selecoes) {
            stats.add(new EstatisticasSelecao(s));
        }

        for (Partida p : partidas) {
            EstatisticasSelecao casa = buscarEstatisticasNaLista(stats, p.getTimeDaCasa());
            EstatisticasSelecao visitante = buscarEstatisticasNaLista(stats, p.getTimeVisitante());

            if (casa == null || visitante == null) continue;

            int golsCasa;
            int golsVisitante;
            boolean temDados;

            if (p.getStatus() == StatusPartida.FINALIZADA) {
                golsCasa = p.getGolsTimeCasaOficial();
                golsVisitante = p.getGolsTimeVisitanteOficial();
                temDados = true;
            } else {
                temDados = false;
                golsCasa = 0;
                golsVisitante = 0;
            }

            if (temDados) {
                casa.registrarResultado(golsCasa, golsVisitante);
                visitante.registrarResultado(golsVisitante, golsCasa);

                // Confronto direto — necessário para o desempate correto (H3)
                casa.registrarConfrontoDirecto(p.getTimeVisitante(), golsCasa, golsVisitante);
                visitante.registrarConfrontoDirecto(p.getTimeDaCasa(), golsVisitante, golsCasa);
            }
        }
        return stats;
    }

    /**
     * H07: Retorna a classificação do grupo calculada ESTRITAMENTE pelos palpites,
     * ignorando os placares oficiais mesmo que a partida já esteja finalizada.
     * Instancia uma lista temporária para não poluir a classificação oficial do grupo.
     */
    public List<EstatisticasSelecao> getClassificacaoPelosPalpites() {
        List<EstatisticasSelecao> statsPalpite = new ArrayList<>();
        for (Selecao s : selecoes) {
            statsPalpite.add(new EstatisticasSelecao(s));
        }

        for (Partida p : partidas) {
            if (!p.temPalpite()) continue;

            EstatisticasSelecao casa = buscarEstatisticasNaLista(statsPalpite, p.getTimeDaCasa());
            EstatisticasSelecao visitante = buscarEstatisticasNaLista(statsPalpite, p.getTimeVisitante());

            if (casa == null || visitante == null) continue;

            int golsCasa = p.getPalpite().getGolsTimeCasa();
            int golsVisitante = p.getPalpite().getGolsTimeVisitante();

            casa.registrarResultado(golsCasa, golsVisitante);
            visitante.registrarResultado(golsVisitante, golsCasa);

            casa.registrarConfrontoDirecto(p.getTimeVisitante(), golsCasa, golsVisitante);
            visitante.registrarConfrontoDirecto(p.getTimeDaCasa(), golsVisitante, golsCasa);
        }

        statsPalpite.sort(new ClassificacaoComparator());
        return statsPalpite;
    }

    private EstatisticasSelecao buscarEstatisticasNaLista(List<EstatisticasSelecao> lista, Selecao selecao) {
        for (EstatisticasSelecao es : lista) {
            if (es.getSelecao().equals(selecao)) return es;
        }
        return null;
    }
}