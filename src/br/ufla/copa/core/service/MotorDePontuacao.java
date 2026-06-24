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

// Motor extensível de pontuação (H5): percorre a lista de regras sem if/else por tipo;
// basta adicionar uma instância à lista para ela ser considerada automaticamente
public class MotorDePontuacao {

    private List<RegraDePontuacaoDePalpite> regras;

    // Inicializa com o conjunto padrão de regras R1–R6
    public MotorDePontuacao() {
        this.regras = new ArrayList<>();
        this.regras.add(new RegraVencedorOuEmpate());
        this.regras.add(new RegraSaldoDeGols());
        this.regras.add(new RegraGolsDoVencedor());
        this.regras.add(new RegraGolsDoPerdedor());
        this.regras.add(new RegraApostouEmpateNaoFoi());
        this.regras.add(new RegraPlacarExatoEmpate());
    }

    // Calcula a pontuação de um palpite em uma partida (H5);
    // retorna null se a partida não tiver palpite ou não estiver finalizada
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

    // Calcula a pontuação total de todas as partidas com palpite e resultado oficial (H6)
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

    // Soma os pontos de uma lista de resultados (H6)
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

    // Calcula o bônus de classificação de um grupo (H7);
    // retorna 0 se qualquer partida do grupo não estiver finalizada ou sem palpite
    public int calcularBonusDoGrupo(Grupo grupo) {
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

        // Compara os 3 primeiros colocados (posição humana: 1º, 2º, 3º)
        for (int i = 0; i < Math.min(3, Math.min(oficial.size(), palpitada.size())); i++) {
            Selecao selecaoOficial = oficial.get(i).getSelecao();

            int posicaoNoPalpite = buscarPosicaoDaSelecao(palpitada, selecaoOficial);

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
