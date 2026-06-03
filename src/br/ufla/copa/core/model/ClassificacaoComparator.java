package br.ufla.copa.core.model;

import java.util.Comparator;
import java.util.List;

public class ClassificacaoComparator implements Comparator<EstatisticasSelecao> {

    private List<Partida> partidasDoGrupo;

    public ClassificacaoComparator(List<Partida> partidasDoGrupo) {
        this.partidasDoGrupo = partidasDoGrupo;
    }

    @Override
    public int compare(EstatisticasSelecao e1, EstatisticasSelecao e2) {
        // 1. Maior número de pontos geral
        if (e1.getPontos() != e2.getPontos()) {
            return Integer.compare(e2.getPontos(), e1.getPontos());
        }

        // --- INÍCIO DO CRITÉRIO DE CONFRONTO DIRETO ---
        Partida confronto = null;
        for (int i = 0; i < partidasDoGrupo.size(); i++) {
            Partida p = partidasDoGrupo.get(i);
            if (p.getPalpite() != null) {
                Selecao timeA = p.getTimeA();
                Selecao timeB = p.getTimeB();
                
                if ((timeA.equals(e1.getSelecao()) && timeB.equals(e2.getSelecao())) ||
                    (timeB.equals(e1.getSelecao()) && timeA.equals(e2.getSelecao()))) {
                    confronto = p;
                    break;
                }
            }
        }

        if (confronto != null) {
            Palpite palpite = confronto.getPalpite();
            int golsE1 = 0;
            int golsE2 = 0;

            if (confronto.getTimeA().equals(e1.getSelecao())) {
                golsE1 = palpite.getGolsTimeA();
                golsE2 = palpite.getGolsTimeB();
            } else {
                golsE1 = palpite.getGolsTimeB();
                golsE2 = palpite.getGolsTimeA();
            }

            // a. Pontos no confronto direto
            int pontosE1 = (golsE1 > golsE2) ? 3 : (golsE1 == golsE2 ? 1 : 0);
            int pontosE2 = (golsE2 > golsE1) ? 3 : (golsE1 == golsE2 ? 1 : 0);

            if (pontosE1 != pontosE2) {
                return Integer.compare(pontosE2, pontosE1);
            }

            // b. Saldo de gols no confronto direto
            int saldoE1 = golsE1 - golsE2;
            int saldoE2 = golsE2 - golsE1;
            if (saldoE1 != saldoE2) {
                return Integer.compare(saldoE2, saldoE1);
            }

            // c. Gols marcados no confronto direto
            if (golsE1 != golsE2) {
                return Integer.compare(golsE2, golsE1);
            }
        }
        // --- FIM DO CRITÉRIO DE CONFRONTO DIRETO ---

        // d. Melhor saldo de gols geral
        if (e1.getSaldoGols() != e2.getSaldoGols()) {
            return Integer.compare(e2.getSaldoGols(), e1.getSaldoGols());
        }

        // e. Maior número de gols marcados geral
        if (e1.getGolsPro() != e2.getGolsPro()) {
            return Integer.compare(e2.getGolsPro(), e1.getGolsPro());
        }

        // f. Ordem alfabética
        return e1.getSelecao().getNome().compareToIgnoreCase(e2.getSelecao().getNome());
    }
}