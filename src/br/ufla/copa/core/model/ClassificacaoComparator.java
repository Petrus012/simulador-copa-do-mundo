package br.ufla.copa.core.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

// Ordena a classificação do grupo pelos critérios oficiais da Copa (H3):
public class ClassificacaoComparator implements Comparator<EstatisticasSelecao> {

    private static final Collator COLLATOR = Collator.getInstance(Locale.of("pt", "BR"));

    @Override
    public int compare(EstatisticasSelecao t1, EstatisticasSelecao t2) {
        // 1. BLINDAGEM DEFENSIVA: Trata objetos nulos e iguais logo na porta
        if (t1 == t2) {
            return 0;
        }
        if (t1 == null) {
            return 1;
        }
        if (t2 == null) {
            return -1;
        }

        int compPontos = Integer.compare(t2.getPontos(), t1.getPontos());
        if (compPontos != 0) {
            return compPontos;
        }

        Selecao sel1 = t1.getSelecao();
        Selecao sel2 = t2.getSelecao();

        // 2. Proteção caso as entidades de Seleção interna venham nulas
        if (sel1 == null && sel2 == null) {
            return 0;
        }
        if (sel1 == null) {
            return 1;
        }
        if (sel2 == null) {
            return -1;
        }

        
        // a) Pontos no confronto direto
        int pontosT1Direto = t1.getPontosConfrontoDirecto(sel2);
        int pontosT2Direto = t2.getPontosConfrontoDirecto(sel1);
        int compConfrontoPontos = Integer.compare(pontosT2Direto, pontosT1Direto);
        if (compConfrontoPontos != 0) {
            return compConfrontoPontos;
        }

        // b) Saldo de gols no confronto direto
        int saldoT1Direto = t1.getSaldoConfrontoDirecto(sel2);
        int saldoT2Direto = t2.getSaldoConfrontoDirecto(sel1);
        int compConfrontoSaldo = Integer.compare(saldoT2Direto, saldoT1Direto);
        if (compConfrontoSaldo != 0) {
            return compConfrontoSaldo;
        }

        // c) Gols marcados no confronto direto
        int golsT1Direto = t1.getGolsProConfrontoDirecto(sel2);
        int golsT2Direto = t2.getGolsProConfrontoDirecto(sel1);
        int compConfrontoGols = Integer.compare(golsT2Direto, golsT1Direto);
        if (compConfrontoGols != 0) {
            return compConfrontoGols;
        }

        // d) Saldo de gols em todas as partidas do grupo
        int compSaldo = Integer.compare(t2.getSaldoGols(), t1.getSaldoGols());
        if (compSaldo != 0) {
            return compSaldo;
        }

        // e) Gols marcados em todas as partidas do grupo
        int compGolsPro = Integer.compare(t2.getGolsPro(), t1.getGolsPro());
        if (compGolsPro != 0) {
            return compGolsPro;
        }

        // f) Ordem alfabética estável (com proteção caso o método getNome retorne nulo)
        String nome1 = sel1.getNome() != null ? sel1.getNome() : "";
        String nome2 = sel2.getNome() != null ? sel2.getNome() : "";
        return COLLATOR.compare(nome1, nome2);
    }
}
