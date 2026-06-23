package br.ufla.copa.ui.web;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

import br.ufla.copa.core.model.Analista;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.StatusPartida;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

/**
 * Tela de registro de palpites via interface web (H13).
 * Permite selecionar ou criar um analista, escolher um grupo por aba
 * e digitar o palpite de cada partida. Ao salvar, exibe a classificação
 * do grupo calculada pelos palpites registrados.
 * Não há lógica de negócio aqui — tudo é delegado ao Simulador.
 */
@Route(value = "palpites", layout = MainLayout.class)
@PageTitle("Registrar Palpites | Copa 2026")
public class PalpitesView extends VerticalLayout {

    private final SimuladorAnalistasCopaDoMundo simulador;
    private final List<Grupo> grupos;
    private final List<Tab> listaAbas;

    // Componentes de seleção de analista
    private final Select<String> selectorAnalista;
    private final TextField campoNovoAnalista;

    // Título dinâmico acima do formulário
    private final H3 tituloFormulario;

    // Container do formulário — é reconstruído ao trocar de grupo ou analista
    private final VerticalLayout areaFormulario;

    // Grid de classificação pelos palpites
    private final Grid<EstatisticasSelecao> gridClassificacaoPalpites;

    // Estado do grupo exibido e das linhas do formulário (listas paralelas)
    private Grupo grupoAtual;
    private List<Partida> partidasDaLinha;
    private List<TextField> camposGolsCasa;
    private List<TextField> camposGolsVisitante;

    public PalpitesView() {
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();
        grupos    = simulador.buscarGrupos();
        listaAbas = new ArrayList<>();

        partidasDaLinha       = new ArrayList<>();
        camposGolsCasa        = new ArrayList<>();
        camposGolsVisitante   = new ArrayList<>();

        add(new H2("Registrar Palpites"));

        // ---- Seção de seleção / criação de analista ----
        selectorAnalista   = criarSelectorAnalista();
        campoNovoAnalista  = new TextField();
        campoNovoAnalista.setPlaceholder("Nome do novo analista");
        campoNovoAnalista.setWidth("220px");

        Button botaoCriar = new Button("Criar Analista");
        Button botaoUsar  = new Button("Usar Analista Selecionado");

        configurarBotaoCriar(botaoCriar);
        configurarBotaoUsar(botaoUsar);

        HorizontalLayout barraAnalista = new HorizontalLayout(
            new Span("Analista:"), selectorAnalista, botaoUsar,
            campoNovoAnalista, botaoCriar
        );
        barraAnalista.setAlignItems(Alignment.BASELINE);
        add(barraAnalista);

        // ---- Abas de grupos ----
        Tabs abaGrupos = criarAbas();
        add(abaGrupos);

        // ---- Formulário de palpites ----
        tituloFormulario = new H3();
        areaFormulario   = new VerticalLayout();
        areaFormulario.setPadding(false);
        areaFormulario.setSpacing(false);
        add(tituloFormulario, areaFormulario);

        Button botaoSalvar = new Button("Salvar Palpites do Grupo");
        configurarBotaoSalvar(botaoSalvar);
        add(botaoSalvar);

        // ---- Classificação pelos palpites ----
        add(new H3("Classificação pelo meus Palpites"));
        gridClassificacaoPalpites = criarGridClassificacao();
        add(gridClassificacaoPalpites);

        // ---- Evento de troca de aba ----
        abaGrupos.addSelectedChangeListener(new ComponentEventListener<Tabs.SelectedChangeEvent>() {
            @Override
            public void onComponentEvent(Tabs.SelectedChangeEvent event) {
                int index = listaAbas.indexOf(event.getSelectedTab());
                if (index >= 0 && index < grupos.size()) {
                    grupoAtual = grupos.get(index);
                    reconstruirFormulario(grupoAtual);
                }
            }
        });

        // Carrega o primeiro grupo ao abrir a página
        if (!grupos.isEmpty()) {
            grupoAtual = grupos.get(0);
            reconstruirFormulario(grupoAtual);
        }

        setWidthFull();
        setPadding(true);
        setSpacing(true);
    }

    // -----------------------------------------------------------------------
    // Criação dos componentes
    // -----------------------------------------------------------------------

    private Select<String> criarSelectorAnalista() {
        Select<String> sel = new Select<>();
        sel.setLabel("Analista");
        atualizarItensDoSelector(sel);
        return sel;
    }

