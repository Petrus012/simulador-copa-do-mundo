package br.ufla.copa.ui.web;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

/**
 * Layout principal da interface web (H12-H14).
 * Fornece cabeçalho fixo e menu lateral com navegação entre as três telas.
 * Estende AppLayout, que já implementa RouterLayout — dispensando qualquer
 * configuração adicional para que as views filhas usem este layout.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        criarCabecalho();
        criarMenuLateral();
    }

    private void criarCabecalho() {
        DrawerToggle toggle = new DrawerToggle();

        H1 titulo = new H1("Copa 2026 — Simulador de Analistas");
        titulo.getStyle()
              .set("font-size", "var(--lumo-font-size-l)")
              .set("margin", "0");

        HorizontalLayout cabecalho = new HorizontalLayout(toggle, titulo);
        cabecalho.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        cabecalho.setWidthFull();
        cabecalho.expand(titulo);

        addToNavbar(cabecalho);
    }

    private void criarMenuLateral() {
        RouterLink linkJogos    = new RouterLink("Jogos e Resultados", MainView.class);
        RouterLink linkPalpites = new RouterLink("Registrar Palpites",  PalpitesView.class);
        RouterLink linkRanking  = new RouterLink("Ranking de Prestígio", RankingView.class);

        VerticalLayout menu = new VerticalLayout(linkJogos, linkPalpites, linkRanking);
        menu.setPadding(true);
        menu.setSpacing(true);

        addToDrawer(menu);
    }
}
