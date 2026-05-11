package br.ufla.copa.ui.web;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import java.util.List;

import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;


/**
 * Classe que implementa a página inicial da interface web
 */
@Route("") 
public class MainView extends VerticalLayout {

    // Simulador, objeto principal da regra de negócio
    private SimuladorAnalistasCopaDoMundo simulador;

    // Caixa de texto para inserção do nome
    private TextField campoNome;
    // Botão que envia notificação de saudação
    private Button botaoSaudacao;
    // Botão que carrega os dados dos estadios
    private Button botaoCarregarEstadios;
    // Grid para exibição dos dados dos estádios
    private Grid<Estadio> gridEstadios;

    /**
     * Construtor da classe
     */
    public MainView() {
        // obtém a única instância do simulador (Padrão de Projeto Singleton)
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();

        // Cabeçalho
        H1 cabecalho = new H1("Simulador de Analistas Esportivos - Copa 2026");
        
        // Caixa de texto para inserção do nome
        campoNome = new TextField("Digite seu nome:");
        campoNome.setPlaceholder("Ex: João Silva"); // dica que aparece na caixa de texto

        // Botão com Notificação
        botaoSaudacao = new Button("Enviar");

        // Botão para carregar os dados dos estadios
        botaoCarregarEstadios = new Button("Carregar Estadios");

        // Titulo para o Grid
        H2 tituloGrid = new H2("Estádios");
        
        // Grid para exibir dados da classe Estadio
        gridEstadios = new Grid<>(Estadio.class);

        // Configura os eventos dos componentes
        configuraEventos();
        
        // Adiciona todos os componentes ao layout da página
        add(cabecalho, campoNome, botaoSaudacao, botaoCarregarEstadios, tituloGrid, gridEstadios);
        
        // Ajustes de layout (centraliza os componentes)
        setAlignItems(Alignment.CENTER);
    }

    private void configuraEventos() {
        // Tratamento do evento de clique do botão de Saudação usando Classe Anônima
        botaoSaudacao.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                // obtém o texto do campo de nome
                String nome = campoNome.getValue();
                // Exibe uma notificação de acordo com o valor do campo de nome
                if (nome.isEmpty()) {
                    Notification.show("Por favor, digite um nome!");
                } else {
                    Notification.show("Olá, " + nome + "! Bem-vindo ao simulador.");
                }
            }
        });

        // Tratamento do evento de clique do botão de Carregar Estadios usando Classe Anônima
        botaoCarregarEstadios.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                carregarGridEstadios();
            }
        });
    }

    private void carregarGridEstadios() {
        // Configura os dados (Simulando o que viria do repositório/core)
        List<Estadio> listaEstadios = simulador.buscarEstadios();
        
        // Passa a lista de dados para o grid
        gridEstadios.setItems(listaEstadios);
        // Remove as colunas adicionadas anteriormente
        gridEstadios.removeAllColumns();

        // Adiciona a primeira coluna ao grid, indicado que ela deve usar o
        // método getNome da classe Estadio para obter a informação        
        gridEstadios.addColumn(Estadio::getNome)
            .setHeader("Nome do Estádio") // título da coluna no grid
            .setSortable(true)    // permite ordenar pela coluna
            .setAutoWidth(true); // define largura automática

        // Adiciona a segunda coluna ao grid, indicado que ela deve usar o
        // método getCapacidade da classe Estadio para obter a informação
        gridEstadios.addColumn(Estadio::getCapacidade)
            .setHeader("Capacidade") // título da coluna no grid
            .setSortable(true)    // permite ordenar pela coluna
            .setAutoWidth(true); // define largura automática
        
        // Obs. 1: o encadeamento de chamadas dos comandos acima usa o 
        //         padrão de projeto Fluent Interface
        // Obs. 2: a passagem de referência de método com o operador :: como
        //         em Estadio::getNome é uma forma concisa de escrever uma 
        //         função lambda (algo que não é permitido no trabalho, mas 
        //         será aceito nesse caso para simplificar a implementação)
    }

}