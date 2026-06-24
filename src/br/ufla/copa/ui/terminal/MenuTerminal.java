package br.ufla.copa.ui.terminal;

import java.util.List;

import br.ufla.copa.core.model.Analista;
import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.ResultadoImportacaoPalpites;
import br.ufla.copa.core.service.ItemRankingGeral;
import br.ufla.copa.core.service.ResultadoPontuacao;
import br.ufla.copa.core.service.SimuladorAnalistasCopaDoMundo;

public class MenuTerminal {
    private static final String CAMINHO_PADRAO_PALPITES = "src/resources/modelo_palpites.csv";
    private static final String CAMINHO_PADRAO_RESULTADOS = "src/resources/resultados.csv";

    private SimuladorAnalistasCopaDoMundo simulador;

    public MenuTerminal() {
        simulador = SimuladorAnalistasCopaDoMundo.getInstance();
    }

    public void iniciar() {
        System.out.println("\nIniciando interface via terminal...\n");

        int opcao = 0;
        do {
            exibirMenu();
            System.out.print("\nDigite sua opção: ");

            try {
                String linha = LeitorConsole.lerLinha();
                if (linha.isEmpty()) continue;

                opcao = Integer.parseInt(linha);
                tratarMenu(opcao);

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número correspondente à opção.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }

        // H09: Loop expandido para 11 opções
        } while (opcao != 11);
    }

