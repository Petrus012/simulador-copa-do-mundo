package br.ufla.copa.core.service;

import br.ufla.copa.core.contracts.RegraDePontuacaoDePalpite;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.StatusPartida;
import br.ufla.copa.core.rules.RegraApostouEmpateNaoFoi;
import br.ufla.copa.core.rules.RegraGolsDoPerdedor;
import br.ufla.copa.core.rules.RegraGolsDoVencedor;
import br.ufla.copa.core.rules.RegraPlacarExatoEmpate;
import br.ufla.copa.core.rules.RegraPosicaoFinal;
import br.ufla.copa.core.rules.RegraSaldoDeGols;
import br.ufla.copa.core.rules.RegraVencedorOuEmpate;
import br.ufla.copa.core.contracts.RegraDePontuacaoDeClassificacao;
import br.ufla.copa.core.model.Selecao;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de pontuação extensível (H5).
 * Mantém uma coleção de regras e as percorre para calcular a pontuação
 * de um palpite em uma partida, sem nenhuma estrutura if/else sobre
 * qual regra aplicar — basta adicionar uma nova instância à lista para
 * que ela passe a ser considerada automaticamente.
 */
public class MotorDePontuacao {

    private List<RegraDePontuacaoDePalpite> regras;

    /**
     * Constrói o motor com o conjunto padrão de regras (R1 a R5).
     */
    public MotorDePontuacao() {
        this.regras = new ArrayList<>();
        this.regras.add(new RegraVencedorOuEmpate());
        this.regras.add(new RegraSaldoDeGols());
        this.regras.add(new RegraGolsDoVencedor());
        this.regras.add(new RegraGolsDoPerdedor());
        this.regras.add(new RegraApostouEmpateNaoFoi());
        this.regras.add(new RegraPlacarExatoEmpate()); 
    }

    /**
     * Calcula a pontuação obtida em uma partida específica (H5).
     * Retorna null se a partida não tiver palpite ou não estiver finalizada.
     *
     * @param partida a partida a ser avaliada
     * @return resultado detalhado com pontos por regra, ou null se inaplicável
     */
    public ResultadoPontuacao calcularPontuacaoPartida(Partida partida) {
        if (!partida.temPalpite() || partida.getStatus() != StatusPartida.FINALIZADA) {
            return null;
        }

        int palpiteCasa = partida.getPalpite().getGolsTimeCasa();
        int palpiteVisitante = partida.getPalpite().getGolsTimeVisitante();
        int oficialCasa = partida.getGolsTimeCasaOficial();
        int oficialVisitante = partida.getGolsTimeVisitanteOficial();

        ResultadoPontuacao resultado = new ResultadoPontuacao(partida);

        for (RegraDePontuacaoDePalpite regra : regras) {
            int pontos = regra.calcular(palpiteCasa, palpiteVisitante, oficialCasa, oficialVisitante);
            resultado.adicionarRegra(regra.getIdentificador(), regra.getDescricao(), pontos);
        }

        return resultado;
    }

    /**
     * Calcula a pontuação total somando todas as partidas com palpite e
     * resultado oficial (H6). Retorna uma lista de resultados individuais,
     * um por partida pontuável.
     *
     * @param partidas lista completa de partidas
     * @return lista de ResultadoPontuacao para cada partida avaliável
     */
    public List<ResultadoPontuacao> calcularPontuacaoTotal(List<Partida> partidas) {
        List<ResultadoPontuacao> resultados = new ArrayList<>();

        for (Partida partida : partidas) {
            ResultadoPontuacao resultado = calcularPontuacaoPartida(partida);
            if (resultado != null) {
                resultados.add(resultado);
            }
        }

        return resultados;
    }

    /**
     * Soma os pontos de uma lista de resultados (H6).
     */
    public int somarPontuacao(List<ResultadoPontuacao> resultados) {
        int total = 0;
        for (ResultadoPontuacao r : resultados) {
            total += r.getTotalPontos();
        }
        return total;
    }

    public List<RegraDePontuacaoDePalpite> getRegras() {
        return regras;
    }

    /**
     * Calcula o bônus total de Posição Final de um Grupo (H07).
     * Retorna 0 se o grupo tiver ao menos uma partida não finalizada ("Tudo ou Nada").
     */
    public int calcularBonusDoGrupo(Grupo grupo) {
        // Regra de segurança da H7: Todos os jogos do grupo têm que estar finalizados
        for (Partida p : grupo.getPartidas()) {
            if (p.getStatus() != StatusPartida.FINALIZADA) {
                return 0; 
            }
            if (!p.temPalpite()) {
                return 0; 
            }
        }

        List<EstatisticasSelecao> oficial = grupo.getClassificacao();
        List<EstatisticasSelecao> palpitada = grupo.getClassificacaoPelosPalpites();

        int totalBonusGrupo = 0;
        RegraDePontuacaoDeClassificacao regraPosicao = new RegraPosicaoFinal();

        // Compara os 3 primeiros colocados (índices 0, 1 e 2)
        for (int i = 0; i < Math.min(3, Math.min(oficial.size(), palpitada.size())); i++) {
            Selecao selecaoOficial = oficial.get(i).getSelecao();

            int posicaoNoPalpite = buscarPosicaoDaSelecao(palpitada, selecaoOficial);

            // Passamos (i + 1) porque a interface do professor exige posição humana (1º, 2º, 3º)
            totalBonusGrupo += regraPosicao.calcularPontosPorPosicao(posicaoNoPalpite, (i + 1));
        }

        return totalBonusGrupo;
    }

    private int buscarPosicaoDaSelecao(List<EstatisticasSelecao> lista, Selecao alvo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getSelecao().equals(alvo)) {
                return (i + 1);
            }
        }
        return -1;
    }

}