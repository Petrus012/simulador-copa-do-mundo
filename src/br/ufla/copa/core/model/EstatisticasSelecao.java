package br.ufla.copa.core.model;

public class EstatisticasSelecao {
    private Selecao selecao;
    private int pontos;
    private int vitorias;
    private int saldoGols;
    private int golsPro;

    public EstatisticasSelecao(Selecao selecao) {
        this.selecao = selecao;
        this.pontos = 0;
        this.vitorias = 0;
        this.saldoGols = 0;
        this.golsPro = 0;
    }

    public void registrarResultado(int golsFeitos, int golsSofridos) {
        this.golsPro += golsFeitos;
        this.saldoGols += (golsFeitos - golsSofridos);

        if (golsFeitos > golsSofridos) {
            this.pontos += 3;
            this.vitorias++;
        } else if (golsFeitos == golsSofridos) {
            this.pontos += 1;
        }
    }

    public Selecao getSelecao() {
        return selecao;
    }

    public int getPontos() {
        return pontos;
    }

    public int getVitorias() {
        return vitorias;
    }

    public int getSaldoGols() {
        return saldoGols;
    }

    public int getGolsPro() {
        return golsPro;
    }
}