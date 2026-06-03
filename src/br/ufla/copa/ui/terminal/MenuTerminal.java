package br.ufla.copa.ui.terminal;

import java.util.List;
import java.util.Scanner;

import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
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

        int opcao = 0;
        // Executa o loop de menu
        do {
            exibirMenu();
            System.out.print("\nDigite sua opção: ");
            
            String linha = entrada.nextLine();
            if (!linha.trim().isEmpty()) {
                opcao = Integer.parseInt(linha);
                tratarMenu(opcao);
            }
            
        } while (opcao != 4);
        
        entrada.close();
    }

    private void exibirMenu() {
        System.out.println("\n--- SIMULADOR DA COPA DO MUNDO 2026 ---");
        System.out.println("1 - Exibir Estádios");
        System.out.println("2 - Exibir Partidas por Grupo");
        System.out.println("3 - Importar Palpites");
        System.out.println("4 - Sair");
    }

    private void tratarMenu(int opcao) {
        switch (opcao) {
            case 1:
                exibirEstadios();
                break;
            case 2:
                exibirPartidasPorGrupo();
                break;
            case 3:
                importarPalpites();
                break;
            case 4:
                System.out.println("Saindo do programa...");
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }
    }

    private void exibirEstadios() {
        // Busca a list de estádios
        List<Estadio> listaEstadios = simulador.buscarEstadios();
        
        // Exibe os estádios
        System.out.println("\nEstadios:");
        for (Estadio estadio : listaEstadios) {
            System.out.println(estadio.getNome() + " - capacidade: " + estadio.getCapacidade() + " pessoas");
        }
    }

    private void exibirPartidasPorGrupo() {
        System.out.print("\nDigite a letra do grupo que deseja visualizar (ex: A, B, C): ");
        String letraGrupo = entrada.nextLine().trim().toUpperCase();

        Grupo grupoEscolhido = simulador.buscarGrupo(letraGrupo);

        if (grupoEscolhido != null) {
            System.out.println("\n--- Partidas do Grupo " + letraGrupo + " ---");
            for (int i = 0; i < grupoEscolhido.getPartidas().size(); i++) {
                Partida p = grupoEscolhido.getPartidas().get(i);
                System.out.println(p.toString());
            }
        } else {
            System.out.println("\nGrupo não encontrado.");
        }
    }

    private void importarPalpites() {
        System.out.print("\nDigite o caminho do arquivo CSV (ou aperte Enter para usar src/resources/modelo_palpites.csv): ");
        String caminho = entrada.nextLine().trim();
        
        if (caminho.isEmpty()) {
            caminho = "src/resources/modelo_palpites.csv";
        }

        int quantidade = simulador.importarPalpites(caminho);
        System.out.println("\nForam importados/atualizados " + quantidade + " palpites com sucesso!");
    }
}