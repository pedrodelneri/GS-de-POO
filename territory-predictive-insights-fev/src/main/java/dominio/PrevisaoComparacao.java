package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao() {
        super("Comparação de Tendência (Período Atual vs. Anterior)");
    }

    @Override
    public double prever(List<Medicao> historico) throws DadosInvalidosException {
        double ultimaLeitura = super.prever(historico);
        if (historico.size() == 1) {
            return ultimaLeitura;
        }
        double leituraAnterior = historico.get(historico.size() - 2).getValor();
        double variacao = ultimaLeitura - leituraAnterior;
        double projecao = Math.max(0.0, ultimaLeitura + variacao);
        return Math.round(projecao * 100.0) / 100.0;
    }

    @Override
    public String explicarResultado(double valorPrevisto, String unidade) {
        return String.format("[%s] Projeção para o próximo ciclo: %.2f %s considerando a aceleração recente.",
                getNomeEstrategia(), valorPrevisto, unidade);
    }
}
