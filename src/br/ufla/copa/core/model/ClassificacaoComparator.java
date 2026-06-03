package br.ufla.copa.core.model;

import java.util.Comparator;

/**
 * Comparador para ordenar a classificação do grupo seguindo os critérios
 * oficiais do regulamento da Copa do Mundo (H3):
 *
 * a) Maior número de pontos no confronto direto entre as equipes empatadas
 * b) Melhor saldo de gols no confronto direto
 * c) Maior número de gols marcados no confronto direto
 * d) Melhor saldo de gols considerando todas as partidas do grupo
 * e) Maior número de gols marcados em todas as partidas do grupo
 * f) Ordem alfabética (critério final por falta de dados de cartões/ranking FIFA)
 */
public class ClassificacaoComparator implements Comparator<EstatisticasSelecao> {

    @Override
    public int compare(EstatisticasSelecao t1, EstatisticasSelecao t2) {
        // 1º critério: pontos gerais no grupo
        int compPontos = Integer.compare(t2.getPontos(), t1.getPontos());
        if (compPontos != 0) return compPontos;

        // Critérios a, b, c: confronto direto entre as duas seleções empatadas
        Selecao sel1 = t1.getSelecao();
        Selecao sel2 = t2.getSelecao();

        // a) Pontos no confronto direto entre si
        int pontosT1Direto = t1.getPontosConfrontoDirecto(sel2);
        int pontosT2Direto = t2.getPontosConfrontoDirecto(sel1);
        int compConfrontoPontos = Integer.compare(pontosT2Direto, pontosT1Direto);
        if (compConfrontoPontos != 0) return compConfrontoPontos;

        // b) Saldo de gols no confronto direto
        int saldoT1Direto = t1.getSaldoConfrontoDirecto(sel2);
        int saldoT2Direto = t2.getSaldoConfrontoDirecto(sel1);
        int compConfrontoSaldo = Integer.compare(saldoT2Direto, saldoT1Direto);
        if (compConfrontoSaldo != 0) return compConfrontoSaldo;

        // c) Gols marcados no confronto direto
        int golsT1Direto = t1.getGolsProConfrontoDirecto(sel2);
        int golsT2Direto = t2.getGolsProConfrontoDirecto(sel1);
        int compConfrontoGols = Integer.compare(golsT2Direto, golsT1Direto);
        if (compConfrontoGols != 0) return compConfrontoGols;

        // d) Saldo de gols em todas as partidas do grupo
        int compSaldo = Integer.compare(t2.getSaldoGols(), t1.getSaldoGols());
        if (compSaldo != 0) return compSaldo;

        // e) Gols marcados em todas as partidas do grupo
        int compGolsPro = Integer.compare(t2.getGolsPro(), t1.getGolsPro());
        if (compGolsPro != 0) return compGolsPro;

        // f) Ordem alfabética
        return t1.getSelecao().getNome().compareTo(t2.getSelecao().getNome());
    }
}