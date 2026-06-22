package br.ufla.copa.core.service;

import java.util.ArrayList;
import java.util.List;

import br.ufla.copa.core.data.CarregadorDeDados;
import br.ufla.copa.core.model.Analista;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Palpite;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.Estadio;

/**
 * Fachada principal do sistema (Padrão Singleton + Facade).
 * Gerencia o ciclo de vida dos Analistas e aplica o padrão Flyweight (Context Switching)
 * para hidratar a base global de partidas com os palpites do usuário ativo (H08).
 */
public class SimuladorAnalistasCopaDoMundo {

    private static SimuladorAnalistasCopaDoMundo instancia;

    private CarregadorDeDados carregador;
    private MotorDePontuacao motorDePontuacao;

    // H08: Repositório em memória dos analistas e ponteiro do usuário logado
    private List<Analista> analistas;
    private Analista analistaAtivo;

    /**
     * Construtor privado — padrão Singleton.
     * Já inicializa um analista padrão ("Júlio") para o sistema não nascer órfão.
     */
    private SimuladorAnalistasCopaDoMundo() {
        this.carregador = new CarregadorDeDados();
        this.motorDePontuacao = new MotorDePontuacao();

        // Tenta puxar o Memory Card do HD
        List<Analista> salvos = carregador.carregarAnalistasDoHD();
        if (salvos != null && !salvos.isEmpty()) {
            this.analistas = salvos;
            this.analistaAtivo = salvos.get(0);
            hidratarPalpitesDoAnalistaAtivo();
        } else {
            this.analistas = new ArrayList<>();
            Analista padrao = new Analista("Júlio");
            this.analistas.add(padrao);
            this.analistaAtivo = padrao;
        }
    }

    public static SimuladorAnalistasCopaDoMundo getInstance() {
        if (instancia == null) {
            instancia = new SimuladorAnalistasCopaDoMundo();
        }
        return instancia;
    }

    // -----------------------------------------------------------------------
    // H08: GERENCIAMENTO DE ANALISTAS E TROCA DE CONTEXTO (FLYWEIGHT)
    // -----------------------------------------------------------------------

    public Analista getAnalistaAtivo() {
        return analistaAtivo;
    }

    public List<Analista> getAnalistas() {
        return analistas;
    }

    /**
     * Motor Flyweight: Limpa os palpites das Partidas globais e pendura 
     * estritamente os palpites guardados no cofre do Analista Ativo.
     */
    public void hidratarPalpitesDoAnalistaAtivo() {
        if (analistaAtivo == null) return;

        for (Partida p : carregador.getPartidas()) {
            // 1. Limpa o palco
            p.setPalpite(null);

            // 2. Veste a roupa do usuário logado se ele tiver dado palpite pra esse jogo
            Palpite palpiteDoCofre = analistaAtivo.getPalpitePara(p.getId());
            if (palpiteDoCofre != null) {
                p.setPalpite(palpiteDoCofre);
            }
        }
    }

    /**
     * Cadastra um novo analista. Retorna false se o nome já existir.
     */
    public boolean cadastrarAnalista(String nome) {
        if (nome == null || nome.trim().isEmpty()) return false;
        String nomeLimpo = nome.trim();

        for (Analista a : analistas) {
            if (a.getNome().equalsIgnoreCase(nomeLimpo)) {
                return false; // Nome já em uso
            }
        }

        Analista novo = new Analista(nomeLimpo);
        this.analistas.add(novo);
        this.analistaAtivo = novo;
        hidratarPalpitesDoAnalistaAtivo(); // Limpa as partidas da tela pro novato
        salvarEstadoDoSistema();
        return true;
    }

    /**
     * Altera o usuário ativo do sistema e engatilha a hidratação visual.
     */
    public boolean selecionarAnalista(String nome) {
        if (nome == null || nome.trim().isEmpty()) return false;
        String nomeLimpo = nome.trim();

        for (Analista a : analistas) {
            if (a.getNome().equalsIgnoreCase(nomeLimpo)) {
                this.analistaAtivo = a;
                hidratarPalpitesDoAnalistaAtivo(); // Carrega os palpites desse cara na tela
                return true;
            }
        }
        return false; // Analista não encontrado
    }

    /**
     * Força a gravação imediata do estado de todos os analistas no HD.
     */
    public void salvarEstadoDoSistema() {
        carregador.salvarAnalistasNoHD(this.analistas);
    }

