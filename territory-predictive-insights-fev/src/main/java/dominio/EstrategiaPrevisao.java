package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

public abstract class EstrategiaPrevisao {
    private final String nomeEstrategia;

    public EstrategiaPrevisao(String nomeEstrategia) {
        this.nomeEstrategia = nomeEstrategia;
    }

    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        if (historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException("Histórico vazio: não há medições suficientes para calcular previsão.");
        }
        return historico.get(historico.size() - 1).getValor();
    }

    public abstract String explicarResultado(double valorPrevisto, String unidade);

    public String getNomeEstrategia() { return nomeEstrategia; }
}
