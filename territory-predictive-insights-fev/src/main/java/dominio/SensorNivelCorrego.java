package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.Random;

public class SensorNivelCorrego implements Sensor {
    private final String id;
    private boolean ativo;
    private final Random random = new Random();

    public SensorNivelCorrego(String id, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
    }

    @Override
    public String getId() { return id; }

    @Override
    public double lerValor() throws FonteIndisponivelException, DadosInvalidosException {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de Nível do Córrego [" + id + "] indisponível.");
        }
        double leitura = Math.round((0.3 + random.nextDouble() * 3.9) * 100.0) / 100.0;
        if (leitura < 0) {
            throw new DadosInvalidosException("Nível de córrego inválido: " + leitura);
        }
        return leitura;
    }

    @Override
    public boolean verificarFuncionamento() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String retornarTipo() { return "NIVEL_CORREGO"; }

    @Override
    public String getUnidadeMedida() { return "m"; }
}
