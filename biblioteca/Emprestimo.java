package biblioteca;

import java.time.LocalDate;
import java.util.Objects;

public final class Emprestimo {
    private final String id;
    private final Livro livro;
    private final Usuario usuario;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private final LocalDate dataEfetivaDevolucao;

    public Emprestimo(
            String id,
            Livro livro,
            Usuario usuario,
            LocalDate dataEmprestimo,
            LocalDate dataPrevistaDevolucao,
            LocalDate dataEfetivaDevolucao
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador do emprestimo obrigatorio");
        }
        this.livro = Objects.requireNonNull(livro, "Livro obrigatorio");
        this.usuario = Objects.requireNonNull(usuario, "Usuario obrigatorio");
        this.dataEmprestimo = Objects.requireNonNull(dataEmprestimo, "Data do emprestimo obrigatoria");
        this.dataPrevistaDevolucao = Objects.requireNonNull(dataPrevistaDevolucao, "Data prevista obrigatoria");
        if (dataPrevistaDevolucao.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("Data prevista nao pode ser anterior a data do emprestimo");
        }
        if (dataEfetivaDevolucao != null && dataEfetivaDevolucao.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("Data efetiva nao pode ser anterior a data do emprestimo");
        }
        this.id = id;
        this.dataEfetivaDevolucao = dataEfetivaDevolucao;
    }

    public Emprestimo(
            String id,
            Livro livro,
            Usuario usuario,
            LocalDate dataEmprestimo,
            LocalDate dataPrevistaDevolucao
    ) {
        this(id, livro, usuario, dataEmprestimo, dataPrevistaDevolucao, null);
    }

    public Emprestimo comDevolucaoRegistrada(LocalDate dataDevolucao) {
        Objects.requireNonNull(dataDevolucao, "Data de devolucao obrigatoria");
        return new Emprestimo(
                this.id,
                this.livro,
                this.usuario,
                this.dataEmprestimo,
                this.dataPrevistaDevolucao,
                dataDevolucao
        );
    }

    public boolean estaAtivo() {
        return dataEfetivaDevolucao == null;
    }

    public String getId() {
        return id;
    }

    public Livro getLivro() {
        return livro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataEfetivaDevolucao() {
        return dataEfetivaDevolucao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Emprestimo that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Emprestimo[id=%s, livro=%s, usuario=%s, emprestimo=%s, prevista=%s, efetiva=%s]".formatted(
                id,
                livro.getTitulo(),
                usuario.getNome(),
                dataEmprestimo,
                dataPrevistaDevolucao,
                dataEfetivaDevolucao
        );
    }
}