# Sistema de Gerenciamento de Biblioteca

Projeto desenvolvido para a disciplina de **Paradigmas de Linguagens de Programação**
## Feito por
* Italo Belém Lianza da Franca

## Tecnologias Utilizadas
* **Linguagem:** Java 21 (compatível com JDK 21+)
* **Gerenciador de Dependências e Build:** Apache Maven
* **Testes Automatizados:** JUnit 5 (Jupiter 5.10.2)

## Estrutura e Arquitetura do Projeto

O sistema foi modelado em quatro classes principais no pacote `biblioteca`, além da classe de execução (`Main`) e da suíte de testes (`BibliotecaTest`):

1. **`Livro`**: Entidade imutável (`final`) com atributos encapsulados (`id`, `titulo`, `autor`, `categoria`). Cada instância representa um exemplar único no acervo.
2. **`Usuario`**: Entidade imutável (`final`) que representa um usuário cadastrado (`id`, `nome`, `matricula`).
3. **`Emprestimo`**: Entidade imutável (`final`) que associa um único `Livro` a um único `Usuario`, armazenando `dataEmprestimo`, `dataPrevistaDevolucao` e `dataEfetivaDevolucao`. A devolução utiliza o método `comDevolucaoRegistrada(LocalDate)`, que retorna uma nova instância preenchida sem modificar o objeto original.
4. **`Biblioteca`**: Concentra o gerenciamento de estado (cadastros, empréstimos e devoluções) separado das funções puras de consulta, filtragem, cálculo de multas e geração de estatísticas.

## Funcionalidades Implementadas

### 1. Regras de Negócio e POO
* Cadastro e consulta de livros e usuários com validação de unicidade de identificador.
* Registro de empréstimos garantindo que um exemplar ativo não possa ser emprestado a outro usuário simultaneamente.
* Registro de devoluções que torna o livro imediatamente disponível para novos empréstimos.
* Proteção das coleções internas através de cópias imutáveis (`List.copyOf`).

### 2. Programação Funcional e Imutabilidade
* **Filtragem de Livros (`filtrarLivrosPorCategoria`)**: Recebe uma coleção de livros e uma categoria, retornando uma nova lista filtrada via `Stream.filter()` sem alterar a coleção original.
* **Cálculo Puro de Multa (`calcularMulta` e `calcularDiasAtraso`)**: Funções puras com transparência referencial que calculam os dias de atraso e o valor da multa (R$ 2,00 por dia) sem efeitos colaterais.
* **Empréstimos Atrasados (`filtrarEmprestimosAtrasados`)**: Filtra apenas empréstimos ativos (`dataEfetivaDevolucao == null`) cuja data prevista foi ultrapassada em relação à data de referência.
* **Relatório de Multas (`calcularValorTotalMultas` e `gerarItensRelatorioMultas`)**: Pipeline funcional que aplica `filter` para selecionar atrasados ativos, `map` para transformar cada empréstimo no valor de sua multa e `reduce` para agregar o total devido.
* **Estatísticas (`contarLivrosPorCategoria`, `contarEmprestimosPorUsuario`, `contarEmprestimosAtivos`, `contarEmprestimosAtivosEmAtraso`)**: Agregações funcionais, incluindo o uso explícito de `reduce` na contagem de empréstimos ativos e atrasados.

### 3. Comparação entre Paradigmas
* **Funcional (`calcularValorTotalMultas`)**: Implementado com pipeline declarativo (`filter`, `map`, `reduce`), sem variáveis mutáveis.
* **Imperativo (`calcularValorTotalMultasImperativo`)**: Implementado com laço `for-each`, condicionais `if` e variável acumuladora mutável (`totalMultas += ...`), produzindo resultado idêntico para as mesmas entradas.

## Como Executar o Projeto

### Pelo IntelliJ IDEA
1. Abra a pasta do projeto no IntelliJ IDEA e aguarde o carregamento do Maven (`pom.xml`).
2. Para rodar a demonstração completa no console, abra `src/biblioteca/Main.java` e clique em **Run (`▷`)**.
3. Para rodar os testes unitários, abra `src/biblioteca/BibliotecaTest.java` e clique em **Run Tests (`▷▷`)**.

### Pelo Terminal (Maven)
1. Para executar os testes automatizados:
   ```bash
   mvn test