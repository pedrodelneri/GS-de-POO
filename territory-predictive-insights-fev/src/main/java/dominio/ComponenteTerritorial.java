package dominio;

import excecao.DadosInvalidosException;

public class AreaRiscoAlagamento extends ComponenteTerritorial {
    private int populacaoAfetada;
    private boolean drenagemObstruida;

    public AreaRiscoAlagamento(String id, String nome, String regiao, String status,
                               int populacaoAfetada, boolean drenagemObstruida) throws DadosInvalidosException {
        super(id, nome, regiao, status);
        setPopulacaoAfetada(populacaoAfetada);
        this.drenagemObstruida = drenagemObstruida;
    }

    @Override
    public String verificarStatus() {
        if (drenagemObstruida) {
            return "VULNERÁVEL - Sistema de drenagem com obstrução em " + getNome() + " (" + getRegiao() + ")";
        }
        return "ESTÁVEL - Drenagem operando normalmente em " + getNome() + " (" + getRegiao() + ")";
    }

    public int getPopulacaoAfetada() { return populacaoAfetada; }
    public void setPopulacaoAfetada(int populacaoAfetada) throws DadosInvalidosException {
        if (populacaoAfetada < 0) throw new DadosInvalidosException("A população estimada da área não pode ser negativa.");
        this.populacaoAfetada = populacaoAfetada;
    }

    public boolean isDrenagemObstruida() { return drenagemObstruida; }
    public void setDrenagemObstruida(boolean drenagemObstruida) { this.drenagemObstruida = drenagemObstruida; }
}