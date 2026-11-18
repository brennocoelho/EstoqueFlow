<div align="center">

# EstoqueFlow

**Sistema desktop de controle e movimentação de estoque desenvolvido em Java.**

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Interface-Java%20Swing-305C79?style=flat-square)
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![H2](https://img.shields.io/badge/Banco-H2%20Database-36678C?style=flat-square)
![Status](https://img.shields.io/badge/Status-Em%20desenvolvimento-yellow?style=flat-square)

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

## Arquitetura

O projeto utiliza uma organização em camadas, inspirada no padrão **MVC**, com **View**, **Model**, **Service** e **DAO**. A interface Swing acessa os serviços e DAOs diretamente, enquanto as regras de negócio e a persistência permanecem separadas.

```mermaid
flowchart TD
    U[Usuário] --> V[View · Swing]
    V --> S[Service · regras de negócio]
    V --> D[DAO · JDBC]
    S --> D
    D <--> B[(H2 Database)]
    D --> M[Model · entidades]
    S --> M
```

> A tela principal utiliza os serviços e DAOs para apresentar os dados e executar as operações disponíveis.

### Classes centrais

- `Main` — inicialização da aplicação e abertura da janela principal.
- `TelaPrincipal` — janela Swing, dashboard, navegação e telas de produtos, movimentações e alertas.
- `Produto` — código, nome, categoria, preço, quantidade e estoque mínimo.
- `Movimentacao` e `TipoMovimentacao` — registros de entrada e saída.
- `ProdutoDAO` e `MovimentacaoDAO` — consultas e comandos SQL.
- `EstoqueService` — operações e validações de estoque, incluindo transações.
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
git switch Joao
.\gradlew.bat clean build
.\gradlew.bat run
```

### Linux / macOS

```bash
git clone https://github.com/brennocoelho/EstoqueFlow.git
cd EstoqueFlow
git switch Joao
./gradlew clean build
./gradlew run
```

O comando `run` inicia a aplicação desktop com a interface Swing implementada.

## Armazenamento

O H2 cria automaticamente um arquivo de dados no diretório do usuário:

```text
~/EstoqueFlow/data/estoque.mv.db
```

No Windows, isso corresponde a `%USERPROFILE%\EstoqueFlow\data\estoque.mv.db`.

Os dados permanecem disponíveis após fechar e reabrir o aplicativo. **O arquivo do banco não deve ser enviado ao GitHub**; cada máquina utilizará seu próprio banco local.

## Demonstração do núcleo de estoque

Os testes manuais já confirmaram o seguinte cenário:

```text
Saldo inicial: 0
Entrada: +20 unidades
Saída: -5 unidades
Tentativa de saída de 99999 unidades: rejeitada
Saldo final: 15
```

O histórico registra as movimentações válidas. As classes `TestePersistencia` e `TesteMovimentacoes` foram usadas durante o desenvolvimento; elas não constituem uma suíte automatizada de testes.

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
  <sub>EstoqueFlow · Java + MVC · Projeto acadêmico</sub>
</div>
