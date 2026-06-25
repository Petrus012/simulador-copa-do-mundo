# Diagrama de Classes — Simulador de Analistas Copa do Mundo 2026

```mermaid
classDiagram
    class RegraDePontuacaoDePalpite {
        <<interface>>
    }
    class RegraDePontuacaoDeClassificacao {
        <<interface>>
    }
    class StatusPartida {
        <<enumeration>>
    }

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

    class Selecao
    class Palpite
    class Analista
    class Partida
    class Estadio
    class EstatisticasSelecao
    class Grupo
    class ClassificacaoComparator
    class ResultadoImportacaoPalpites

    class SimuladorAnalistasCopaDoMundo
    class MotorDePontuacao
    class ResultadoPontuacao
    class ItemRankingGeral
    class CarregadorDeDados

    class MenuTerminal
    class LeitorConsole
    class MainLayout
    class MainView
    class PalpitesView
    class RankingView
    class GridClassificacao

    RegraAbstrataDePalpite ..|> RegraDePontuacaoDePalpite
    RegraPosicaoFinal ..|> RegraDePontuacaoDeClassificacao

    RegraVencedorOuEmpate --|> RegraAbstrataDePalpite
    RegraSaldoDeGols --|> RegraAbstrataDePalpite
    RegraGolsDoVencedor --|> RegraAbstrataDePalpite
    RegraGolsDoPerdedor --|> RegraAbstrataDePalpite
    RegraApostouEmpateNaoFoi --|> RegraAbstrataDePalpite
    RegraPlacarExatoEmpate --|> RegraAbstrataDePalpite

    Partida *-- Selecao
    Partida --> StatusPartida
    Analista *-- Palpite
    Grupo *-- Selecao
    Grupo *-- Partida
    Grupo --> EstatisticasSelecao
    EstatisticasSelecao --> Selecao
    ClassificacaoComparator --> EstatisticasSelecao

    SimuladorAnalistasCopaDoMundo *-- CarregadorDeDados
    SimuladorAnalistasCopaDoMundo *-- MotorDePontuacao
    SimuladorAnalistasCopaDoMundo *-- Analista
    SimuladorAnalistasCopaDoMundo --> ResultadoImportacaoPalpites
    SimuladorAnalistasCopaDoMundo --> ItemRankingGeral
    MotorDePontuacao --> RegraDePontuacaoDePalpite
    MotorDePontuacao --> RegraDePontuacaoDeClassificacao
    ResultadoPontuacao --> Partida
    ResultadoImportacaoPalpites --> Palpite

    CarregadorDeDados --> Grupo
    CarregadorDeDados --> Partida
    CarregadorDeDados --> Estadio
    CarregadorDeDados --> Analista

    MenuTerminal --> SimuladorAnalistasCopaDoMundo
    MenuTerminal --> LeitorConsole
    MainView --> SimuladorAnalistasCopaDoMundo
    PalpitesView --> SimuladorAnalistasCopaDoMundo
    PalpitesView --> GridClassificacao
    RankingView --> SimuladorAnalistasCopaDoMundo
    RankingView --> ItemRankingGeral
    MainLayout --> MainView
    MainLayout --> PalpitesView
    MainLayout --> RankingView
    GridClassificacao --> EstatisticasSelecao
```
