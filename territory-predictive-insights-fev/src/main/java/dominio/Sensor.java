package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;

public interface Sensor {
    String getId();
    double lerValor() throws FonteIndisponivelException, DadosInvalidosException;
    boolean verificarFuncionamento();
    String retornarTipo();
    String getUnidadeMedida();
}
