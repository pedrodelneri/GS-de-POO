package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoH2 {
    // Salva o banco em arquivo local dentro da pasta ./dados/territorio
    private static final String URL = "jdbc:h2:file:./dados/territorio;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void inicializarBanco() throws SQLException {
        String sqlMedicoes = """
            CREATE TABLE IF NOT EXISTS medicoes (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                sensor_id VARCHAR(50) NOT NULL,
                tipo_sensor VARCHAR(50) NOT NULL,
                valor DOUBLE NOT NULL,
                unidade VARCHAR(20) NOT NULL,
                data_hora TIMESTAMP NOT NULL
            )
            """;

        String sqlAlertas = """
            CREATE TABLE IF NOT EXISTS alertas (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                sensor_id VARCHAR(50) NOT NULL,
                regiao VARCHAR(100) NOT NULL,
                nivel VARCHAR(20) NOT NULL,
                valor_previsto DOUBLE NOT NULL,
                limiar_excedido DOUBLE NOT NULL,
                justificativa VARCHAR(255) NOT NULL,
                data_hora TIMESTAMP NOT NULL
            )
            """;

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlMedicoes);
            stmt.execute(sqlAlertas);
        }
    }
}