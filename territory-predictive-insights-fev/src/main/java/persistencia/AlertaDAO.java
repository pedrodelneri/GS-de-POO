package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaDAO {

    public void salvar(Alerta alerta) throws SQLException {
        String sql = "INSERT INTO alertas (sensor_id, regiao, nivel, valor_previsto, limiar_excedido, justificativa, data_hora) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoH2.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, alerta.getSensorId());
            ps.setString(2, alerta.getRegiao());
            ps.setString(3, alerta.getNivel().name());
            ps.setDouble(4, alerta.getValorPrevisto());
            ps.setDouble(5, alerta.getLimiarExcedido());
            ps.setString(6, alerta.getJustificativa());
            ps.setTimestamp(7, Timestamp.valueOf(alerta.getDataHora()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    alerta.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Alerta> listarTodos() throws SQLException {
        String sql = "SELECT id, sensor_id, regiao, nivel, valor_previsto, limiar_excedido, justificativa, data_hora FROM alertas ORDER BY data_hora DESC";
        List<Alerta> alertas = new ArrayList<>();

        try (Connection conn = ConexaoH2.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String sensorId = rs.getString("sensor_id");
                String regiao = rs.getString("regiao");
                NivelAlerta nivel = NivelAlerta.valueOf(rs.getString("nivel"));
                double valorPrevisto = rs.getDouble("valor_previsto");
                double limiarExcedido = rs.getDouble("limiar_excedido");
                String justificativa = rs.getString("justificativa");
                LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();

                alertas.add(new Alerta(id, sensorId, regiao, nivel, valorPrevisto, limiarExcedido, justificativa, dataHora));
            }
        }
        return alertas;
    }
}