    /**
     * Ponto de injeção para o leitor de arquivos binários.
     */
    public void carregarRepositorioDeAnalistas(List<Analista> listaRecuperada) {
        if (listaRecuperada != null && !listaRecuperada.isEmpty()) {
            this.analistas = listaRecuperada;
            this.analistaAtivo = listaRecuperada.get(0);
            hidratarPalpitesDoAnalistaAtivo();
        }
    }

    /**
     * H09: Gera o ranking de todos os analistas ordenado por prestígio decrescente.
     * Utiliza o padrão Snapshot (try-finally) para garantir a restauração do usuário ativo.
     */
    public List<ItemRankingGeral> obterRankingGeralOrdenado() {
        List<ItemRankingGeral> ranking = new ArrayList<>();
        if (analistas == null || analistas.isEmpty()) {
            return ranking;
        }

        // 1. Tira a "foto" do crachá do usuário atual (Snapshot)
        Analista analistaOriginal = this.analistaAtivo;

        try {
            // 2. Faz um tour vestindo a roupa de cada analista da base
            for (Analista a : analistas) {
                this.analistaAtivo = a;
                hidratarPalpitesDoAnalistaAtivo();

                if (a.getPalpites().isEmpty()) {
                    ranking.add(new ItemRankingGeral(a.getNome(), 0));
                    continue;
                }

                List<ResultadoPontuacao> resultados = calcularPontuacaoTotal();
                int pontosPartidas = somarPontuacao(resultados);
                int bonusGrupo = calcularBonusTotalDeClassificacao();
                
                int prestigioFinal = pontosPartidas + bonusGrupo;

                ranking.add(new ItemRankingGeral(a.getNome(), prestigioFinal));
            }
        } finally {
            // 3. O bloco finally GARANTE que, mesmo que a matemática de algum analista 
            // dê erro no meio do laço, o usuário original receberá sua roupa de volta.
            this.analistaAtivo = analistaOriginal;
            hidratarPalpitesDoAnalistaAtivo();
        }

        java.util.Collections.sort(ranking);

        return ranking;
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
     * Importa palpites do CSV (H2) e SALVA uma cópia no cofre do Analista Ativo (H8).
     */
    public int importarPalpites(String caminhoArquivo) {
        int qtdImportada = carregador.importarPalpites(caminhoArquivo);
        
        // Interceptador H08: O que entrou na partida global, vai pro cofre do cara
        if (analistaAtivo != null) {
            for (Partida p : carregador.getPartidas()) {
                if (p.temPalpite()) {
                    analistaAtivo.registrarPalpite(p.getId(), p.getPalpite());
                }
            }
        }
        salvarEstadoDoSistema();
        return qtdImportada;
    }

    public void importarResultadosOficiais(String caminhoArquivo) {
        carregador.importarResultadosOficiais(caminhoArquivo);
    }

    // -----------------------------------------------------------------------
    // Classificação (H3 / H4)
    // -----------------------------------------------------------------------

    public List<EstatisticasSelecao> obterClassificacaoGrupo(char nomeGrupo) {
        Grupo grupoEncontrado = buscarGrupo(nomeGrupo);
        if (grupoEncontrado == null) {
            throw new IllegalArgumentException("Grupo não encontrado: " + nomeGrupo);
        }
        return grupoEncontrado.getClassificacao();
    }

    // -----------------------------------------------------------------------
    // Pontuação de palpites (H5, H6 e H7)
    // -----------------------------------------------------------------------

    public ResultadoPontuacao calcularPontuacaoPartida(int idPartida) {
        Partida partida = buscarPartidaPorId(idPartida);
        if (partida == null) {
            throw new IllegalArgumentException("Partida não encontrada: " + idPartida);
        }
        return motorDePontuacao.calcularPontuacaoPartida(partida);
    }

    public List<ResultadoPontuacao> calcularPontuacaoTotal() {
        return motorDePontuacao.calcularPontuacaoTotal(carregador.getPartidas());
    }

    public int somarPontuacao(List<ResultadoPontuacao> resultados) {
        return motorDePontuacao.somarPontuacao(resultados);
    }

    public int calcularBonusTotalDeClassificacao() {
        int totalBonus = 0;
        for (Grupo g : buscarGrupos()) {
            totalBonus += motorDePontuacao.calcularBonusDoGrupo(g);
        }
        return totalBonus;
    }
}