<div align="center">

# EstoqueFlow

**Sistema desktop de controle e movimentação de estoque desenvolvido em Java.**

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Interface-Java%20Swing-305C79?style=flat-square)
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![H2](https://img.shields.io/badge/Banco-H2%20Database-36678C?style=flat-square)
![Status](https://img.shields.io/badge/Status-Pronto%20para%20demonstracao-brightgreen?style=flat-square)

Projeto acadêmico de **Programação Orientada a Objetos**, com foco no padrão **MVC**, separação de responsabilidades e persistência de dados.

</div>

---

## Sobre o projeto

O **EstoqueFlow** é uma aplicação Java para cadastrar produtos, registrar entradas e saídas de mercadorias e consultar o histórico de movimentações. A aplicação utiliza um banco de dados **H2 em arquivo**, dispensando a instalação de um servidor de banco de dados.

A interface desktop foi implementada em **Java Swing**, com tema escuro, navegação lateral com ícones vetoriais Java2D e dashboard para acompanhamento do estoque.

> **Estado atual:** o núcleo de persistência e movimentações está implementado. A interface gráfica principal também está implementada, incluindo visão geral, produtos, movimentações e estoque baixo. A compilação Gradle e a abertura da aplicação foram verificadas. A suíte automatizada cobre entradas, saídas, validações de quantidade, produtos inativos e limites numéricos do estoque.

## Funcionalidades

| Recurso | Situação |
| :-- | :-- |
| Cadastro de produtos via DAO e interface | ✅ Implementado |
| Listagem e pesquisa por código, nome ou categoria | ✅ Implementado |
| Persistência local com H2 + JDBC | ✅ Implementado |
| Entrada e saída de mercadorias | ✅ Implementado |
| Validação de estoque insuficiente | ✅ Implementado |
| Histórico de movimentações por produto | ✅ Implementado |
| Transações com `commit` e `rollback` | ✅ Implementado |
| Interface gráfica com dashboard e navegação lateral | ✅ Implementado |
| Edição de produtos e ativação/desativação | ✅ Implementado |
| Indicadores e alertas visuais de estoque baixo | ✅ Implementado |

## Tecnologias

- **Java 21** — linguagem e regras de negócio
- **Java Swing** — interface gráfica desktop
- **Gradle (Groovy DSL)** — build e dependências
- **H2 Database + JDBC** — armazenamento persistente e acesso SQL
- **JUnit 5** — testes automatizados

## Arquitetura

O projeto utiliza uma arquitetura em camadas baseada no padrão **MVC (Model-View-Controller)**, separando interface, controle, regras de negócio e persistência.

```mermaid
flowchart TD
    U[Usuário] --> V[View · Swing]
    V --> C[Controller · coordenação]
    C --> S[Service · regras de negócio]
    C --> D[DAO · consultas e persistência]
    S --> D
    D <--> B[(H2 Database)]
    D --> M[Model · entidades]
    S --> M
    C --> M
    M --> V
```

### Classes centrais

- `Main` — inicialização do banco e abertura da aplicação.
- `TelaPrincipal` — View Swing responsável pela interface, dashboard e navegação.
- `EstoqueController` — recebe ações da View e coordena consultas e operações.
- `Produto` — entidade que representa os produtos.
- `Movimentacao` e `TipoMovimentacao` — representam as movimentações do estoque.
- `ProdutoDAO` e `MovimentacaoDAO` — consultas e operações SQL.
- `EstoqueService` — regras de negócio, validações e transações de estoque.
- `DatabaseManager` — conexão com o H2 e criação das tabelas.

## Como executar

### Pré-requisitos

- **JDK 21** instalado e configurado.
- Acesso ao terminal (PowerShell no Windows, por exemplo).

O **Gradle Wrapper** acompanha o projeto, então não é necessário instalar o Gradle separadamente. Na primeira execução, ele pode precisar de internet para baixar a distribuição do Gradle e as dependências.

### Windows

```powershell
git clone https://github.com/brennocoelho/EstoqueFlow.git
cd EstoqueFlow
.\gradlew.bat clean test build
.\gradlew.bat run
```

### Linux / macOS

```bash
git clone https://github.com/brennocoelho/EstoqueFlow.git
cd EstoqueFlow
bash gradlew clean test build
bash gradlew run
```

O comando `run` inicia a aplicação desktop com a interface Swing implementada.

## Armazenamento

O H2 cria automaticamente um arquivo de dados no diretório do usuário:

```text
~/EstoqueFlow/data/estoque.mv.db
```

No Windows, isso corresponde a `%USERPROFILE%\EstoqueFlow\data\estoque.mv.db`.

Os dados permanecem disponíveis após fechar e reabrir o aplicativo. **O arquivo do banco não deve ser enviado ao GitHub**; cada máquina utilizará seu próprio banco local.

## Testes automatizados

A suíte JUnit 5 cobre cinco cenários:

- Registro de entrada e saída com atualização do histórico.
- Rejeição de saída maior que o saldo, sem alterar o estoque.
- Rejeição de quantidade zero ou negativa.
- Bloqueio de movimentação de produto inativo.
- Proteção contra estouro numérico sem perda do saldo.

Execute com `bash gradlew test` no Linux ou `gradlew.bat test` no Windows.

## Distribuição para apresentação

Gere a distribuição com `bash gradlew clean test installDist` no Linux ou `gradlew.bat clean test installDist` no Windows.

Os arquivos serão gerados em `build/install/EstoqueFlow/`. Execute `build/install/EstoqueFlow/bin/EstoqueFlow` no Linux ou `build\\install\\EstoqueFlow\\bin\\EstoqueFlow.bat` no Windows.

Copie a pasta `EstoqueFlow` inteira, incluindo `bin` e `lib`. O computador de apresentação precisa ter Java 21 instalado. Não é necessário instalar Gradle nem utilizar uma IDE.

## Demonstração do núcleo de estoque

Os testes manuais já confirmaram o seguinte cenário:

```text
Saldo inicial: 0
Entrada: +20 unidades
Saída: -5 unidades
Tentativa de saída de 99999 unidades: rejeitada
Saldo final: 15
```

O histórico registra as movimentações válidas. A suíte automatizada JUnit 5 valida as regras centrais do serviço de estoque.

## Estrutura do código

```text
src/main/java/br/com/estoqueflow/
├── Main.java
├── model/        # Produto, Movimentacao e TipoMovimentacao
├── database/     # DatabaseManager
├── dao/          # Acesso aos dados
├── service/      # Regras de negócio
└── view/         # Interface gráfica Swing
```

---

<div align="center">
</div>
