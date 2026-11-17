package biblioteca;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Biblioteca {
    public static final double VALOR_MULTA_DIARIA = 2.0;

    private final List<Livro> livros;
    private final List<Usuario> usuarios;
    private final List<Emprestimo> emprestimos;

    public Biblioteca() {
        this.livros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
    }

    public void cadastrarLivro(Livro livro) {
        Objects.requireNonNull(livro, "Livro nao pode ser nulo");
        boolean jaExiste = livros.stream().anyMatch(l -> l.getId().equals(livro.getId()));
        if (jaExiste) {
            throw new IllegalArgumentException("Livro com este identificador ja cadastrado");
        }
        livros.add(livro);
    }

    public void cadastrarUsuario(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuario nao pode ser nulo");
        boolean jaExiste = usuarios.stream().anyMatch(u -> u.getId().equals(usuario.getId()));
        if (jaExiste) {
            throw new IllegalArgumentException("Usuario com este identificador ja cadastrado");
        }
        usuarios.add(usuario);
    }

    public Emprestimo registrarEmprestimo(
            String idEmprestimo,
            String idLivro,
            String idUsuario,
            LocalDate dataEmprestimo,
            LocalDate dataPrevistaDevolucao
    ) {
        boolean emprestimoJaExiste = emprestimos.stream().anyMatch(e -> e.getId().equals(idEmprestimo));
        if (emprestimoJaExiste) {
            throw new IllegalArgumentException("Identificador de emprestimo ja utilizado");
        }
        Livro livro = buscarLivroPorId(idLivro)
                .orElseThrow(() -> new IllegalArgumentException("Livro nao encontrado"));
        Usuario usuario = buscarUsuarioPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        if (!estaLivroDisponivel(idLivro)) {
            throw new IllegalStateException("Livro ja esta emprestado no momento");
        }

        Emprestimo novoEmprestimo = new Emprestimo(
                idEmprestimo,
                livro,
                usuario,
                dataEmprestimo,
                dataPrevistaDevolucao
        );
        emprestimos.add(novoEmprestimo);
        return novoEmprestimo;
    }

    public Emprestimo registrarDevolucao(String idEmprestimo, LocalDate dataEfetivaDevolucao) {
        for (int i = 0; i < emprestimos.size(); i++) {
            Emprestimo atual = emprestimos.get(i);
            if (atual.getId().equals(idEmprestimo)) {
                if (!atual.estaAtivo()) {
                    throw new IllegalStateException("Este emprestimo ja foi devolvido");
                }
                Emprestimo devolvido = atual.comDevolucaoRegistrada(dataEfetivaDevolucao);
                emprestimos.set(i, devolvido);
                return devolvido;
            }
        }
        throw new IllegalArgumentException("Emprestimo nao encontrado");
    }

    public List<Livro> consultarLivros() {
        return List.copyOf(livros);
    }

    public List<Usuario> consultarUsuarios() {
        return List.copyOf(usuarios);
    }

    public List<Emprestimo> consultarEmprestimos() {
        return List.copyOf(emprestimos);
    }

    public Optional<Livro> buscarLivroPorId(String idLivro) {
        return livros.stream()
                .filter(livro -> livro.getId().equals(idLivro))
                .findFirst();
    }

    public Optional<Usuario> buscarUsuarioPorId(String idUsuario) {
        return usuarios.stream()
                .filter(usuario -> usuario.getId().equals(idUsuario))
                .findFirst();
    }

    public boolean estaLivroDisponivel(String idLivro) {
        return emprestimos.stream()
                .noneMatch(e -> e.estaAtivo() && e.getLivro().getId().equals(idLivro));
    }

    public List<Livro> filtrarLivrosPorCategoria(List<Livro> colecaoLivros, String categoria) {
        return colecaoLivros.stream()
                .filter(livro -> livro.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    public double calcularMulta(long diasAtraso, double valorMultaDiaria) {
        if (diasAtraso <= 0 || valorMultaDiaria <= 0) {
            return 0.0;
        }
        return diasAtraso * valorMultaDiaria;
    }

    public long calcularDiasAtraso(Emprestimo emprestimo, LocalDate dataReferencia) {
        if (!emprestimo.estaAtivo() || !dataReferencia.isAfter(emprestimo.getDataPrevistaDevolucao())) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), dataReferencia);
    }

    public List<Emprestimo> filtrarEmprestimosAtrasados(
            List<Emprestimo> colecaoEmprestimos,
            LocalDate dataReferencia
    ) {
        return colecaoEmprestimos.stream()
                .filter(emprestimo -> emprestimo.estaAtivo()
                        && dataReferencia.isAfter(emprestimo.getDataPrevistaDevolucao()))
                .toList();
    }

    public List<String> gerarItensRelatorioMultas(
            List<Emprestimo> colecaoEmprestimos,
            LocalDate dataReferencia
    ) {
        return filtrarEmprestimosAtrasados(colecaoEmprestimos, dataReferencia).stream()
                .map(emprestimo -> {
                    long dias = calcularDiasAtraso(emprestimo, dataReferencia);
                    double multa = calcularMulta(dias, VALOR_MULTA_DIARIA);
                    return "Usuario: %s | Livro: %s | Dias de atraso: %d | Multa: R$ %.2f".formatted(
                            emprestimo.getUsuario().getNome(),
                            emprestimo.getLivro().getTitulo(),
                            dias,
                            multa
                    );
                })
                .toList();
    }

    public double calcularValorTotalMultas(
            List<Emprestimo> colecaoEmprestimos,
            LocalDate dataReferencia
    ) {
        return colecaoEmprestimos.stream()
                .filter(emprestimo -> emprestimo.estaAtivo()
                        && dataReferencia.isAfter(emprestimo.getDataPrevistaDevolucao()))
                .map(emprestimo -> calcularMulta(
                        calcularDiasAtraso(emprestimo, dataReferencia),
                        VALOR_MULTA_DIARIA
                ))
                .reduce(0.0, Double::sum);
    }

    public double calcularValorTotalMultasImperativo(
            List<Emprestimo> colecaoEmprestimos,
            LocalDate dataReferencia
    ) {
        double totalMultas = 0.0;
        for (Emprestimo emprestimo : colecaoEmprestimos) {
            if (emprestimo.estaAtivo() && dataReferencia.isAfter(emprestimo.getDataPrevistaDevolucao())) {
                long diasAtraso = ChronoUnit.DAYS.between(
                        emprestimo.getDataPrevistaDevolucao(),
                        dataReferencia
                );
                if (diasAtraso > 0) {
                    totalMultas += diasAtraso * VALOR_MULTA_DIARIA;
                }
            }
        }
        return totalMultas;
    }

    public Map<String, Long> contarLivrosPorCategoria(List<Livro> colecaoLivros) {
        return colecaoLivros.stream()
                .collect(Collectors.groupingBy(Livro::getCategoria, Collectors.counting()));
    }

    public Map<String, Long> contarEmprestimosPorUsuario(List<Emprestimo> colecaoEmprestimos) {
        return colecaoEmprestimos.stream()
                .collect(Collectors.groupingBy(
                        emprestimo -> emprestimo.getUsuario().getNome(),
                        Collectors.counting()
                ));
    }

    public long contarEmprestimosAtivos(List<Emprestimo> colecaoEmprestimos) {
        return colecaoEmprestimos.stream()
                .filter(Emprestimo::estaAtivo)
                .map(emprestimo -> 1L)
                .reduce(0L, Long::sum);
    }

    public long contarEmprestimosAtivosEmAtraso(
            List<Emprestimo> colecaoEmprestimos,
            LocalDate dataReferencia
    ) {
        return filtrarEmprestimosAtrasados(colecaoEmprestimos, dataReferencia).stream()
                .map(emprestimo -> 1L)
                .reduce(0L, Long::sum);
    }
}