package br.ufla.copa.core.service;

import java.util.ArrayList;
import java.util.List;

import br.ufla.copa.core.contracts.RegraPosicaoFinalNoGrupo;
import br.ufla.copa.core.data.CarregadorDeDados;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.Estadio;

/**
 * Fachada principal do sistema (Padrão Singleton + Facade).
 * Conecta a interface do usuário com a camada de dados e os serviços de negócio.
 */
public class SimuladorAnalistasCopaDoMundo {

    private static SimuladorAnalistasCopaDoMundo instancia;

    private CarregadorDeDados carregador;
    private MotorDePontuacao motorDePontuacao;

    /**
     * Construtor privado — padrão Singleton.
     * Já inicializa o carregador de dados (partidas) e o motor de pontuação.
     */
    private SimuladorAnalistasCopaDoMundo() {
        this.carregador = new CarregadorDeDados();
        this.motorDePontuacao = new MotorDePontuacao();
    }

    /**
     * Retorna a instância única da classe (Padrão Singleton).
     */
    public static SimuladorAnalistasCopaDoMundo getInstance() {
        if (instancia == null) {
            instancia = new SimuladorAnalistasCopaDoMundo();
        }
        return instancia;
    }

    // -----------------------------------------------------------------------
    // Consultas de dados
    // -----------------------------------------------------------------------

    public List<Estadio> buscarEstadios() {
        return carregador.getEstadios();
    }

    public List<Grupo> buscarGrupos() {
        return carregador.getGrupos();
    }

    public Grupo buscarGrupo(char letraGrupo) {
        for (Grupo g : carregador.getGrupos()) {
            if (g.getNome() == letraGrupo) {
                return g;
            }
        }
        return null;
    }

    public Partida buscarPartidaPorId(int id) {
        for (Partida p : carregador.getPartidas()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Importação (H2 e H4)
    // -----------------------------------------------------------------------

    /**
     * Importa palpites de um arquivo CSV (H2).
     * @return quantidade de palpites importados com sucesso
     */
    public int importarPalpites(String caminhoArquivo) {
        return carregador.importarPalpites(caminhoArquivo);
    }

    /**
     * Importa resultados oficiais de um arquivo CSV (H4).
     * Partidas importadas ficam com status FINALIZADA e passam a bloquear novos palpites.
     */
    public void importarResultadosOficiais(String caminhoArquivo) {
        carregador.importarResultadosOficiais(caminhoArquivo);
    }

    // -----------------------------------------------------------------------
    // Classificação (H3 / H4)
    // -----------------------------------------------------------------------

    /**
     * Retorna a tabela de classificação já calculada e ordenada de um grupo.
     * Usa resultados oficiais quando disponíveis; palpites caso contrário.
     */
    public List<EstatisticasSelecao> obterClassificacaoGrupo(char nomeGrupo) {
        Grupo grupoEncontrado = buscarGrupo(nomeGrupo);
        if (grupoEncontrado == null) {
            throw new IllegalArgumentException("Grupo não encontrado: " + nomeGrupo);
        }
        return grupoEncontrado.getClassificacao();
    }

    // -----------------------------------------------------------------------
    // Pontuação de palpites (H5 e H6)
    // -----------------------------------------------------------------------

    /**
     * Calcula a pontuação detalhada do palpite em uma partida específica (H5).
     * Retorna null se a partida não tiver palpite ou não estiver finalizada.
     */
    public ResultadoPontuacao calcularPontuacaoPartida(int idPartida) {
        Partida partida = buscarPartidaPorId(idPartida);
        if (partida == null) {
            throw new IllegalArgumentException("Partida não encontrada: " + idPartida);
        }
        return motorDePontuacao.calcularPontuacaoPartida(partida);
    }

    /**
     * Calcula a pontuação de todos os palpites sobre partidas finalizadas (H6).
     * @return lista de ResultadoPontuacao, uma entrada por partida pontuável
     */
    public List<ResultadoPontuacao> calcularPontuacaoTotal() {
        return motorDePontuacao.calcularPontuacaoTotal(carregador.getPartidas());
    }

    /**
     * Soma os pontos de uma lista de resultados (H6).
     */
    public int somarPontuacao(List<ResultadoPontuacao> resultados) {
        return motorDePontuacao.somarPontuacao(resultados);
    }

    // -----------------------------------------------------------------------
    // Bônus de posição final no grupo (H7)
    // -----------------------------------------------------------------------

    /**
     * Calcula o bônus de posição final para todos os grupos finalizados (H7).
     * Para cada grupo em que todas as partidas estão com status FINALIZADA,
     * compara a classificação oficial com a prevista pelos palpites e pontua
     * usando RegraPosicaoFinalNoGrupo (1º=15pts, 2º=10pts, 3º=5pts).
     *
     * @return lista com um ResultadoBonusPosicao por grupo finalizado
     */
    public List<ResultadoBonusPosicao> calcularBonusPosicao() {
        List<ResultadoBonusPosicao> resultados = new ArrayList<>();
        RegraPosicaoFinalNoGrupo regra = new RegraPosicaoFinalNoGrupo();

        for (Grupo grupo : carregador.getGrupos()) {
            if (!grupo.isGrupoFinalizado()) {
                continue;
            }

            List<EstatisticasSelecao> classificacaoOficial  = grupo.getClassificacao();
            List<EstatisticasSelecao> classificacaoPalpites = grupo.getClassificacaoPorPalpites();

            ResultadoBonusPosicao resultado = new ResultadoBonusPosicao(grupo.getNome());

            // Pontua apenas as 3 primeiras posições oficiais
            for (int posOficial = 1; posOficial <= 3 && posOficial <= classificacaoOficial.size(); posOficial++) {
                EstatisticasSelecao selecaoOficial = classificacaoOficial.get(posOficial - 1);
                String nomeSelecao = selecaoOficial.getSelecao().getNome();

                // Descobre em qual posição o analista colocou essa seleção
                int posPalpite = buscarPosicaoNaLista(classificacaoPalpites, nomeSelecao);

                int pontos = regra.calcularPontosPorPosicao(posPalpite, posOficial);
                resultado.adicionarPosicao(posOficial, nomeSelecao, posPalpite, pontos);
            }

            resultados.add(resultado);
        }

        return resultados;
    }

    /**
     * Soma os pontos de bônus de posição de todos os grupos (H7).
     */
    public int somarBonusPosicao(List<ResultadoBonusPosicao> resultados) {
        int total = 0;
        for (ResultadoBonusPosicao r : resultados) {
            total += r.getTotalPontos();
        }
        return total;
    }

    /** Retorna a posição (1-based) de uma seleção pelo nome numa lista ordenada. */
    private int buscarPosicaoNaLista(List<EstatisticasSelecao> lista, String nomeSelecao) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getSelecao().getNome().equals(nomeSelecao)) {
                return i + 1;
            }
        }
        return -1; // seleção sem palpite — sem bônus
    }
}