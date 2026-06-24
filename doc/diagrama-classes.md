# Diagrama de Classes — Simulador de Analistas Copa do Mundo 2026

```mermaid
classDiagram

    %% ── INTERFACES ──────────────────────────────────────────────────────────
    class RegraDePontuacaoDePalpite {
        <<interface>>
    }
    class RegraDePontuacaoDeClassificacao {
        <<interface>>
    }

    %% ── ENUMERAÇÃO ───────────────────────────────────────────────────────────
    class StatusPartida {
        <<enumeration>>
    }

    %% ── MODEL ────────────────────────────────────────────────────────────────
    class Selecao
    class Palpite
    class Analista
    class Partida
    class Estadio
    class EstatisticasSelecao
    class Grupo
    class ClassificacaoComparator
    class ResultadoImportacaoPalpites

    %% ── REGRAS DE PONTUAÇÃO ──────────────────────────────────────────────────
    class RegraAbstrataDePalpite {
        <<abstract>>
    }
    class RegraVencedorOuEmpate
    class RegraSaldoDeGols
    class RegraGolsDoVencedor
    class RegraGolsDoPerdedor
    class RegraApostouEmpateNaoFoi
    class RegraPlacarExatoEmpate
    class RegraPosicaoFinal

    %% ── SERVICE ──────────────────────────────────────────────────────────────
    class SimuladorAnalistasCopaDoMundo
    class MotorDePontuacao
    class ResultadoPontuacao
    class ItemRankingGeral

    %% ── DATA ─────────────────────────────────────────────────────────────────
    class CarregadorDeDados

    %% ── UI TERMINAL ──────────────────────────────────────────────────────────
    class MenuTerminal
    class LeitorConsole

    %% ── UI WEB ───────────────────────────────────────────────────────────────
    class MainLayout
    class MainView
    class PalpitesView
    class RankingView
    class GridClassificacao

    %% ── REALIZAÇÕES (implementação de interfaces) ────────────────────────────
    RegraAbstrataDePalpite   ..|> RegraDePontuacaoDePalpite
    RegraPosicaoFinal        ..|> RegraDePontuacaoDeClassificacao

    %% ── HERANÇA ─────────────────────────────────────────────────────────────
    RegraVencedorOuEmpate    --|> RegraAbstrataDePalpite
    RegraSaldoDeGols         --|> RegraAbstrataDePalpite
    RegraGolsDoVencedor      --|> RegraAbstrataDePalpite
    RegraGolsDoPerdedor      --|> RegraAbstrataDePalpite
    RegraApostouEmpateNaoFoi --|> RegraAbstrataDePalpite
    RegraPlacarExatoEmpate   --|> RegraAbstrataDePalpite

    %% ── COMPOSIÇÃO / AGREGAÇÃO (model) ──────────────────────────────────────
    Partida             "1" *-- "2" Selecao
    Partida             "1" o-- "0..1" Palpite
    Partida                 -->       StatusPartida
    Analista            "1" *-- "*"  Palpite
    Grupo               "1" *-- "*"  Selecao
    Grupo               "1" *-- "*"  Partida
    Grupo                   -->       EstatisticasSelecao
    EstatisticasSelecao     -->       Selecao
    ClassificacaoComparator -->       EstatisticasSelecao

    %% ── SERVICE ──────────────────────────────────────────────────────────────
    SimuladorAnalistasCopaDoMundo "1" *-- "1" CarregadorDeDados
    SimuladorAnalistasCopaDoMundo "1" *-- "1" MotorDePontuacao
    SimuladorAnalistasCopaDoMundo "1" *-- "*" Analista
    SimuladorAnalistasCopaDoMundo     -->      ResultadoImportacaoPalpites
    SimuladorAnalistasCopaDoMundo     -->      ItemRankingGeral
    MotorDePontuacao              "1" *-- "*" RegraDePontuacaoDePalpite
    MotorDePontuacao                  -->      RegraDePontuacaoDeClassificacao
    ResultadoPontuacao                -->      Partida
    ResultadoImportacaoPalpites       -->      Palpite

    %% ── DATA ─────────────────────────────────────────────────────────────────
    CarregadorDeDados --> Grupo
    CarregadorDeDados --> Partida
    CarregadorDeDados --> Estadio
    CarregadorDeDados --> Analista

    %% ── UI ───────────────────────────────────────────────────────────────────
    MenuTerminal      --> SimuladorAnalistasCopaDoMundo
    MenuTerminal      --> LeitorConsole
    MenuTerminal      --> ResultadoImportacaoPalpites
    MainView          --> SimuladorAnalistasCopaDoMundo
    PalpitesView      --> SimuladorAnalistasCopaDoMundo
    PalpitesView      --> GridClassificacao
    RankingView       --> SimuladorAnalistasCopaDoMundo
    RankingView       --> ItemRankingGeral
    MainLayout        --> MainView
    MainLayout        --> PalpitesView
    MainLayout        --> RankingView
    GridClassificacao --> EstatisticasSelecao
```
