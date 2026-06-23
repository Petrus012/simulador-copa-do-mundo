package br.ufla.copa.ui.web;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.StatusPartida;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

/**
 * Dashboard principal da interface web (H12).
 * Exibe abas por grupo com as partidas e a classificação oficial de cada grupo.
 * Toda a lógica de negócio reside na camada core; aqui apenas chamamos o Simulador.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Jogos e Resultados | Copa 2026")
public class MainView extends VerticalLayout {

    private final SimuladorAnalistasCopaDoMundo simulador;
    private final List<Grupo> grupos;
    // Lista paralela às abas para localizar o grupo pelo índice da tab selecionada
    private final List<Tab> listaAbas;

    private final Grid<Partida> gridPartidas;
    private final Grid<EstatisticasSelecao> gridClassificacao;

    public MainView() {
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();
        grupos    = simulador.buscarGrupos();
        listaAbas = new ArrayList<>();

        add(new H2("Jogos e Resultados Oficiais"));

        Tabs abaGrupos = criarAbas();
        add(abaGrupos);

        add(new H3("Partidas"));
        gridPartidas = criarGridPartidas();
        add(gridPartidas);

        add(new H3("Classificação Oficial do Grupo"));
        gridClassificacao = criarGridClassificacao();
        add(gridClassificacao);

        // Evento de troca de aba — usa Classe Anônima conforme requisito do trabalho
        abaGrupos.addSelectedChangeListener(new ComponentEventListener<Tabs.SelectedChangeEvent>() {
            @Override
            public void onComponentEvent(Tabs.SelectedChangeEvent event) {
                int index = listaAbas.indexOf(event.getSelectedTab());
                if (index >= 0 && index < grupos.size()) {
                    carregarDadosDoGrupo(grupos.get(index));
                }
            }
        });

        // Carrega o primeiro grupo ao abrir a página
        if (!grupos.isEmpty()) {
            carregarDadosDoGrupo(grupos.get(0));
        }

        setWidthFull();
        setPadding(true);
        setSpacing(true);
    }

    // -----------------------------------------------------------------------
    // Criação dos componentes
    // -----------------------------------------------------------------------

    private Tabs criarAbas() {
        Tabs tabs = new Tabs();
        for (Grupo g : grupos) {
            Tab aba = new Tab("Grupo " + g.getNome());
            listaAbas.add(aba);
            tabs.add(aba);
        }
        return tabs;
    }

    private Grid<Partida> criarGridPartidas() {
        Grid<Partida> grid = new Grid<>();
        grid.removeAllColumns();

        grid.addColumn(new ValueProvider<Partida, Integer>() {
            @Override
            public Integer apply(Partida p) {
                return p.getId();
            }
        }).setHeader("ID").setAutoWidth(true);

        grid.addColumn(new ValueProvider<Partida, String>() {
            @Override
            public String apply(Partida p) {
                return p.getTimeDaCasa().getNome();
            }
        }).setHeader("Mandante").setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(new ValueProvider<Partida, String>() {
            @Override
            public String apply(Partida p) {
                if (p.getStatus() == StatusPartida.FINALIZADA) {
                    return p.getGolsTimeCasaOficial() + " x " + p.getGolsTimeVisitanteOficial();
                }
                return "— x —";
            }
        }).setHeader("Placar Oficial").setAutoWidth(true);

        grid.addColumn(new ValueProvider<Partida, String>() {
            @Override
            public String apply(Partida p) {
                return p.getTimeVisitante().getNome();
            }
        }).setHeader("Visitante").setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(new ValueProvider<Partida, String>() {
            @Override
            public String apply(Partida p) {
                switch (p.getStatus()) {
                    case FINALIZADA:   return "Finalizada";
                    case EM_ANDAMENTO: return "Em andamento";
                    default:           return "Agendada";
                }
            }
        }).setHeader("Status").setAutoWidth(true);

        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        return grid;
    }

    private Grid<EstatisticasSelecao> criarGridClassificacao() {
        Grid<EstatisticasSelecao> grid = new Grid<>();
        grid.removeAllColumns();

        grid.addColumn(new ValueProvider<EstatisticasSelecao, String>() {
            @Override
            public String apply(EstatisticasSelecao es) {
                return es.getSelecao().getNome();
            }
        }).setHeader("País").setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getPontos();
            }
        }).setHeader("Pts").setAutoWidth(true);

        grid.addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getVitorias();
            }
        }).setHeader("V").setAutoWidth(true);

        grid.addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getSaldoGols();
            }
        }).setHeader("SG").setAutoWidth(true);

        grid.addColumn(new ValueProvider<EstatisticasSelecao, Integer>() {
            @Override
            public Integer apply(EstatisticasSelecao es) {
                return es.getGolsPro();
            }
        }).setHeader("GP").setAutoWidth(true);

        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        return grid;
    }

    // -----------------------------------------------------------------------
    // Atualização dos dados exibidos
    // -----------------------------------------------------------------------

    private void carregarDadosDoGrupo(Grupo grupo) {
        gridPartidas.setItems(grupo.getPartidas());
        // getClassificacao() prioriza resultados oficiais (H4) sobre palpites
        gridClassificacao.setItems(grupo.getClassificacao());
    }
}
