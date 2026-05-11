# Simulador Analistas Esportivos - Copa do Mundo

Este projeto foi preparado como código inicial do trabalho da disciplina de 
Práticas de Programação Orientada a Objetos, do ICET/UFLA.

## Orientação Inicial

Antes de ler as instruções abaixo, leia as 
[instruções do trabalho](https://docs.google.com/document/d/16fR_zarTxbiw61fgJb5nmCv8nuqucYhojHL69sbRGpY/edit?usp=sharing) 
que estão na página do Campus Virtual da disciplina.

Não se assustem com a quantidade de código ou arquivos desse projeto inicial. 
Ele foi estruturado para facilitar o desenvolvimento, principalmente,
da parte final do trabalho que envolve a interface web.

Mas, na maior parte do trabalho, vocês vão implementar apenas as classes de 
regras de negócio e da interface via terminal.

Portanto, a recomendação é:

1. Implementem as histórias de usuário das instruções do trabalho na ordem em
   que foram apresentadas. 
2. Com isso, vocês vão desenvolver quase tudo testando pela interface de terminal.
2. Somente na etapa final precisarão tratar a interface web.
3. E não é necessário dominar Maven ou Vaadin Flow para fazer essa parte.

**ATENÇÃO**: Não esqueça de preencher o checklist das entregas no final deste arquivo.

### Dicas adicionais

- Façam commits pequenos e frequentes.
- Testem cada mudanca no terminal antes de avançar.
- Na parte web, apenas integrem funcionalidade já validada, em vez de criarem lógica 
  nova direto na tela.

## Como executar o projeto

### Opção recomendada no VS Code

- Pressione `F5`.
- A configuracao do projeto inicia tanto o terminal quando a parte web de apoio.
- A interface web poderá ser acessada pelo navegador através do endereço: `http://localhost:8080`
  
  (mas como dito anteriormente, você pode ignorar a parte web até a etapa final do trabalho).

**ATENÇÃO**: a primeira execução deve demorar um pouco porque serão baixadas as 
dependências (bibliotecas) necessárias (e você precisa estar conectado a internet para isso acontecer).

### Caso queira rodar só a interface web

Execute o comando:

```bash
./mvnw jetty:run
```

No Windows (PowerShell ou Prompt), use:

```powershell
.\\mvnw.cmd jetty:run
```

## Estrutura de pastas em src

```text
doc/
src/
  MainApp.java  
  br/ufla/copa/
    core/
      contracts/
      model/
      service/
    ui/
      terminal/
      web/
  resources/
```

### Para que serve cada pasta

- `doc`
  - Pasta onde deve ser colocado o diagrama de classes UML simplificado.

- `src/MainApp.java`
  - Ponto de entrada da aplicacao.
  - Inicia o fluxo principal no terminal.

- `src/br/ufla/copa/core/contracts`
  - Interfaces de regras (contratos).
  - Define "o que" precisa ser implementado, sem prender ao "como".
  - Essas interfaces **não podem ser alteradas**.

- `src/br/ufla/copa/core/model`
  - Entidades do dominio (ex.: `Estadio`).
  - Representam os dados do sistema.
  - A classe `Estadio` foi colocada apenas como exemplo, podendo ser alterada ou removida conforme necessário.

- `src/br/ufla/copa/core/service`
  - Coordenação da regra de negócio (ex.: simulador).
  - Aqui ficam os métodos que orquestram os casos de uso.
  - A classe disponibilizada é só um exemplo, podendo ser alterada conforme necessário.

- `src/br/ufla/copa/ui/terminal`
  - Interface textual para executar e validar o sistema no dia a dia.
  - Este será seu principal ambiente de teste durante o desenvolvimento.
  - Altere a classe disponibilizada para que o menu funcione conforme desejado.

- `src/br/ufla/copa/ui/web`
  - Interface web com Vaadin Flow.
  - Recomendado focar nela apenas depois que a parte de negócio estiver consistente.
  - Altere a classe para representar a tela principal, integrando as funcionalidades já testadas no terminal.

- `src/resources`
  - Pasta para arquivos de configuração, imagens ou outros recursos que o projeto possa precisar.
  - É nessa pasta que se encontra o arquivo `partidas.csv` com os dados das partidas da primeira fase da Copa do Mundo.


## Resumo rápido sobre Maven e Vaadin Flow

### Maven

Maven é uma ferramenta que organiza o projeto Java e automatiza tarefas comuns, como:

1. Baixar bibliotecas usadas pelo projeto.
2. Compilar o código.
3. Executar plugins e montar a aplicação.

Em outras palavras: ele evita configurações manuais repetitivas.

Voce não precisa aprender Maven para fazer o trabalho, pois ele já está configurado nesse projeto-base.

### Vaadin Flow

Vaadin Flow permite criar interface web usando Java (sem precisar escrever front-end em JavaScript, por exemplo).

Neste projeto inicial:

1. A tela web principal está em `ui/web/MainView.java`.
2. Ela traz exemplos de uso de componentes como `Grid`, `Button` e `TextField` montados no código Java.
3. Você pode alterar totalmente a tela conforme precisar.

## Checklist das entregas

**Preencha a coluna _Respostas_** das tabelas abaixo **antes de fazer as entregas**.

- A tabelas devem ser preenchidas na primeira entrega e depois atualizadas na entrega final.

**Histórias de Usuário**

|  Id |  Descrição                                                  |  Implementada     | 
|-----|-------------------------------------------------------------|-------------------|
| H01 | Inicialização da Tabela de Partidas                         | (sim/não/parcial) |
| H02 | Registro de Palpites                                        | (sim/não/parcial) |
| H03 | Tabela de Classificação do Grupo                            | (sim/não/parcial) |
| H04 | Carregamento dos Resultados Oficiais                        | (sim/não/parcial) |
| H05 | Pontuação do Palpite de Uma Partida                         | (sim/não/parcial) |
| H06 | Pontuação de Todos os Palpites                              | (sim/não/parcial) |
| H07 | Pontuação pela Classificação na primeira fase               | (sim/não/parcial) |
| H08 | Criação de Perfil de Analista e Persistência dos Palpites   | (sim/não/parcial) |
| H09 | Ranking Geral de Prestígio                                  | (sim/não/parcial) |
| H10 | Sincronização Online de Resultados Oficiais                 | (sim/não/parcial) |
| H11 | Diagrama de Classes Simplificado e Checklist                | (sim/não/parcial) |
| H12 | Dashboard de Jogos e Resultados Oficiais                    | (sim/não/parcial) |
| H13 | Registro de Palpites via Web                                | (sim/não/parcial) |
| H14 | Ranking Comparativo de Analistas                            | (sim/não/parcial) |
| H15 | Atualização do Diagrama de Classes Simplificado e Checklist | (sim/não/parcial) |

**Requisitos de OO**

|  Descrição                                           |  Respostas | 
|------------------------------------------------------|------------|
| Usa corretamente conceitos básicos de OO             | (sim/não)  |  
| Usa corretamente encapsulamento                      | (sim/não)  |
| Usa corretamente composição, agregação e associação  | (sim/não)  |
| Tem bom design de classes                            | (sim/não)  |
| Tem divisão de camadas                               | (sim/não)  |
| Usa corretamente herança                             | (sim/não)  |
| Usa corretamente polimorfismo                        | (sim/não)  |
| Usa tratamento de exceçãos                           | (sim/não)  | 
| Usa Java versão 21                                   | (sim/não)  |
| Código compila                                       | (sim/não)  |
| Código está organizado, legível e bem comentado      | (sim/não)  |