    private void exibirMenu() {
        String nomeAtivo = (simulador.getAnalistaAtivo() != null) 
                ? simulador.getAnalistaAtivo().getNome() 
                : "Nenhum";

        System.out.println("\n==============================================================================");
        System.out.println("--- SIMULADOR DA COPA DO MUNDO 2026  |  Analista logado: [" + nomeAtivo + "] ---");
        System.out.println("==============================================================================");
        System.out.println("1  - Exibir Estádios");
        System.out.println("2  - Exibir Partidas por Grupo");
        System.out.println("3  - Importar Palpites (Registra no cofre do analista atual)");
        System.out.println("4  - Importar Resultados Oficiais");
        System.out.println("5  - Exibir Tabela de Classificação do Grupo");
        System.out.println("6  - Pontuação de Uma Partida");
        System.out.println("7  - Pontuação Total Acumulada do Analista Atual");
        System.out.println("8  - Exibir Ranking Geral de Prestígio (H09)");
        System.out.println("9  - Cadastrar Novo Analista");
        System.out.println("10 - Mudar de Analista Ativo");
        System.out.println("11 - Sair");
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
                exibirRankingGeral();
                break;
            case 9:
                cadastrarNovoAnalista();
                break;
            case 10:
                mudarAnalistaAtivo();
                break;
            case 11:
                simulador.salvarEstadoDoSistema();
                System.out.println("\nSaindo do simulador. Até a próxima!");
                break;
            default:
                System.out.println("Opção inválida! Escolha entre 1 e 11.");
                break;
        }
    }

    // -----------------------------------------------------------------------
    // H09 — Ranking Geral de Prestígio
    // -----------------------------------------------------------------------

    private void exibirRankingGeral() {
        List<ItemRankingGeral> ranking = simulador.obterRankingGeralOrdenado();

        if (ranking.isEmpty()) {
            System.out.println("\nNenhum analista cadastrado no sistema.");
            return;
        }

        System.out.println("\n==============================================================================");
        System.out.println("------------------------- RANKING GERAL DE PRESTÍGIO -------------------------");
        System.out.println("==============================================================================");
        System.out.printf("%-5s | %-42s | %-15s%n", "Pos", "Analista Esportivo", "Prestígio Total");
        System.out.println("------------------------------------------------------------------------------");

        for (int i = 0; i < ranking.size(); i++) {
            ItemRankingGeral item = ranking.get(i);
            System.out.printf("%2dº   | %-42s | %10d pts%n",
                    (i + 1),
                    item.getNomeAnalista(),
                    item.getPontuacaoTotal());
        }
        System.out.println("==============================================================================");
    }

    // -----------------------------------------------------------------------
    // H08 — Interface de Troca de Contexto e Cadastro
    // -----------------------------------------------------------------------

    private void mudarAnalistaAtivo() {
        List<Analista> lista = simulador.getAnalistas();
        System.out.println("\nPerfis cadastrados no sistema:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + lista.get(i).toString());
        }

        System.out.print("\nDigite exatamente o NOME do analista para assumir o controle: ");
        String nomeEscolhido = LeitorConsole.lerLinha();

        if (nomeEscolhido.isEmpty()) {
            System.out.println("Operação cancelada.");
            return;
        }

        if (simulador.selecionarAnalista(nomeEscolhido)) {
            System.out.println("\n[SUCESSO] O sistema agora está operando sob a perspectiva de: " + nomeEscolhido);
        } else {
            System.out.println("\n[ERRO] Analista '" + nomeEscolhido + "' não encontrado.");
        }
    }

    private void cadastrarNovoAnalista() {
        System.out.print("\nDigite o nome do novo Analista: ");
        String novoNome = LeitorConsole.lerLinha();

        if (novoNome.isEmpty()) {
            System.out.println("Nome não pode ser vazio. Operação cancelada.");
            return;
        }

        if (simulador.cadastrarAnalista(novoNome)) {
            System.out.println("\n[SUCESSO] Analista '" + novoNome + "' criado!");
            System.out.println("O sistema limpou a tela e já ativou o perfil dele como atual.");
        } else {
            System.out.println("\n[ERRO] Já existe um analista cadastrado com o nome '" + novoNome + "'.");
        }
    }

    // -----------------------------------------------------------------------
    // H1 a H7 — Consultas e Cargas
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
        String entrada_str = LeitorConsole.lerLinha().toUpperCase();

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

    private void importarPalpites() {
        System.out.print("\nDigite o nome do arquivo CSV em src/resources/");
        System.out.print("\n(ou pressione Enter para usar '" + CAMINHO_PADRAO_PALPITES + "'): ");
        String nomeArquivo = LeitorConsole.lerLinha();

        String caminho;
        if (nomeArquivo.isEmpty()) {
            caminho = CAMINHO_PADRAO_PALPITES;  
        } else {
            caminho = "src/resources/" + nomeArquivo;
        }

        ResultadoImportacaoPalpites resultado = simulador.importarPalpites(caminho);
        System.out.println("\nForam importados/atualizados " + resultado.getRegistrados() + " palpites para o perfil [" + simulador.getAnalistaAtivo().getNome() + "]!");
        if (resultado.getParciais() > 0) {
            System.out.println("Atenção: " + resultado.getParciais() + " linha(s) ignorada(s) por palpite incompleto (apenas um gol preenchido).");
        }
    }

    private void importarResultadosOficiais() {
        System.out.print("\nDigite o nome do arquivo CSV em src/resources/");
        System.out.print("\n(ou pressione Enter para usar '" + CAMINHO_PADRAO_RESULTADOS + "'): ");
        String nomeArquivo = LeitorConsole.lerLinha();

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

    private void exibirClassificacaoGrupo() {
        System.out.print("\nDigite a letra do grupo (ex: A, B, C): ");
        String entrada_str = LeitorConsole.lerLinha().toUpperCase();

        if (entrada_str.isEmpty()) {
            System.out.println("Entrada vazia. Operação cancelada.");
            return;
        }

        Grupo grupoEscolhido = simulador.buscarGrupo(entrada_str.charAt(0));
        if (grupoEscolhido == null) {
            System.out.println("Grupo '" + entrada_str + "' não encontrado.");
            return;
        }

        System.out.println("\nQual visão da tabela deseja exibir?");
        System.out.println("  1 - Oficial (baseada nos resultados oficiais dos jogos)");
        System.out.println("  2 - Palpites (baseada nos palpites do analista ativo)");
        System.out.print("Opção: ");
        String opcaoVisao = LeitorConsole.lerLinha().trim();

        List<EstatisticasSelecao> classificacao;
        String tituloVisao;

        if ("1".equals(opcaoVisao)) {
            classificacao = grupoEscolhido.getClassificacao();
            tituloVisao = "Oficial";
        } else if ("2".equals(opcaoVisao)) {
            classificacao = grupoEscolhido.getClassificacaoPelosPalpites();
            String nomeAnalista = simulador.getAnalistaAtivo() != null
                    ? simulador.getAnalistaAtivo().getNome()
                    : "Nenhum";
            tituloVisao = "Palpites de [" + nomeAnalista + "]";
        } else {
            System.out.println("Opção inválida. Operação cancelada.");
            return;
        }

        System.out.println("\n--- Tabela de Classificação do Grupo " + entrada_str
                + " (" + tituloVisao + ") ---");
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
    }

    private void exibirPontuacaoPartida() {
        System.out.print("\nDigite o ID da partida: ");
        String entrada_str = LeitorConsole.lerLinha();

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

    private void exibirPontuacaoTotal() {
        List<ResultadoPontuacao> resultados = simulador.calcularPontuacaoTotal();

        if (resultados.isEmpty()) {
            System.out.println("\nNenhuma partida finalizada com palpite encontrada para o analista [" + simulador.getAnalistaAtivo().getNome() + "].");
            return;
        }

        System.out.println("\n----- PONTUAÇÃO TOTAL DOS PALPITES -----");
        System.out.printf("%-10s | %-35s | %-10s | %-6s%n",
                "Partida", "Confronto", "Palpite", "Pontos");
        System.out.println("------------------------------------------------------------------------------");

        for (ResultadoPontuacao resultado : resultados) {
            Partida p = resultado.getPartida();
            String confronto = p.getTimeDaCasa().getNome() + " x " + p.getTimeVisitante().getNome();
            String palpite = p.getPalpite().getGolsTimeCasa() + " x " + p.getPalpite().getGolsTimeVisitante();

            System.out.printf("%-10d | %-35s | %-10s | %6d%n",
                    p.getId(),
                    confronto,
                    palpite,
                    resultado.getTotalPontos());
        }

        int totalPartidas = simulador.somarPontuacao(resultados);
        int totalBonus = simulador.calcularBonusTotalDeClassificacao();
        int prestigioGeral = totalPartidas + totalBonus;

        System.out.println("------------------------------------------------------------------------------");
        System.out.printf("%-62s %6d pts%n", "PONTUAÇÃO DAS PARTIDAS:", totalPartidas);
        System.out.printf("%-62s %6d pts%n", "BÔNUS DE CLASSIFICAÇÃO:", totalBonus);
        System.out.println("------------------------------------------------------------------------------");
        System.out.printf("%-62s %6d pts%n", "PRESTÍGIO TOTAL ACUMULADO:", prestigioGeral);
    }
}