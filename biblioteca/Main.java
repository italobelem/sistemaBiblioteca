package biblioteca;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();

        Livro livro1 = new Livro("L1", "Codigo Limpo", "Robert C. Martin", "Computacao");
        Livro livro2 = new Livro("L2", "Java Efetivo", "Joshua Bloch", "Computacao");
        Livro livro3 = new Livro("L3", "Arquitetura Limpa", "Robert C. Martin", "Computacao");
        Livro livro4 = new Livro("L4", "O Senhor dos Aneis", "J.R.R. Tolkien", "Fantasia");
        Livro livro5 = new Livro("L5", "Calculo Volume 1", "James Stewart", "Matematica");

        biblioteca.cadastrarLivro(livro1);
        biblioteca.cadastrarLivro(livro2);
        biblioteca.cadastrarLivro(livro3);
        biblioteca.cadastrarLivro(livro4);
        biblioteca.cadastrarLivro(livro5);

        Usuario usuario1 = new Usuario("U1", "Ana Silva", "2024001");
        Usuario usuario2 = new Usuario("U2", "Bruno Costa", "2024002");
        Usuario usuario3 = new Usuario("U3", "Carla Souza", "2024003");

        biblioteca.cadastrarUsuario(usuario1);
        biblioteca.cadastrarUsuario(usuario2);
        biblioteca.cadastrarUsuario(usuario3);

        System.out.println("=== 1. LIVROS E USUARIOS CADASTRADOS ===");
        biblioteca.consultarLivros().forEach(System.out::println);
        biblioteca.consultarUsuarios().forEach(System.out::println);

        biblioteca.registrarEmprestimo(
                "E1",
                "L1",
                "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );

        biblioteca.registrarEmprestimo(
                "E2",
                "L2",
                "U1",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 12)
        );

        biblioteca.registrarEmprestimo(
                "E3",
                "L4",
                "U2",
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 5, 20)
        );

        biblioteca.registrarEmprestimo(
                "E4",
                "L5",
                "U3",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 8)
        );

        System.out.println("\n=== 2. TESTE DE BLOQUEIO DE EXEMPLAR UNICO ===");
        try {
            biblioteca.registrarEmprestimo(
                    "E5",
                    "L1",
                    "U2",
                    LocalDate.of(2025, 5, 11),
                    LocalDate.of(2025, 5, 18)
            );
        } catch (IllegalStateException ex) {
            System.out.println("Regra validada com sucesso: " + ex.getMessage());
        }

        System.out.println("\n=== 3. REGISTRO DE DEVOLUCAO ===");
        System.out.println("Livro L5 disponivel antes da devolucao? " + biblioteca.estaLivroDisponivel("L5"));
        Emprestimo emprestimoDevolvido = biblioteca.registrarDevolucao("E4", LocalDate.of(2025, 5, 14));
        System.out.println("Devolucao registrada: " + emprestimoDevolvido);
        System.out.println("Livro L5 disponivel apos a devolucao? " + biblioteca.estaLivroDisponivel("L5"));

        List<Livro> acervoOriginal = biblioteca.consultarLivros();
        List<Livro> livrosComputacao = biblioteca.filtrarLivrosPorCategoria(acervoOriginal, "Computacao");

        System.out.println("\n=== 4. FILTRAGEM FUNCIONAL E IMUTABILIDADE ===");
        System.out.println("Livros da categoria Computacao:");
        livrosComputacao.forEach(System.out::println);
        System.out.println("Tamanho do acervo original intacto: " + acervoOriginal.size());
        System.out.println("Tamanho da nova lista filtrada: " + livrosComputacao.size());

        LocalDate dataReferencia = LocalDate.of(2025, 5, 15);
        List<Emprestimo> todosEmprestimos = biblioteca.consultarEmprestimos();
        List<Emprestimo> atrasadosAtivos = biblioteca.filtrarEmprestimosAtrasados(todosEmprestimos, dataReferencia);

        System.out.println("\n=== 5. EMPRESTIMOS ATIVOS EM ATRASO (REF: " + dataReferencia + ") ===");
        atrasadosAtivos.forEach(System.out::println);

        System.out.println("\n=== 6. RELATORIO DE MULTAS (FILTER, MAP, REDUCE) ===");
        List<String> itensRelatorio = biblioteca.gerarItensRelatorioMultas(todosEmprestimos, dataReferencia);
        itensRelatorio.forEach(System.out::println);
        double totalMultasFuncional = biblioteca.calcularValorTotalMultas(todosEmprestimos, dataReferencia);
        System.out.printf("Valor total das multas: R$ %.2f%n", totalMultasFuncional);

        System.out.println("\n=== 7. ESTATISTICAS DO SISTEMA ===");
        Map<String, Long> livrosPorCategoria = biblioteca.contarLivrosPorCategoria(acervoOriginal);
        System.out.println("Quantidade de livros por categoria: " + livrosPorCategoria);

        Map<String, Long> emprestimosPorUsuario = biblioteca.contarEmprestimosPorUsuario(todosEmprestimos);
        System.out.println("Quantidade de emprestimos por usuario: " + emprestimosPorUsuario);

        long totalAtivos = biblioteca.contarEmprestimosAtivos(todosEmprestimos);
        System.out.println("Quantidade de emprestimos ativos: " + totalAtivos);

        long totalAtivosAtrasados = biblioteca.contarEmprestimosAtivosEmAtraso(todosEmprestimos, dataReferencia);
        System.out.println("Quantidade de emprestimos ativos em atraso: " + totalAtivosAtrasados);

        System.out.printf("Valor total das multas acumulado: R$ %.2f%n", totalMultasFuncional);

        System.out.println("\n=== 8. COMPARACAO ENTRE PARADIGMAS (FUNCIONAL VS IMPERATIVO) ===");
        double totalMultasImperativo = biblioteca.calcularValorTotalMultasImperativo(todosEmprestimos, dataReferencia);
        System.out.printf("Resultado Funcional (filter/map/reduce): R$ %.2f%n", totalMultasFuncional);
        System.out.printf("Resultado Imperativo (for/if/acumulador): R$ %.2f%n", totalMultasImperativo);
        System.out.println("Resultados equivalentes? " + (Double.compare(totalMultasFuncional, totalMultasImperativo) == 0));
    }
}