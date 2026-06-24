package br.ufla.copa.core.model;

import java.util.ArrayList;
import java.util.List;

// Acumula pontos, vitórias, saldo e gols de uma seleção em um grupo,
// incluindo estatísticas de confronto direto para os critérios de desempate (H3)
public class EstatisticasSelecao {
    private Selecao selecao;
    private int pontos;
    private int vitorias;
    private int saldoGols;
    private int golsPro;

    // Listas paralelas de adversários e suas respectivas estatísticas de confronto direto
    private List<Selecao> adversariosConfrontoDirecto;
    private List<Integer> pontosConfrontoDirecto;
    private List<Integer> saldoConfrontoDirecto;
    private List<Integer> golsProConfrontoDirecto;

    public EstatisticasSelecao(Selecao selecao) {
        this.selecao = selecao;
        this.pontos = 0;
        this.vitorias = 0;
        this.saldoGols = 0;
        this.golsPro = 0;

        this.adversariosConfrontoDirecto = new ArrayList<>();
        this.pontosConfrontoDirecto = new ArrayList<>();
        this.saldoConfrontoDirecto = new ArrayList<>();
        this.golsProConfrontoDirecto = new ArrayList<>();
    }

    // Atualiza pontos, vitórias, saldo e gols com o resultado de uma partida do grupo
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

    // Acumula os dados do confronto direto contra um adversário específico (desempate a, b, c)
    public void registrarConfrontoDirecto(Selecao adversario, int golsFeitos, int golsSofridos) {
        int indice = buscarIndiceAdversario(adversario);

        int pontosPartida = 0;
        int saldoPartida = golsFeitos - golsSofridos;

        if (golsFeitos > golsSofridos) {
            pontosPartida = 3;
        } else if (golsFeitos == golsSofridos) {
            pontosPartida = 1;
        }

        if (indice == -1) {
            adversariosConfrontoDirecto.add(adversario);
            pontosConfrontoDirecto.add(pontosPartida);
            saldoConfrontoDirecto.add(saldoPartida);
            golsProConfrontoDirecto.add(golsFeitos);
        } else {
            pontosConfrontoDirecto.set(indice, pontosConfrontoDirecto.get(indice) + pontosPartida);
            saldoConfrontoDirecto.set(indice, saldoConfrontoDirecto.get(indice) + saldoPartida);
            golsProConfrontoDirecto.set(indice, golsProConfrontoDirecto.get(indice) + golsFeitos);
        }
    }

    // Retorna os pontos obtidos no confronto direto contra o adversário informado
    public int getPontosConfrontoDirecto(Selecao adversario) {
        int indice = buscarIndiceAdversario(adversario);
        if (indice == -1) return 0;
        return pontosConfrontoDirecto.get(indice);
    }

    // Retorna o saldo de gols no confronto direto contra o adversário informado
    public int getSaldoConfrontoDirecto(Selecao adversario) {
        int indice = buscarIndiceAdversario(adversario);
        if (indice == -1) return 0;
        return saldoConfrontoDirecto.get(indice);
    }

    // Retorna os gols marcados no confronto direto contra o adversário informado
    public int getGolsProConfrontoDirecto(Selecao adversario) {
        int indice = buscarIndiceAdversario(adversario);
        if (indice == -1) return 0;
        return golsProConfrontoDirecto.get(indice);
    }

    private int buscarIndiceAdversario(Selecao adversario) {
        for (int i = 0; i < adversariosConfrontoDirecto.size(); i++) {
            if (adversariosConfrontoDirecto.get(i).equals(adversario)) {
                return i;
            }
        }
        return -1;
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
