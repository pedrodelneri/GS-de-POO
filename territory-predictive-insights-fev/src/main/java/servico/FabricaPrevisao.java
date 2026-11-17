package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;

public class FabricaPrevisao {

    public static EstrategiaPrevisao criarEstrategia(int opcao) throws DadosInvalidosException {
        return switch (opcao) {
            case 1 -> new PrevisaoMediaMovel(3);
            case 2 -> new PrevisaoComparacao();
            default -> throw new DadosInvalidosException("Estratégia de previsão inválida. Escolha 1 (Média Móvel) ou 2 (Comparação).");
        };
    }
}