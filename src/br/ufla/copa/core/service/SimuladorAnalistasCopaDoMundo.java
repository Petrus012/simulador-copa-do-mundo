package br.ufla.copa.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.ufla.copa.core.data.CarregadorDeDados;
import br.ufla.copa.core.model.Analista;
import br.ufla.copa.core.model.EstatisticasSelecao;
import br.ufla.copa.core.model.Estadio;
import br.ufla.copa.core.model.Grupo;
import br.ufla.copa.core.model.Palpite;
import br.ufla.copa.core.model.Partida;
import br.ufla.copa.core.model.ResultadoImportacaoPalpites;
import br.ufla.copa.core.model.StatusPartida;

// Fachada singleton que coordena todas as operações do sistema (H1–H10)
public class SimuladorAnalistasCopaDoMundo {

    private static SimuladorAnalistasCopaDoMundo instancia;

    private CarregadorDeDados carregador;
    private MotorDePontuacao motorDePontuacao;

    private List<Analista> analistas;
    private Analista analistaAtivo;
    private boolean sincronizacaoWebAtivada = false;

    private SimuladorAnalistasCopaDoMundo() {
        this.carregador = new CarregadorDeDados();
        this.motorDePontuacao = new MotorDePontuacao();

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

    public Analista getAnalistaAtivo() {
        return analistaAtivo;
    }

    public List<Analista> getAnalistas() {
        return analistas;
    }

    // Carrega os palpites do cofre do analista ativo nas partidas correspondentes
    public void hidratarPalpitesDoAnalistaAtivo() {
        if (analistaAtivo == null) {
            return;
        }

        for (Partida p : carregador.getPartidas()) {
            p.limparPalpite();

            Palpite palpiteDoCofre = analistaAtivo.getPalpitePara(p.getId());
            if (palpiteDoCofre != null) {
                p.setPalpite(palpiteDoCofre);
            }
        }
    }

    // Cadastra novo analista, torna-o ativo e persiste (H8)
    public boolean cadastrarAnalista(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        String nomeLimpo = nome.trim();

        for (Analista a : analistas) {
            if (a.getNome().equalsIgnoreCase(nomeLimpo)) {
                return false;
            }
        }

        Analista novo = new Analista(nomeLimpo);
        this.analistas.add(novo);
        this.analistaAtivo = novo;
        hidratarPalpitesDoAnalistaAtivo();
        salvarEstadoDoSistema();
        return true;
    }

    // Troca o analista ativo e hidrata seus palpites nas partidas (H8)
    public boolean selecionarAnalista(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        String nomeLimpo = nome.trim();

        for (Analista a : analistas) {
            if (a.getNome().equalsIgnoreCase(nomeLimpo)) {
                this.analistaAtivo = a;
                hidratarPalpitesDoAnalistaAtivo();
                return true;
            }
        }
        return false;
    }

    public void salvarEstadoDoSistema() {
        carregador.salvarAnalistasNoHD(this.analistas);
    }

    public void carregarRepositorioDeAnalistas(List<Analista> listaRecuperada) {
        if (listaRecuperada != null && !listaRecuperada.isEmpty()) {
            this.analistas = listaRecuperada;
            this.analistaAtivo = listaRecuperada.get(0);
            hidratarPalpitesDoAnalistaAtivo();
        }
    }

    // Calcula o ranking de todos os analistas ordenado por prestígio decrescente (H9)
    public List<ItemRankingGeral> obterRankingGeralOrdenado() {
        List<ItemRankingGeral> ranking = new ArrayList<>();
        if (analistas == null || analistas.isEmpty()) {
            return ranking;
        }

        Analista analistaOriginal = this.analistaAtivo;

        try {
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
            // Garante que o analista original seja restaurado mesmo em caso de erro
            this.analistaAtivo = analistaOriginal;
            hidratarPalpitesDoAnalistaAtivo();
        }

        Collections.sort(ranking);
        return ranking;
    }

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

    // Importa palpites do CSV e registra no cofre do analista ativo (H2)
    public ResultadoImportacaoPalpites importarPalpites(String caminhoArquivo) {
        ResultadoImportacaoPalpites resultado = carregador.importarPalpites(caminhoArquivo);

        if (analistaAtivo != null) {
            salvarTodosPalpitesDoCsvNoCofre(resultado);
        }
        hidratarPalpitesDoAnalistaAtivo();
        salvarEstadoDoSistema();
        return resultado;
    }

    // Salva no cofre apenas os palpites de partidas ainda não ocorridas;
    // contabiliza os bloqueados por resultado oficial e por snapshot de sync
    private void salvarTodosPalpitesDoCsvNoCofre(ResultadoImportacaoPalpites resultado) {
        int bloqueadosPorResultado = 0;
        int bloqueadosPorSnapshot = 0;
        for (Map.Entry<Integer, Palpite> entrada : resultado.getPalpitesLidos().entrySet()) {
            int id = entrada.getKey();
            Partida partida = buscarPartidaPorId(id);
            if (partida == null) continue;
            if (carregador.isNoSnapshot(id)) {
                bloqueadosPorSnapshot++;
            } else if (partida.getStatus() == StatusPartida.FINALIZADA) {
                bloqueadosPorResultado++;
            } else {
                analistaAtivo.registrarPalpite(id, entrada.getValue());
            }
        }
        resultado.setBloqueadosPorResultado(bloqueadosPorResultado);
        resultado.setBloqueadosPorSnapshot(bloqueadosPorSnapshot);
    }

    public void importarResultadosOficiais(String caminhoArquivo) {
        carregador.importarResultadosOficiais(caminhoArquivo);
    }

    // Registra ou atualiza um palpite via UI; bloqueia partidas finalizadas ou no snapshot (H2)
    public boolean atualizarPalpite(int idPartida, int golsCasa, int golsVisitante) {
        if (golsCasa < 0 || golsVisitante < 0) {
            return false;
        }
        Partida partida = buscarPartidaPorId(idPartida);
        if (partida == null || partida.getStatus() == StatusPartida.FINALIZADA
                || carregador.isNoSnapshot(idPartida)) {
            return false;
        }
        Palpite novoPalpite = new Palpite(golsCasa, golsVisitante);
        analistaAtivo.registrarPalpite(idPartida, novoPalpite);
        partida.setPalpite(novoPalpite);
        return true;
    }

    public List<EstatisticasSelecao> obterClassificacaoGrupo(char nomeGrupo) {
        Grupo grupoEncontrado = buscarGrupo(nomeGrupo);
        if (grupoEncontrado == null) {
            throw new IllegalArgumentException("Grupo não encontrado: " + nomeGrupo);
            }
        return grupoEncontrado.getClassificacao();
    }

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

    public List<Partida> getPartidas() {
        return carregador.getPartidas();
    }

    // Retorna true apenas se todas as 72 partidas do torneio estiverem FINALIZADAS
    public boolean todasPartidasFinalizadas() {
        for (Partida p : carregador.getPartidas()) {
            if (p.getStatus() != StatusPartida.FINALIZADA) {
                return false;
            }
        }
        return true;
    }

    // Soma o bônus de classificação de todos os grupos (H7)
    public int calcularBonusTotalDeClassificacao() {
        int totalBonus = 0;
        for (Grupo g : buscarGrupos()) {
            totalBonus += motorDePontuacao.calcularBonusDoGrupo(g);
        }
        return totalBonus;
    }

    public boolean isSincronizacaoWebAtivada() {
        return sincronizacaoWebAtivada;
    }

    // Alterna o modo online/offline: ON salva snapshot e sincroniza;
    // OFF restaura o snapshot e reidrata os palpites do analista ativo (H10)
    public void alternarSincronizacaoWeb() {
        this.sincronizacaoWebAtivada = !this.sincronizacaoWebAtivada;
        if (this.sincronizacaoWebAtivada) {
            System.out.println("\n[Rede] Sincronização Web ATIVADA. Buscando placares ao vivo...");
            carregador.salvarSnapshotResultados();
            carregador.sincronizarResultadosOnline();
        } else {
            System.out.println("\n[Rede] Sincronização Web DESATIVADA. Restaurando resultados anteriores...");
            carregador.restaurarSnapshotResultados();
            hidratarPalpitesDoAnalistaAtivo();
        }
    }
}
