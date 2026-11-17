import dominio.*;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.ServicoMonitoramento;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoDAO medicaoDAO;

    @Mock
    private AlertaDAO alertaDAO;

    @InjectMocks
    private ServicoMonitoramento servico;

    @BeforeEach
    void configurarLimiaresPadrao() throws DadosInvalidosException {
        // Garante limiares conhecidos antes de cada teste: ATENÇÃO=30, ALERTA=60, CRÍTICO=85
        ConfiguracaoSistema.getInstance().atualizarLimiares(30.0, 60.0, 85.0);
    }

    @Test
    @DisplayName("Deve gerar e salvar alerta CRÍTICO quando previsão ultrapassar limiar máximo")
    void deveGerarAlertaCriticoQuandoPrevisaoUltrapassarLimiar() throws SQLException, DadosInvalidosException {
        // Arrange
        Sensor sensor = new SensorPluviometro("PLUV-01", true);
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(3);
        List<Medicao> historicoSimulado = List.of(
                new Medicao("PLUV-01", "PLUVIOMETRO", 90.0, "mm/h"),
                new Medicao("PLUV-01", "PLUVIOMETRO", 95.0, "mm/h"),
                new Medicao("PLUV-01", "PLUVIOMETRO", 100.0, "mm/h")
        );
        when(medicaoDAO.buscarPorSensor("PLUV-01")).thenReturn(historicoSimulado);

        // Act
        Optional<Alerta> resultado = servico.executarPrevisaoEAvaliarRisco(sensor, estrategia, "Zona Sul");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(NivelAlerta.CRITICO, resultado.get().getNivel());
        assertEquals(95.0, resultado.get().getValorPrevisto(), 0.01);
        verify(alertaDAO, times(1)).salvar(any(Alerta.class));
    }

    @Test
    @DisplayName("Não deve gerar alerta quando previsão estiver abaixo do limiar de atenção")
    void naoDeveGerarAlertaQuandoRiscoForNormal() throws SQLException, DadosInvalidosException {
        // Arrange
        Sensor sensor = new SensorPluviometro("PLUV-01", true);
        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel(3);
        List<Medicao> historicoBaixo = List.of(
                new Medicao("PLUV-01", "PLUVIOMETRO", 10.0, "mm/h"),
                new Medicao("PLUV-01", "PLUVIOMETRO", 15.0, "mm/h")
        );
        when(medicaoDAO.buscarPorSensor("PLUV-01")).thenReturn(historicoBaixo);

        // Act
        Optional<Alerta> resultado = servico.executarPrevisaoEAvaliarRisco(sensor, estrategia, "Zona Sul");

        // Assert
        assertTrue(resultado.isEmpty());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }
}