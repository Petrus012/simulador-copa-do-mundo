package br.ufla.copa.ui.terminal;

import java.util.List;
import java.util.Scanner;

import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.service.ResultadoPontuacao;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

/**
 * Classe que trata a interação com o usuário via terminal.
 * Cobre as histórias H1 a H6 da Fase 1 e Fase 2.
 */
public class MenuTerminal {
    private static final String CAMINHO_PADRAO_PALPITES = "src/resources/modelo_palpites.csv";
    private static final String CAMINHO_PADRAO_RESULTADOS = "src/resources/resultados.csv";

    private Scanner entrada;
    private SimuladorAnalistasCopaDoMundo simulador;

    public MenuTerminal() {
        entrada = new Scanner(System.in);
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();
    }

    /**
     * Inicia o loop principal do menu via terminal.
     */
    public void iniciar() {
        System.out.println("\nIniciando interface via terminal...\n");

        int opcao = 0;
        do {
            exibirMenu();
            System.out.print("\nDigite sua opção: ");

            try {
                String linha = entrada.nextLine().trim();
                if (linha.isEmpty()) continue;

                opcao = Integer.parseInt(linha);
                tratarMenu(opcao);

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número correspondente à opção.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }

        } while (opcao != 8);

        entrada.close();
    }

    private void exibirMenu() {
        System.out.println("\n--- SIMULADOR DA COPA DO MUNDO 2026 ---");
        System.out.println("1 - Exibir Estádios");
        System.out.println("2 - Exibir Partidas por Grupo");
        System.out.println("3 - Importar Palpites");
        System.out.println("4 - Importar Resultados Oficiais");
        System.out.println("5 - Exibir Tabela de Classificação do Grupo");
        System.out.println("6 - Pontuação de Uma Partida");
        System.out.println("7 - Pontuação Total dos Palpites");
        System.out.println("8 - Sair");
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
                importarResultadosOficiais();
                break;
            case 5:
                exibirClassificacaoGrupo();
                break;
            case 6:
                exibirPontuacaoPartida();
                break;
            case 7:
                exibirPontuacaoTotal();
                break;
            case 8:
                System.out.println("Saindo do programa...");
                break;
            default:
                System.out.println("Opção inválida! Escolha entre 1 e 8.");
                break;
        }
    }

    // -----------------------------------------------------------------------
    // H1 — Exibição de estádios e partidas por grupo
    // -----------------------------------------------------------------------

    private void exibirEstadios() {
        List<Estadio> listaEstadios = simulador.buscarEstadios();
        System.out.println("\nEstádios:");
        System.out.println("-----------------------------------");
        for (Estadio estadio : listaEstadios) {
            System.out.println(estadio.getNome() + " (" + estadio.getPais() + ")");
        }
    }

    private void exibirPartidasPorGrupo() {
        System.out.print("\nDigite a letra do grupo (ex: A, B, C): ");
        String entrada_str = entrada.nextLine().trim().toUpperCase();

        if (entrada_str.isEmpty()) {
            System.out.println("Entrada vazia. Operação cancelada.");
            return;
        }

        Grupo grupoEscolhido = simulador.buscarGrupo(entrada_str.charAt(0));

        if (grupoEscolhido != null) {
            System.out.println("\n--- Partidas do Grupo " + grupoEscolhido.getNome() + " ---");
            for (Partida p : grupoEscolhido.getPartidas()) {
                System.out.println(p.toString());
            }
        } else {
            System.out.println("Grupo '" + entrada_str + "' não encontrado.");
        }
    }

    // -----------------------------------------------------------------------
    // H2 — Importação de palpites
    // -----------------------------------------------------------------------

    private void importarPalpites() {
    System.out.print("\nDigite o nome do arquivo CSV em src/resources/");
    System.out.print("\n(ou pressione Enter para usar '" + CAMINHO_PADRAO_PALPITES + "'): ");
    String nomeArquivo = entrada.nextLine().trim();

    String caminho;
    if (nomeArquivo.isEmpty()) {
        caminho = CAMINHO_PADRAO_PALPITES;  // "src/resources/modelo_palpites.csv"
    } else {
        caminho = "src/resources/" + nomeArquivo;
    }

    int quantidade = simulador.importarPalpites(caminho);
    System.out.println("\nForam importados/atualizados " + quantidade + " palpites com sucesso!");
}

    // -----------------------------------------------------------------------
    // H4 — Importação de resultados oficiais
    // -----------------------------------------------------------------------

    private void importarResultadosOficiais() {
        System.out.print("\nDigite o nome do arquivo CSV em src/resources/");
        System.out.print("\n(ou pressione Enter para usar '" + CAMINHO_PADRAO_RESULTADOS + "'): ");
        String nomeArquivo = entrada.nextLine().trim();

        String caminho;
        if (nomeArquivo.isEmpty()) {
            caminho = CAMINHO_PADRAO_RESULTADOS;
        } else {
            caminho = "src/resources/" + nomeArquivo;
        }

        simulador.importarResultadosOficiais(caminho);
        System.out.println("\nResultados oficiais importados com sucesso!");
        System.out.println("Partidas importadas agora têm status FINALIZADA e não aceitam novos palpites.");
    }

    // -----------------------------------------------------------------------
    // H3 — Tabela de classificação do grupo
    // -----------------------------------------------------------------------

    private void exibirClassificacaoGrupo() {
        System.out.print("\nDigite a letra do grupo (ex: A, B, C): ");
        String entrada_str = entrada.nextLine().trim().toUpperCase();

        if (entrada_str.isEmpty()) {
            System.out.println("Entrada vazia. Operação cancelada.");
            return;
        }

        try {
            List<EstatisticasSelecao> classificacao =
                    simulador.obterClassificacaoGrupo(entrada_str.charAt(0));

            System.out.println("\n--- Tabela de Classificação do Grupo " + entrada_str + " ---");
            System.out.printf("%-4s | %-22s | %-3s | %-3s | %-5s | %-5s%n",
                    "Pos", "Seleção", "Pts", "V", "SG", "GP");
            System.out.println("------------------------------------------------------");

            for (int i = 0; i < classificacao.size(); i++) {
                EstatisticasSelecao est = classificacao.get(i);
                System.out.printf("%2dº  | %-22s | %3d | %3d | %5d | %5d%n",
                        (i + 1),
                        est.getSelecao().getNome(),
                        est.getPontos(),
                        est.getVitorias(),
                        est.getSaldoGols(),
                        est.getGolsPro());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nErro: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // H5 — Pontuação de uma partida específica
    // -----------------------------------------------------------------------

    private void exibirPontuacaoPartida() {
        System.out.print("\nDigite o ID da partida: ");
        String entrada_str = entrada.nextLine().trim();

        if (entrada_str.isEmpty()) {
            System.out.println("Entrada vazia. Operação cancelada.");
            return;
        }

        try {
            int idPartida = Integer.parseInt(entrada_str);
            ResultadoPontuacao resultado = simulador.calcularPontuacaoPartida(idPartida);

            if (resultado == null) {
                System.out.println("\nNão é possível calcular pontuação para esta partida.");
                System.out.println("Verifique se há palpite registrado e se o resultado oficial foi importado.");
                return;
            }

            Partida partida = resultado.getPartida();
            System.out.println("\n--- Pontuação da Partida " + idPartida + " ---");
            System.out.println(partida.toString());
            System.out.println();

            for (int i = 0; i < resultado.getQuantidadeRegras(); i++) {
                String status = resultado.getPontosObtidos(i) > 0 ? "✔" : "✘";
                System.out.printf("  %s %s — %s: %d pts%n",
                        status,
                        resultado.getIdentificador(i),
                        resultado.getDescricao(i),
                        resultado.getPontosObtidos(i));
            }

            System.out.println("  ---------------------------------------");
            System.out.printf("  TOTAL: %d pontos%n", resultado.getTotalPontos());

        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número inteiro.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // H6 — Pontuação total de todos os palpites
    // -----------------------------------------------------------------------

    private void exibirPontuacaoTotal() {
        List<ResultadoPontuacao> resultados = simulador.calcularPontuacaoTotal();

        if (resultados.isEmpty()) {
            System.out.println("\nNenhuma partida finalizada com palpite encontrada.");
            System.out.println("Importe palpites e resultados oficiais antes de consultar a pontuação.");
            return;
        }

        System.out.println("\n--- Pontuação Total dos Palpites ---");
        System.out.printf("%-10s | %-30s | %-10s | %-6s%n",
                "Partida", "Confronto", "Palpite", "Pontos");
        System.out.println("-----------------------------------------------------------");

        for (ResultadoPontuacao resultado : resultados) {
            Partida p = resultado.getPartida();
            String confronto = p.getTimeDaCasa().getNome() + " x " + p.getTimeVisitante().getNome();
            String palpite = p.getPalpite().getGolsTimeCasa() + " x " + p.getPalpite().getGolsTimeVisitante();

            System.out.printf("%-10d | %-30s | %-10s | %6d%n",
                    p.getId(),
                    confronto,
                    palpite,
                    resultado.getTotalPontos());
        }

        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-55s %6d pts%n", "PONTUAÇÃO TOTAL:", simulador.somarPontuacao(resultados));
    }
}