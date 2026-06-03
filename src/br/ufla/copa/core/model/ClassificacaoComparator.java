package br.ufla.copa.core.model;

import java.util.Comparator;

public class ClassificacaoComparator implements Comparator<EstatisticasSelecao> {

    @Override
    public int compare(EstatisticasSelecao e1, EstatisticasSelecao e2) {
        if (e1.getPontos() != e2.getPontos()) {
            return Integer.compare(e2.getPontos(), e1.getPontos());
        }

        if (e1.getSaldoGols() != e2.getSaldoGols()) {
            return Integer.compare(e2.getSaldoGols(), e1.getSaldoGols());
        }

        if (e1.getGolsPro() != e2.getGolsPro()) {
            return Integer.compare(e2.getGolsPro(), e1.getGolsPro());
        }

        return e1.getSelecao().getNome().compareToIgnoreCase(e2.getSelecao().getNome());
    }
}