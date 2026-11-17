package biblioteca;

import java.util.Objects;

public final class Usuario {
    private final String id;
    private final String nome;
    private final String matricula;

    public Usuario(String id, String nome, String matricula) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador do usuario obrigatorio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do usuario obrigatorio");
        }
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Matricula do usuario obrigatoria");
        }
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario[id=%s, nome=%s, matricula=%s]".formatted(id, nome, matricula);
    }
}
