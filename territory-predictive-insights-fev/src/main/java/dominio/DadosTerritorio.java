package dominio;

import excecao.DadosInvalidosException;

public class DadosTerritorio {
    private String nomeTerritorio;
    private int populacaoEstimada;
    private double limiarAtencao;
    private double limiarAlerta;
    private double limiarCritico;

    public DadosTerritorio(String nomeTerritorio, int populacaoEstimada,
                           double limiarAtencao, double limiarAlerta, double limiarCritico) throws DadosInvalidosException {
        setNomeTerritorio(nomeTerritorio);
        setPopulacaoEstimada(populacaoEstimada);
        atualizarLimiares(limiarAtencao, limiarAlerta, limiarCritico);
    }

    public void atualizarLimiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        if (atencao <= 0 || alerta <= 0 || critico <= 0) {
            throw new DadosInvalidosException("Os limiares devem ser maiores que zero.");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException("Os limiares devem obedecer a ordem: ATENÇÃO < ALERTA < CRÍTICO.");
        }
        this.limiarAtencao = atencao;
        this.limiarAlerta = alerta;
        this.limiarCritico = critico;
    }

    public String getNomeTerritorio() { return nomeTerritorio; }
    public void setNomeTerritorio(String nomeTerritorio) throws DadosInvalidosException {
        if (nomeTerritorio == null || nomeTerritorio.isBlank()) throw new DadosInvalidosException("O nome do território não pode ser vazio.");
        this.nomeTerritorio = nomeTerritorio;
    }

    public int getPopulacaoEstimada() { return populacaoEstimada; }
    public void setPopulacaoEstimada(int populacaoEstimada) throws DadosInvalidosException {
        if (populacaoEstimada < 0) throw new DadosInvalidosException("A população estimada não pode ser negativa.");
        this.populacaoEstimada = populacaoEstimada;
    }

    public double getLimiarAtencao() { return limiarAtencao; }
    public double getLimiarAlerta() { return limiarAlerta; }
    public double getLimiarCritico() { return limiarCritico; }
}