    private Tabs criarAbas() {
        Tabs tabs = new Tabs();
        for (Grupo g : grupos) {
            Tab aba = new Tab("Grupo " + g.getNome());
            listaAbas.add(aba);
            tabs.add(aba);
        }
        return tabs;
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
    // Configuração de eventos dos botões (Classes Anônimas — sem lambdas)
    // -----------------------------------------------------------------------

    private void configurarBotaoCriar(Button botao) {
        botao.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                String nome = campoNovoAnalista.getValue().trim();
                if (nome.isEmpty()) {
                    Notification.show("Digite um nome para o novo analista.");
                    return;
                }
                if (simulador.cadastrarAnalista(nome)) {
                    campoNovoAnalista.clear();
                    atualizarItensDoSelector(selectorAnalista);
                    selectorAnalista.setValue(simulador.getAnalistaAtivo().getNome());
                    if (grupoAtual != null) {
                        reconstruirFormulario(grupoAtual);
                    }
                    Notification.show("Analista \"" + nome + "\" criado e selecionado.");
                } else {
                    Notification.show("Já existe um analista com esse nome.");
                }
            }
        });
    }

    private void configurarBotaoUsar(Button botao) {
        botao.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                String nome = selectorAnalista.getValue();
                if (nome == null || nome.isEmpty()) {
                    Notification.show("Selecione um analista da lista.");
                    return;
                }
                simulador.selecionarAnalista(nome);
                if (grupoAtual != null) {
                    reconstruirFormulario(grupoAtual);
                }
                Notification.show("Analista \"" + nome + "\" ativado.");
            }
        });
    }

    private void configurarBotaoSalvar(Button botao) {
        botao.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                salvarPalpitesDoGrupo();
            }
        });
    }

    // -----------------------------------------------------------------------
    // Formulário de palpites
    // -----------------------------------------------------------------------

    /**
     * Reconstrói o formulário para o grupo recebido.
     * Pré-preenche os campos com os palpites já salvos pelo analista ativo.
     * Partidas finalizadas ficam em modo leitura.
     */
    private void reconstruirFormulario(Grupo grupo) {
        areaFormulario.removeAll();
        partidasDaLinha     = new ArrayList<>(grupo.getPartidas());
        camposGolsCasa      = new ArrayList<>();
        camposGolsVisitante = new ArrayList<>();

        String nomeAnalista = simulador.getAnalistaAtivo().getNome();
        tituloFormulario.setText(
            "Partidas do Grupo " + grupo.getNome() + " — " + nomeAnalista
        );

        for (Partida p : partidasDaLinha) {
            boolean finalizada = p.getStatus() == StatusPartida.FINALIZADA;

            // Campos de gols
            TextField tfCasa = new TextField();
            tfCasa.setWidth("55px");
            tfCasa.setPlaceholder("0");

            TextField tfVisitante = new TextField();
            tfVisitante.setWidth("55px");
            tfVisitante.setPlaceholder("0");

            // Pré-preenche com palpite existente (carregado via hidratação do analista)
            if (p.temPalpite()) {
                tfCasa.setValue(String.valueOf(p.getPalpite().getGolsTimeCasa()));
                tfVisitante.setValue(String.valueOf(p.getPalpite().getGolsTimeVisitante()));
            }

            // Partidas finalizadas ficam somente leitura
            if (finalizada) {
                tfCasa.setReadOnly(true);
                tfVisitante.setReadOnly(true);
            }

            // Status e resultado oficial (se finalizada)
            String labelStatus = finalizada
                ? " (Finalizada — Oficial: "
                  + p.getGolsTimeCasaOficial() + "x" + p.getGolsTimeVisitanteOficial() + ")"
                : " (Agendada)";

            HorizontalLayout linha = new HorizontalLayout(
                new Span(String.format("#%d  %-25s", p.getId(), p.getTimeDaCasa().getNome())),
                tfCasa,
                new Span(" x "),
                tfVisitante,
                new Span(p.getTimeVisitante().getNome()),
                new Span(labelStatus)
            );
            linha.setAlignItems(Alignment.CENTER);
            areaFormulario.add(linha);

            camposGolsCasa.add(tfCasa);
            camposGolsVisitante.add(tfVisitante);
        }

        // Atualiza classificação baseada nos palpites atuais do analista
        atualizarGridClassificacaoPalpites(grupo);
    }

    /**
     * Lê os campos do formulário, registra os palpites válidos no serviço
     * e persiste no HD ao final do lote.
     */
    private void salvarPalpitesDoGrupo() {
        if (grupoAtual == null) return;

        int salvos = 0;
        int ignorados = 0;

        for (int i = 0; i < partidasDaLinha.size(); i++) {
            Partida p = partidasDaLinha.get(i);
            if (p.getStatus() == StatusPartida.FINALIZADA) continue;

            String valorCasa = camposGolsCasa.get(i).getValue().trim();
            String valorVis  = camposGolsVisitante.get(i).getValue().trim();

            if (valorCasa.isEmpty() || valorVis.isEmpty()) continue;

            try {
                int gc = Integer.parseInt(valorCasa);
                int gv = Integer.parseInt(valorVis);

                if (simulador.atualizarPalpite(p.getId(), gc, gv)) {
                    salvos++;
                } else {
                    ignorados++;
                }
            } catch (NumberFormatException e) {
                ignorados++;
            }
        }

        // Persiste todos os palpites do lote de uma vez
        simulador.salvarEstadoDoSistema();

        // Atualiza a classificação pelos palpites após o salvamento
        atualizarGridClassificacaoPalpites(grupoAtual);

        String mensagem = salvos + " palpite(s) salvo(s)";
        if (ignorados > 0) {
            mensagem += ", " + ignorados + " ignorado(s) (inválido ou partida finalizada)";
        }
        Notification.show(mensagem + ".");
    }

    // -----------------------------------------------------------------------
    // Utilitários
    // -----------------------------------------------------------------------

    /** Recarrega os itens do selector com a lista atualizada de analistas. */
    private void atualizarItensDoSelector(Select<String> sel) {
        List<String> nomes = new ArrayList<>();
        for (Analista a : simulador.getAnalistas()) {
            nomes.add(a.getNome());
        }
        sel.setItems(nomes);
        if (simulador.getAnalistaAtivo() != null) {
            sel.setValue(simulador.getAnalistaAtivo().getNome());
        }
    }

    /** Atualiza o grid de classificação com os palpites do analista ativo para o grupo. */
    private void atualizarGridClassificacaoPalpites(Grupo grupo) {
        gridClassificacaoPalpites.setItems(grupo.getClassificacaoPelosPalpites());
    }
}
