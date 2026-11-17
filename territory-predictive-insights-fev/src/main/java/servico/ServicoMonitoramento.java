package servico;

import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ServicoMonitoramento {
    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final ConfiguracaoSistema configuracao;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO) {
        this(medicaoDAO, alertaDAO, ConfiguracaoSistema.getInstance());
    }

    // Construtor extra que facilita injetar mocks/configurações nos testes unitários
    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO, ConfiguracaoSistema configuracao) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.configuracao = configuracao;
    }

    public Medicao coletarERegistrar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException, SQLException {
        double valorLido = sensor.lerValor();
        Medicao medicao = new Medicao(
                sensor.getId(),
                sensor.retornarTipo(),
                valorLido,
                sensor.getUnidadeMedida()
        );
        medicaoDAO.salvar(medicao);
        return medicao;
    }

    public Optional<Alerta> executarPrevisaoEAvaliarRisco(Sensor sensor, EstrategiaPrevisao estrategia, String regiao)
            throws SQLException, DadosInvalidosException {

        List<Medicao> historico = medicaoDAO.buscarPorSensor(sensor.getId());
        double valorPrevisto = estrategia.prever(historico);

        System.out.println("\n--- Resultado da Previsão (" + sensor.getId() + ") ---");
        System.out.println(estrategia.explicarResultado(valorPrevisto, sensor.getUnidadeMedida()));

        DadosTerritorio dados = configuracao.getDadosTerritorio();
        NivelAlerta nivelDetectado = null;
        double limiarExcedido = 0.0;

        if (valorPrevisto >= dados.getLimiarCritico()) {
            nivelDetectado = NivelAlerta.CRITICO;
            limiarExcedido = dados.getLimiarCritico();
        } else if (valorPrevisto >= dados.getLimiarAlerta()) {
            nivelDetectado = NivelAlerta.ALERTA;
            limiarExcedido = dados.getLimiarAlerta();
        } else if (valorPrevisto >= dados.getLimiarAtencao()) {
            nivelDetectado = NivelAlerta.ATENCAO;
            limiarExcedido = dados.getLimiarAtencao();
        }

        if (nivelDetectado != null) {
            String justificativa = String.format(
                    "Sensor %s (%s) atingiu previsão de %.2f %s, ultrapassando o limite de %s (%.2f %s).",
                    sensor.getId(), sensor.retornarTipo(), valorPrevisto, sensor.getUnidadeMedida(),
                    nivelDetectado, limiarExcedido, sensor.getUnidadeMedida()
            );

            Alerta alerta = new Alerta(
                    sensor.getId(),
                    regiao,
                    nivelDetectado,
                    valorPrevisto,
                    limiarExcedido,
                    justificativa
            );
            alertaDAO.salvar(alerta);
            return Optional.of(alerta);
        }

        return Optional.empty();
    }

    public List<Medicao> consultarHistoricoSensor(String sensorId) throws SQLException, DadosInvalidosException {
        return medicaoDAO.buscarPorSensor(sensorId);
    }

    public List<Alerta> consultarAlertasEmitidos() throws SQLException {
        return alertaDAO.listarTodos();
    }

    // Bônus (+0,5 pt): Exportar histórico consultado em CSV
    public void exportarHistoricoCsv(String sensorId, String caminhoArquivo)
            throws SQLException, DadosInvalidosException, IOException {
        List<Medicao> medicoes = medicaoDAO.buscarPorSensor(sensorId);
        if (medicoes.isEmpty()) {
            throw new DadosInvalidosException("Não há medições registradas para exportar do sensor " + sensorId);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            writer.println("ID;SENSOR_ID;TIPO_SENSOR;VALOR;UNIDADE;DATA_HORA");
            for (Medicao m : medicoes) {
                writer.printf("%d;%s;%s;%.2f;%s;%s%n",
                        m.getId(), m.getSensorId(), m.getTipoSensor(),
                        m.getValor(), m.getUnidade(), m.getDataHora().format(fmt));
            }
        }
    }
}