package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {
    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média Móvel (" + janela + " últimas leituras)");
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        super.prever(historico);
        int inicio = Math.max(0, historico.size() - janela);
        List<Medicao> recentes = historico.subList(inicio, historico.size());

        double soma = 0.0;
        for (Medicao m : recentes) {
            soma += m.getValor();
        }
        return Math.round((soma / recentes.size()) * 100.0) / 100.0;
    }

    @Override
    public String explicarResultado(double valorPrevisto, String unidade) {
        return String.format("[%s] Tendência estabilizada estimada em %.2f %s com base nas últimas leituras.",
                getNomeEstrategia(), valorPrevisto, unidade);
    }
}
