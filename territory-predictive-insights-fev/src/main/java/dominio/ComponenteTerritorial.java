package dominio;

import excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {
    private String id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(String id, String nome, String regiao, String status) throws DadosInvalidosException {
        setId(id);
        setNome(nome);
        setRegiao(regiao);
        setStatus(status);
    }

    public abstract String verificarStatus();

    public String getId() { return id; }
    public void setId(String id) throws DadosInvalidosException {
        if (id == null || id.isBlank()) throw new DadosInvalidosException("O ID do componente territorial não pode ser vazio.");
        this.id = id;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) throw new DadosInvalidosException("O nome do componente territorial não pode ser vazio.");
        this.nome = nome;
    }

    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) throw new DadosInvalidosException("A região do componente territorial não pode ser vazia.");
        this.regiao = regiao;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) throws DadosInvalidosException {
        if (status == null || status.isBlank()) throw new DadosInvalidosException("O status não pode ser vazio.");
        this.status = status;
    }
}
