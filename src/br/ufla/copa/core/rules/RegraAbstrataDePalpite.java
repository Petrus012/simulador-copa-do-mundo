package br.ufla.copa.core.rules;

import br.ufla.copa.core.contracts.RegraDePontuacaoDePalpite;

/**
 * Base comum às regras R1–R6.
 * Armazena identificador e descrição via construtor, eliminando
 * a repetição de getIdentificador()/getDescricao() em cada subclasse.
 * Subclasses só precisam implementar calcular().
 */
public abstract class RegraAbstrataDePalpite implements RegraDePontuacaoDePalpite {

    private final String identificador;
    private final String descricao;

    protected RegraAbstrataDePalpite(String identificador, String descricao) {
        this.identificador = identificador;
        this.descricao = descricao;
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
