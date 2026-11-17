package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

public class Medicao {
    private Long id;
    private String sensorId;
    private String tipoSensor;
    private double valor;
    private String unidade;
    private LocalDateTime dataHora;

    public Medicao(Long id, String sensorId, String tipoSensor, double valor, String unidade, LocalDateTime dataHora)
            throws DadosInvalidosException {
        this.id = id;
        setSensorId(sensorId);
        setTipoSensor(tipoSensor);
        setValor(valor);
        this.unidade = unidade;
        this.dataHora = (dataHora != null) ? dataHora : LocalDateTime.now();
    }

    public Medicao(String sensorId, String tipoSensor, double valor, String unidade) throws DadosInvalidosException {
        this(null, sensorId, tipoSensor, valor, unidade, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) throw new DadosInvalidosException("O ID do sensor é obrigatório na medição.");
        this.sensorId = sensorId;
    }

    public String getTipoSensor() { return tipoSensor; }
    public void setTipoSensor(String tipoSensor) throws DadosInvalidosException {
        if (tipoSensor == null || tipoSensor.isBlank()) throw new DadosInvalidosException("O tipo do sensor é obrigatório na medição.");
        this.tipoSensor = tipoSensor;
    }

    public double getValor() { return valor; }
    public void setValor(double valor) throws DadosInvalidosException {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new DadosInvalidosException("Valor de medição inválido (não pode ser negativo): " + valor);
        }
        this.valor = valor;
    }

    public String getUnidade() { return unidade; }
    public LocalDateTime getDataHora() { return dataHora; }
}
