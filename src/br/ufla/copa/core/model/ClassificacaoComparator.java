package br.ufla.copa.core.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

// Ordena a classificação do grupo pelos critérios oficiais da Copa (H3):
// a) pontos no confronto direto  b) saldo no confronto direto
// c) gols no confronto direto    d) saldo geral  e) gols geral  f) ordem alfabética
public class ClassificacaoComparator implements Comparator<EstatisticasSelecao> {

    // Collator pt-BR garante ordenação correta de nomes com acentos no critério f
    private static final Collator COLLATOR = Collator.getInstance(new Locale("pt", "BR"));

    @Override
    public int compare(EstatisticasSelecao t1, EstatisticasSelecao t2) {
        // Pontos gerais no grupo
        int compPontos = Integer.compare(t2.getPontos(), t1.getPontos());
        if (compPontos != 0) return compPontos;

        Selecao sel1 = t1.getSelecao();
        Selecao sel2 = t2.getSelecao();

        // a) Pontos no confronto direto
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

        // f) Ordem alfabética com tratamento correto de acentos
        return COLLATOR.compare(t1.getSelecao().getNome(), t2.getSelecao().getNome());
    }
}
