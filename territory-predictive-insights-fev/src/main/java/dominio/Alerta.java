package dominio;

import java.time.LocalDateTime;

public class Alerta {
    private Long id;
    private String sensorId;
    private String regiao;
    private NivelAlerta nivel;
    private double valorPrevisto;
    private double limiarExcedido;
    private String justificativa;
    private LocalDateTime dataHora;

    public Alerta(Long id, String sensorId, String regiao, NivelAlerta nivel,
                  double valorPrevisto, double limiarExcedido, String justificativa, LocalDateTime dataHora) {
        this.id = id;
        this.sensorId = sensorId;
        this.regiao = regiao;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiarExcedido = limiarExcedido;
        this.justificativa = justificativa;
        this.dataHora = (dataHora != null) ? dataHora : LocalDateTime.now();
    }

    public Alerta(String sensorId, String regiao, NivelAlerta nivel,
                  double valorPrevisto, double limiarExcedido, String justificativa) {
        this(null, sensorId, regiao, nivel, valorPrevisto, limiarExcedido, justificativa, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSensorId() { return sensorId; }
    public String getRegiao() { return regiao; }
    public NivelAlerta getNivel() { return nivel; }
    public double getValorPrevisto() { return valorPrevisto; }
    public double getLimiarExcedido() { return limiarExcedido; }
    public String getJustificativa() { return justificativa; }
    public LocalDateTime getDataHora() { return dataHora; }
}
