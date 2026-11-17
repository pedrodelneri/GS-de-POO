import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servico.FabricaPrevisao;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrevisaoMediaMovelTest {

    @Test
    @DisplayName("Deve calcular corretamente a média móvel das últimas 3 medições")
    void deveCalcularMediaMovelUltimasTresLeituras() throws DadosInvalidosException {
        // Arrange
        List<Medicao> historico = List.of(
                new Medicao("PLUV-01", "PLUVIOMETRO", 10.0, "mm/h"), // Deve ser ignorada pela janela de 3
                new Medicao("PLUV-01", "PLUVIOMETRO", 20.0, "mm/h"),
                new Medicao("PLUV-01", "PLUVIOMETRO", 40.0, "mm/h"),
                new Medicao("PLUV-01", "PLUVIOMETRO", 60.0, "mm/h")
        );
        EstrategiaPrevisao estrategia = FabricaPrevisao.criarEstrategia(1);

        // Act
        double resultado = estrategia.prever(historico);

        // Assert (média de 20, 40 e 60 = 40.0)
        assertInstanceOf(PrevisaoMediaMovel.class, estrategia);
        assertEquals(40.0, resultado, 0.01);
    }

    @Test
    @DisplayName("Deve projetar tendência de alta na estratégia de comparação")
    void deveCalcularPrevisaoComparacaoTendencia() throws DadosInvalidosException {
        // Arrange
        List<Medicao> historico = List.of(
                new Medicao("PLUV-01", "PLUVIOMETRO", 30.0, "mm/h"),
                new Medicao("PLUV-01", "PLUVIOMETRO", 50.0, "mm/h") // Subiu +20
        );
        EstrategiaPrevisao estrategia = FabricaPrevisao.criarEstrategia(2);

        // Act
        double resultado = estrategia.prever(historico);

        // Assert (50 + 20 = 70.0)
        assertInstanceOf(PrevisaoComparacao.class, estrategia);
        assertEquals(70.0, resultado, 0.01);
    }

    @Test
    @DisplayName("Deve lançar DadosInvalidosException quando histórico estiver vazio")
    void deveLancarExcecaoParaHistoricoVazio() throws DadosInvalidosException {
        // Arrange
        EstrategiaPrevisao estrategia = FabricaPrevisao.criarEstrategia(1);
        List<Medicao> historicoVazio = Collections.emptyList();

        // Act & Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historicoVazio));
    }
}