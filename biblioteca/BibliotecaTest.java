package biblioteca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BibliotecaTest {

    private Biblioteca biblioteca;
    private Livro livroComputacao1;
    private Livro livroComputacao2;
    private Livro livroFantasia;
    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void configurarCenario() {
        biblioteca = new Biblioteca();

        livroComputacao1 = new Livro("L1", "Codigo Limpo", "Robert C. Martin", "Computacao");
        livroComputacao2 = new Livro("L2", "Java Efetivo", "Joshua Bloch", "Computacao");
        livroFantasia = new Livro("L3", "O Hobbit", "J.R.R. Tolkien", "Fantasia");

        usuario1 = new Usuario("U1", "Ana Silva", "2024001");
        usuario2 = new Usuario("U2", "Bruno Costa", "2024002");

        biblioteca.cadastrarLivro(livroComputacao1);
        biblioteca.cadastrarLivro(livroComputacao2);
        biblioteca.cadastrarLivro(livroFantasia);

        biblioteca.cadastrarUsuario(usuario1);
        biblioteca.cadastrarUsuario(usuario2);
    }

    @Test
    void devePermitirVariosEmprestimosParaMesmoUsuarioComLivrosDiferentes() {
        Emprestimo emp1 = biblioteca.registrarEmprestimo(
                "E1", "L1", "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );
        Emprestimo emp2 = biblioteca.registrarEmprestimo(
                "E2", "L2", "U1",
                LocalDate.of(2025, 5, 2),
                LocalDate.of(2025, 5, 12)
        );

        assertEquals(2, biblioteca.consultarEmprestimos().size());
        assertEquals(usuario1, emp1.getUsuario());
        assertEquals(usuario1, emp2.getUsuario());
    }

    @Test
    void deveImpedirEmprestimoDeLivroJaEmprestado() {
        biblioteca.registrarEmprestimo(
                "E1", "L1", "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );

        assertFalse(biblioteca.estaLivroDisponivel("L1"));

        assertThrows(IllegalStateException.class, () -> biblioteca.registrarEmprestimo(
                "E2", "L1", "U2",
                LocalDate.of(2025, 5, 3),
                LocalDate.of(2025, 5, 13)
        ));
    }

    @Test
    void deveTornarLivroDisponivelAposDevolucaoSemMutarInstanciaOriginal() {
        Emprestimo emprestimoAtivo = biblioteca.registrarEmprestimo(
                "E1", "L1", "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );

        Emprestimo emprestimoDevolvido = biblioteca.registrarDevolucao(
                "E1",
                LocalDate.of(2025, 5, 9)
        );

        assertNull(emprestimoAtivo.getDataEfetivaDevolucao());
        assertTrue(emprestimoAtivo.estaAtivo());

        assertEquals(LocalDate.of(2025, 5, 9), emprestimoDevolvido.getDataEfetivaDevolucao());
        assertFalse(emprestimoDevolvido.estaAtivo());
        assertNotSame(emprestimoAtivo, emprestimoDevolvido);
        assertTrue(biblioteca.estaLivroDisponivel("L1"));
    }

    @Test
    void deveFiltrarLivrosPorCategoriaSemModificarColecaoOriginal() {
        List<Livro> acervoOriginal = new ArrayList<>(biblioteca.consultarLivros());
        List<Livro> filtrados = biblioteca.filtrarLivrosPorCategoria(acervoOriginal, "Computacao");

        assertEquals(2, filtrados.size());
        assertEquals(3, acervoOriginal.size());
        assertTrue(filtrados.contains(livroComputacao1));
        assertTrue(filtrados.contains(livroComputacao2));
        assertFalse(filtrados.contains(livroFantasia));
    }

    @Test
    void deveGarantirTransparenciaReferencialNoCalculoDeMultaPura() {
        double primeiraChamada = biblioteca.calcularMulta(5, 2.0);
        double segundaChamada = biblioteca.calcularMulta(5, 2.0);
        double semAtraso = biblioteca.calcularMulta(0, 2.0);
        double atrasoNegativo = biblioteca.calcularMulta(-3, 2.0);

        assertEquals(10.0, primeiraChamada);
        assertEquals(primeiraChamada, segundaChamada);
        assertEquals(0.0, semAtraso);
        assertEquals(0.0, atrasoNegativo);
    }

    @Test
    void deveConsiderarApenasEmprestimosAtivosEVencidosNoFiltroECalculoDeMultas() {
        biblioteca.registrarEmprestimo(
                "E1", "L1", "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );
        biblioteca.registrarEmprestimo(
                "E2", "L2", "U1",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 20)
        );
        biblioteca.registrarEmprestimo(
                "E3", "L3", "U2",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 8)
        );
        biblioteca.registrarDevolucao("E3", LocalDate.of(2025, 5, 12));

        LocalDate dataReferencia = LocalDate.of(2025, 5, 15);
        List<Emprestimo> todos = biblioteca.consultarEmprestimos();
        List<Emprestimo> atrasados = biblioteca.filtrarEmprestimosAtrasados(todos, dataReferencia);

        assertEquals(1, atrasados.size());
        assertEquals("E1", atrasados.getFirst().getId());

        double totalMultas = biblioteca.calcularValorTotalMultas(todos, dataReferencia);
        assertEquals(10.0, totalMultas);

        List<String> relatorio = biblioteca.gerarItensRelatorioMultas(todos, dataReferencia);
        assertEquals(1, relatorio.size());
        assertTrue(relatorio.getFirst().contains("Ana Silva"));
        assertTrue(relatorio.getFirst().contains("Codigo Limpo"));
    }

    @Test
    void deveProduzirResultadosIdenticosNosParadigmasFuncionalEImperativo() {
        biblioteca.registrarEmprestimo(
                "E1", "L1", "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );
        biblioteca.registrarEmprestimo(
                "E2", "L2", "U2",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 12)
        );

        LocalDate dataReferencia = LocalDate.of(2025, 5, 15);
        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimos();

        double totalFuncional = biblioteca.calcularValorTotalMultas(emprestimos, dataReferencia);
        double totalImperativo = biblioteca.calcularValorTotalMultasImperativo(emprestimos, dataReferencia);

        assertEquals(16.0, totalFuncional);
        assertEquals(totalFuncional, totalImperativo);
    }

    @Test
    void deveCalcularCorretamenteAsCincoEstatisticasDoSistema() {
        biblioteca.registrarEmprestimo(
                "E1", "L1", "U1",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 10)
        );
        biblioteca.registrarEmprestimo(
                "E2", "L2", "U1",
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 20)
        );
        biblioteca.registrarEmprestimo(
                "E3", "L3", "U2",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 8)
        );
        biblioteca.registrarDevolucao("E3", LocalDate.of(2025, 5, 12));

        LocalDate dataReferencia = LocalDate.of(2025, 5, 15);
        List<Livro> livros = biblioteca.consultarLivros();
        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimos();

        Map<String, Long> porCategoria = biblioteca.contarLivrosPorCategoria(livros);
        assertEquals(2L, porCategoria.get("Computacao"));
        assertEquals(1L, porCategoria.get("Fantasia"));

        Map<String, Long> porUsuario = biblioteca.contarEmprestimosPorUsuario(emprestimos);
        assertEquals(2L, porUsuario.get("Ana Silva"));
        assertEquals(1L, porUsuario.get("Bruno Costa"));

        assertEquals(2L, biblioteca.contarEmprestimosAtivos(emprestimos));
        assertEquals(1L, biblioteca.contarEmprestimosAtivosEmAtraso(emprestimos, dataReferencia));
        assertEquals(10.0, biblioteca.calcularValorTotalMultas(emprestimos, dataReferencia));
    }

    @Test
    void deveProtegerListasInternasContraMutacaoExterna() {
        List<Livro> livrosConsultados = biblioteca.consultarLivros();
        Livro novoLivro = new Livro("L99", "Refatoracao", "Martin Fowler", "Computacao");

        assertThrows(UnsupportedOperationException.class, () -> livrosConsultados.add(novoLivro));
    }
}