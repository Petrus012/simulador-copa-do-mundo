package br.ufla.copa.ui.terminal;

import java.util.List;
import java.util.Scanner;

import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

public class MenuTerminal {

    private Scanner entrada;
    private SimuladorAnalistasCopaDoMundo simulador;

    public MenuTerminal() {
        entrada = new Scanner(System.in);
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();
    }

    public void iniciar() {
        System.out.println("\nIniciando interface via terminal...\n");

        int opcao = 0;
        do {
            exibirMenu();
            System.out.print("\nDigite sua opção: ");
            
            String linha = entrada.nextLine();
            if (!linha.trim().isEmpty()) {
                opcao = Integer.parseInt(linha);
                tratarMenu(opcao);
            }
            
        } while (opcao != 7);
        
        entrada.close();
    }

    private void exibirMenu() {
        System.out.println("\n--- SIMULADOR DA COPA DO MUNDO 2026 ---");
        System.out.println("1 - Exibir Estádios");
        System.out.println("2 - Exibir Partidas por Grupo");
        System.out.println("3 - Importar Palpites");
        System.out.println("4 - Exibir Tabela de Classificação dos Palpites");
        System.out.println("5 - Importar Resultados Oficiais");
        System.out.println("6 - Exibir Tabela de Classificação Oficial");
        System.out.println("7 - Sair");
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
                exibirClassificacaoGrupo();
                break;
            case 5:
                importarResultadosOficiais();
                break;
            case 6:
                exibirClassificacaoOficial();
                break;
            case 7:
                System.out.println("Saindo do programa...");
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }
    }

    private void exibirEstadios() {
        List<Estadio> listaEstadios = simulador.buscarEstadios();
        
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

    private void exibirClassificacaoGrupo() {
        System.out.print("\nDigite a letra do grupo que deseja visualizar a classificação (ex: A, B, C): ");
        String letraGrupo = entrada.nextLine().trim().toUpperCase();

        try {
            List<EstatisticasSelecao> classificacao = simulador.obterClassificacaoGrupo(letraGrupo);
            
            System.out.println("\n--- Tabela de Classificação do Grupo " + letraGrupo + " ---");
            System.out.printf("%-4s | %-20s | %-2s | %-2s | %-3s\n", "Pos", "Seleção", "P", "V", "SG");
            System.out.println("-----------------------------------------------------");
            
            for (int i = 0; i < classificacao.size(); i++) {
                EstatisticasSelecao est = classificacao.get(i);
                System.out.printf("%2dº  | %-20s | %2d | %2d | %3d\n", 
                        (i + 1), 
                        est.getSelecao().getNome(), 
                        est.getPontos(), 
                        est.getVitorias(), 
                        est.getSaldoGols());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nErro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nOcorreu um erro inesperado ao processar a classificação do grupo.");
        }
    }

    private void importarResultadosOficiais() {
        System.out.print("\nDigite o caminho do arquivo CSV com resultados (ex: src/resources/resultados.csv): ");
        String caminho = entrada.nextLine().trim();

        try {
            int quantidade = simulador.importarResultadosOficiais(caminho);
            System.out.println("\nForam importados " + quantidade + " resultados oficiais com sucesso!");
        } catch (Exception e) {
            System.out.println("\nErro ao importar resultados: Arquivo não encontrado ou formato inválido.");
        }
    }

    private void exibirClassificacaoOficial() {
        System.out.print("\nDigite a letra do grupo que deseja visualizar a classificação OFICIAL (ex: A, B, C): ");
        String letraGrupo = entrada.nextLine().trim().toUpperCase();

        try {
            List<EstatisticasSelecao> classificacao = simulador.obterClassificacaoOficialGrupo(letraGrupo);
            
            System.out.println("\n--- Tabela OFICIAL do Grupo " + letraGrupo + " ---");
            System.out.printf("%-4s | %-20s | %-2s | %-2s | %-3s\n", "Pos", "Seleção", "P", "V", "SG");
            System.out.println("-----------------------------------------------------");
            
            for (int i = 0; i < classificacao.size(); i++) {
                EstatisticasSelecao est = classificacao.get(i);
                System.out.printf("%2dº  | %-20s | %2d | %2d | %3d\n", 
                        (i + 1), est.getSelecao().getNome(), est.getPontos(), est.getVitorias(), est.getSaldoGols());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nErro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nOcorreu um erro inesperado ao processar a classificação oficial.");
        }
    }
}