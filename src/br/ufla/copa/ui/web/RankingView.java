package br.ufla.copa.ui.web;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

import br.ufla.copa.core.service.ItemRankingGeral;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

// Tela de ranking comparativo de analistas ordenados por prestígio (H14)
@Route(value = "ranking", layout = MainLayout.class)
@PageTitle("Ranking de Prestígio | Copa 2026")
public class RankingView extends VerticalLayout {

    // DTO de apresentação que adiciona o número de posição ao ItemRankingGeral
    private static class EntradaRanking {
        private final int    posicao;
        private final String nomeAnalista;
        private final int    pontuacao;

        EntradaRanking(int posicao, String nomeAnalista, int pontuacao) {
            this.posicao      = posicao;
            this.nomeAnalista = nomeAnalista;
            this.pontuacao    = pontuacao;
        }

        public int    getPosicao()      { return posicao; }
        public String getNomeAnalista() { return nomeAnalista; }
        public int    getPontuacao()    { return pontuacao; }
    }

    private final SimuladorAnalistasCopaDoMundo simulador;
    private final Grid<EntradaRanking> gridRanking;

    public RankingView() {
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();

        add(new H2("Ranking Geral de Prestígio"));
        add(new Span(
            "Pontuação baseada nos acertos de placar (H5/H6) " +
            "e na classificação dos grupos (H7)."
        ));

        gridRanking = criarGrid();
        add(gridRanking);

        Button botaoAtualizar = new Button("Atualizar Ranking");
        // Clique via Classe Anônima — sem lambdas
        botaoAtualizar.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                carregarRanking();
            }
        });
        add(botaoAtualizar);

        // Carrega o ranking ao abrir a página
        carregarRanking();

        setAlignItems(Alignment.CENTER);
        setWidthFull();
        setPadding(true);
        setSpacing(true);
    }

    // -----------------------------------------------------------------------
    // Criação dos componentes
    // -----------------------------------------------------------------------

    private Grid<EntradaRanking> criarGrid() {
        Grid<EntradaRanking> grid = new Grid<>();
        grid.removeAllColumns();

        grid.addColumn(new ValueProvider<EntradaRanking, Integer>() {
            @Override
            public Integer apply(EntradaRanking e) {
                return e.getPosicao();
            }
        }).setHeader("#").setAutoWidth(true);

        grid.addColumn(new ValueProvider<EntradaRanking, String>() {
            @Override
            public String apply(EntradaRanking e) {
                return e.getNomeAnalista();
            }
        }).setHeader("Analista").setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(new ValueProvider<EntradaRanking, Integer>() {
            @Override
            public Integer apply(EntradaRanking e) {
                return e.getPontuacao();
            }
        }).setHeader("Prestígio (pts)").setAutoWidth(true);

        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        return grid;
    }

    // -----------------------------------------------------------------------
    // Carregamento de dados
    // -----------------------------------------------------------------------

    // Obtém o ranking do serviço e converte para EntradaRanking com número de posição
    private void carregarRanking() {
        List<ItemRankingGeral> ranking = simulador.obterRankingGeralOrdenado();
        List<EntradaRanking> entradas  = new ArrayList<>();

        for (int i = 0; i < ranking.size(); i++) {
            ItemRankingGeral item = ranking.get(i);
            entradas.add(new EntradaRanking(i + 1, item.getNomeAnalista(), item.getPontuacaoTotal()));
        }

        gridRanking.setItems(entradas);
    }
}
