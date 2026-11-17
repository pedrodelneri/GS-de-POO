package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.Random;

public class SensorUmidadeSolo implements Sensor {
    private final String id;
    private boolean ativo;
    private final Random random = new Random();

    public SensorUmidadeSolo(String id, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
    }

    @Override
    public String getId() { return id; }

    @Override
    public double lerValor() throws FonteIndisponivelException, DadosInvalidosException {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de Umidade do Solo [" + id + "] indisponível.");
        }
        double leitura = Math.round((20.0 + random.nextDouble() * 80.0) * 100.0) / 100.0;
        if (leitura < 0 || leitura > 100) {
            throw new DadosInvalidosException("Umidade do solo fora da faixa (0% a 100%): " + leitura);
        }
        return leitura;
    }

    @Override
    public boolean verificarFuncionamento() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String retornarTipo() { return "UMIDADE_SOLO"; }

    @Override
    public String getUnidadeMedida() { return "%"; }
}
