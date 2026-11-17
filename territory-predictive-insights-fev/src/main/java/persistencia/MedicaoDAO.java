package persistencia;

import dominio.Medicao;
import excecao.DadosInvalidosException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicaoDAO {

    public void salvar(Medicao medicao) throws SQLException {
        String sql = "INSERT INTO medicoes (sensor_id, tipo_sensor, valor, unidade, data_hora) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoH2.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, medicao.getSensorId());
            ps.setString(2, medicao.getTipoSensor());
            ps.setDouble(3, medicao.getValor());
            ps.setString(4, medicao.getUnidade());
            ps.setTimestamp(5, Timestamp.valueOf(medicao.getDataHora()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    medicao.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Medicao> buscarPorSensor(String sensorId) throws SQLException, DadosInvalidosException {
        String sql = "SELECT id, sensor_id, tipo_sensor, valor, unidade, data_hora FROM medicoes WHERE sensor_id = ? ORDER BY data_hora ASC";
        List<Medicao> historico = new ArrayList<>();

        try (Connection conn = ConexaoH2.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sensorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String sId = rs.getString("sensor_id");
                    String tipo = rs.getString("tipo_sensor");
                    double valor = rs.getDouble("valor");
                    String unidade = rs.getString("unidade");
                    LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();

                    historico.add(new Medicao(id, sId, tipo, valor, unidade, dataHora));
                }
            }
        }
        return historico;
    }

    public List<Medicao> listarTodas() throws SQLException, DadosInvalidosException {
        String sql = "SELECT id, sensor_id, tipo_sensor, valor, unidade, data_hora FROM medicoes ORDER BY data_hora DESC";
        List<Medicao> todas = new ArrayList<>();

        try (Connection conn = ConexaoH2.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                todas.add(new Medicao(
                        rs.getLong("id"),
                        rs.getString("sensor_id"),
                        rs.getString("tipo_sensor"),
                        rs.getDouble("valor"),
                        rs.getString("unidade"),
                        rs.getTimestamp("data_hora").toLocalDateTime()
                ));
            }
        }
        return todas;
    }
}