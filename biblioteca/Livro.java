package biblioteca;

import java.util.Objects;

public final class Livro {
    private final String id;
    private final String titulo;
    private final String autor;
    private final String categoria;

    public Livro(String id, String titulo, String autor, String categoria) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador do livro obrigatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo do livro obrigatorio");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("Autor do livro obrigatorio");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("Categoria do livro obrigatoria");
        }
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Livro livro)) return false;
        return Objects.equals(id, livro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Livro[id=%s, titulo=%s, autor=%s, categoria=%s]".formatted(id, titulo, autor, categoria);
    }
}