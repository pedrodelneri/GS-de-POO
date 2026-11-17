import dominio.DadosTerritorio;
import dominio.Medicao;
import dominio.Sensor;
import dominio.SensorPluviometro;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicaoTest {

    @Test
    @DisplayName("Deve criar medição válida quando todos os dados estão corretos")
    void deveCriarMedicaoValida() throws DadosInvalidosException {
        // Arrange
        String sensorId = "PLUV-01";
        String tipo = "PLUVIOMETRO";
        double valor = 42.5;
        String unidade = "mm/h";

        // Act
        Medicao medicao = new Medicao(sensorId, tipo, valor, unidade);

        // Assert
        assertEquals("PLUV-01", medicao.getSensorId());
        assertEquals(42.5, medicao.getValor());
        assertNotNull(medicao.getDataHora());
    }

    @Test
    @DisplayName("Deve lançar DadosInvalidosException quando valor da medição for negativo")
    void deveLancarExcecaoParaValorNegativo() {
        // Arrange
        double valorNegativo = -10.5;

        // Act & Assert
        DadosInvalidosException excecao = assertThrows(DadosInvalidosException.class, () -> {
            new Medicao("PLUV-01", "PLUVIOMETRO", valorNegativo, "mm/h");
        });

        assertTrue(excecao.getMessage().contains("negativo"));
    }

    @Test
    @DisplayName("Deve lançar DadosInvalidosException quando limiares não seguirem ordem crescente")
    void deveLancarExcecaoParaLimiaresForaDeOrdem() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Zona Sul", 10000, 30.0, 60.0, 85.0);

        // Act & Assert
        assertThrows(DadosInvalidosException.class, () -> {
            dados.atualizarLimiares(70.0, 50.0, 85.0);
        });
    }

    @Test
    @DisplayName("Deve lançar FonteIndisponivelException quando sensor estiver offline")
    void deveLancarExcecaoQuandoSensorOffline() {
        // Arrange
        Sensor sensorOffline = new SensorPluviometro("PLUV-OFF", false);

        // Act & Assert
        assertThrows(FonteIndisponivelException.class, sensorOffline::lerValor);
    }
}