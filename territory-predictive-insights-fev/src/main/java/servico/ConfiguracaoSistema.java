package servico;

import dominio.DadosTerritorio;
import excecao.DadosInvalidosException;

public class ConfiguracaoSistema {
    private static ConfiguracaoSistema instancia;
    private DadosTerritorio dadosTerritorio;

    private ConfiguracaoSistema() {
        try {
            // Valores iniciais padrão (mm/h de chuva como referência principal)
            this.dadosTerritorio = new DadosTerritorio(
                    "Bacia do Córrego Pirajuçara - Zona Sul",
                    45000,
                    30.0, // ATENÇÃO
                    60.0, // ALERTA
                    85.0  // CRÍTICO
            );
        } catch (DadosInvalidosException e) {
            throw new IllegalStateException("Falha ao carregar configuração padrão do território.", e);
        }
    }

    public static synchronized ConfiguracaoSistema getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public DadosTerritorio getDadosTerritorio() {
        return dadosTerritorio;
    }

    public void atualizarLimiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        this.dadosTerritorio.atualizarLimiares(atencao, alerta, critico);
    }
}