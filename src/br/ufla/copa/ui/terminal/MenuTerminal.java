package br.ufla.copa.ui.terminal;

import java.util.List;
import java.util.Scanner;

import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

/**
 * Classe que trata a interação com o usuário via terminal
 */
public class MenuTerminal {

    // Objeto Scanner para leitura de dados via terminal
    private Scanner entrada;
    // Simulador, objeto principal da regra de negócio
    private SimuladorAnalistasCopaDoMundo simulador;

    /**
     * Construtor da classe
     */
    public MenuTerminal() {
        entrada = new Scanner(System.in);
        // obtém a única instância do simulador (Padrão de Projeto Singleton)
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();
    }

    /**
     * Método que inicia a execução do menu via terminal
     */
    public void iniciar() {
        System.out.println("\nIniciando interface via terminal...\n");

        // Busca a list de estádios
        List<Estadio> listaEstadios = simulador.buscarEstadios();

        // Executa o loop de menu
        String opcao;
        do {
            // Exibe os estádios
            System.out.println("Estadios:");
            for (Estadio  estadio : listaEstadios) {
                System.out.println(estadio.getNome() + " - capacidade: " + estadio.getCapacidade() + " pessoas");
            }

            // Sai do loop se o usuário digitar 'sair' (ignorando maiúsculas/minúsculas)
            System.out.println("\nDigite 'sair' para terminar");
            opcao = entrada.nextLine();
        } while (! "sair".equalsIgnoreCase(opcao));

    }
}
