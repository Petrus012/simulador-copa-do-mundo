package br.ufla.copa.ui.web;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.function.ValueProvider;

import br.ufla.copa.core.model.EstatisticasSelecao;

// Componente reutilizável de classificação de grupo — evita duplicação entre MainView e PalpitesView (H12/H13)
public class GridClassificacao extends Grid<EstatisticasSelecao> {

    public GridClassificacao() {
        removeAllColumns();

        addColumn(new ValueProvider<EstatisticasSelecao, String>() {
            @Override
            public String apply(EstatisticasSelecao es) {
                return es.getSelecao().getNome();
            }
        }).setHeader("País").setAutoWidth(true).setFlexGrow(1);

        addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getPontos();
            }
        }).setHeader("Pts").setAutoWidth(true);

        addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getVitorias();
            }
        }).setHeader("V").setAutoWidth(true);

        addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getSaldoGols();
            }
        }).setHeader("SG").setAutoWidth(true);

        addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getGolsPro();
            }
        }).setHeader("GP").setAutoWidth(true);

        setAllRowsVisible(true);
        setWidthFull();
    }
}
