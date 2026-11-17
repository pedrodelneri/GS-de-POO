package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.util.Random;

public class SensorPluviometro implements Sensor {
    private final String id;
    private boolean ativo;
    private final Random random = new Random();

    public SensorPluviometro(String id, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
    }

    @Override
    public String getId() { return id; }

    @Override
    public double lerValor() throws FonteIndisponivelException, DadosInvalidosException {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor Pluviômetro [" + id + "] está offline ou falhou na coleta.");
        }
        double leitura = Math.round((random.nextDouble() * 110.0) * 100.0) / 100.0;
        if (leitura < 0) {
            throw new DadosInvalidosException("Leitura negativa detectada no pluviômetro: " + leitura);
        }
        return leitura;
    }

    @Override
    public boolean verificarFuncionamento() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String retornarTipo() { return "PLUVIOMETRO"; }

    @Override
    public String getUnidadeMedida() { return "mm/h"; }
}
