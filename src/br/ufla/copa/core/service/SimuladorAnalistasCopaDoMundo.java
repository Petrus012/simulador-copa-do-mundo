package br.ufla.copa.core.service;

import java.util.List;

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
}