import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.ConexaoH2;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ServicoMonitoramento;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class SistemaMonitoramento {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        try {
            ConexaoH2.inicializarBanco();
        } catch (SQLException e) {
            System.err.println("[ERRO CRÍTICO] Não foi possível inicializar o banco de dados H2: " + e.getMessage());
            return;
        }

        ServicoMonitoramento servico = new ServicoMonitoramento(new MedicaoDAO(), new AlertaDAO());
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstance();

        try {
            ComponenteTerritorial areaMonitorada = new AreaRiscoAlagamento(
                    "AREA-01",
                    "Várzea do Córrego Pirajuçara",
                    "Zona Sul",
                    "ATIVO",
                    12500,
                    false
            );

            List<Sensor> sensores = new ArrayList<>();
            sensores.add(new SensorPluviometro("PLUV-01", true));
            sensores.add(new SensorNivelCorrego("CORR-01", true));
            sensores.add(new SensorUmidadeSolo("UMID-01", true));

            executarLoopMenu(servico, config, areaMonitorada, sensores);

        } catch (DadosInvalidosException e) {
            System.err.println("[ERRO DE CONFIGURAÇÃO INICIAL] " + e.getMessage());
        }
    }

    private static void executarLoopMenu(ServicoMonitoramento servico, ConfiguracaoSistema config,
                                         ComponenteTerritorial area, List<Sensor> sensores) {
        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        while (executando) {
            System.out.println("\n=======================================================");
            System.out.println("  TERRITORY PREDICTIVE INSIGHTS - MONITORAMENTO CLIMÁTICO");
            System.out.println("=======================================================");
            System.out.println("1. Verificar sensores e coletar novas medições");
            System.out.println("2. Rodar previsões e avaliar alertas");
            System.out.println("3. Ver histórico de medições e alertas (com exportar CSV)");
            System.out.println("4. Gerenciar dados do território (alterar limiares)");
            System.out.println("5. Status completo do território");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> coletarMedicoes(servico, sensores);
                case "2" -> rodarPrevisoes(scanner, servico, area, sensores);
                case "3" -> exibirHistorico(scanner, servico, sensores);
                case "4" -> gerenciarLimiares(scanner, config);
                case "5" -> exibirStatusCompleto(config, area, sensores);
                case "0" -> {
                    executando = false;
                    System.out.println("Encerrando sistema. Os dados permanecem salvos no banco H2.");
                }
                default -> System.out.println("[AVISO] Opção inválida. Digite um número de 0 a 5.");
            }
        }
        scanner.close();
    }

    private static void coletarMedicoes(ServicoMonitoramento servico, List<Sensor> sensores) {
        System.out.println("\n--- Coletando Leituras dos Sensores ---");
        for (Sensor sensor : sensores) {
            try {
                Medicao m = servico.coletarERegistrar(sensor);
                System.out.printf("[OK] %s (%s): %.2f %s | Salvo no banco (ID #%d)%n",
                        m.getSensorId(), m.getTipoSensor(), m.getValor(), m.getUnidade(), m.getId());
            } catch (FonteIndisponivelException e) {
                System.out.println("[FALHA DE SENSOR] " + e.getMessage());
            } catch (DadosInvalidosException e) {
                System.out.println("[DADO REJEITADO] " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("[ERRO NO BANCO] Falha ao persistir medição: " + e.getMessage());
            }
        }
    }

    private static void rodarPrevisoes(Scanner scanner, ServicoMonitoramento servico,
                                       ComponenteTerritorial area, List<Sensor> sensores) {
        System.out.println("\nEscolha a estratégia de previsão:");
        System.out.println("1 - Média Móvel (últimas 3 leituras)");
        System.out.println("2 - Comparação de Tendência (atual vs. anterior)");
        System.out.print("Estratégia: ");

        try {
            int tipo = Integer.parseInt(scanner.nextLine().trim());
            EstrategiaPrevisao estrategia = FabricaPrevisao.criarEstrategia(tipo);

            for (Sensor sensor : sensores) {
                try {
                    Optional<Alerta> alertaOpt = servico.executarPrevisaoEAvaliarRisco(sensor, estrategia, area.getRegiao());
                    if (alertaOpt.isPresent()) {
                        Alerta a = alertaOpt.get();
                        System.out.println(">>> [" + a.getNivel() + "] " + a.getJustificativa());
                    } else {
                        System.out.println(">>> [NORMAL] Risco dentro dos parâmetros seguros para " + sensor.getId());
                    }
                } catch (DadosInvalidosException e) {
                    System.out.println("[AVISO PREVISÃO - " + sensor.getId() + "] " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("[ERRO BANCO - " + sensor.getId() + "] " + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERRO DE ENTRADA] Digite apenas 1 ou 2 para escolher a estratégia.");
        } catch (DadosInvalidosException e) {
            System.out.println("[OPÇÃO INVÁLIDA] " + e.getMessage());
        }
    }

    private static void exibirHistorico(Scanner scanner, ServicoMonitoramento servico, List<Sensor> sensores) {
        System.out.println("\n1 - Listar medições por sensor");
        System.out.println("2 - Listar todos os alertas emitidos");
        System.out.println("3 - Exportar histórico de um sensor para CSV (Bônus)");
        System.out.print("Escolha: ");
        String subOpcao = scanner.nextLine().trim();

        try {
            if ("1".equals(subOpcao) || "3".equals(subOpcao)) {
                System.out.println("Sensores disponíveis: PLUV-01, CORR-01, UMID-01");
                System.out.print("Digite o ID do sensor: ");
                String sensorId = scanner.nextLine().trim().toUpperCase();

                if ("1".equals(subOpcao)) {
                    List<Medicao> lista = servico.consultarHistoricoSensor(sensorId);
                    if (lista.isEmpty()) {
                        System.out.println("Nenhuma medição encontrada para " + sensorId + ". Colete dados na opção 1.");
                        return;
                    }
                    System.out.println("\n--- Histórico de " + sensorId + " ---");
                    for (Medicao m : lista) {
                        System.out.printf("#%d | %.2f %s | %s%n",
                                m.getId(), m.getValor(), m.getUnidade(), m.getDataHora().format(FORMATO_DATA));
                    }
                } else {
                    String arquivo = "historico_" + sensorId.toLowerCase() + ".csv";
                    servico.exportarHistoricoCsv(sensorId, arquivo);
                    System.out.println("[SUCESSO] Arquivo exportado na raiz do projeto: " + arquivo);
                }
            } else if ("2".equals(subOpcao)) {
                List<Alerta> alertas = servico.consultarAlertasEmitidos();
                if (alertas.isEmpty()) {
                    System.out.println("Nenhum alerta registrado no banco até o momento.");
                    return;
                }
                System.out.println("\n--- Alertas Emitidos ---");
                for (Alerta a : alertas) {
                    System.out.printf("#%d [%s] %s | %s%n",
                            a.getId(), a.getNivel(), a.getDataHora().format(FORMATO_DATA), a.getJustificativa());
                }
            } else {
                System.out.println("[AVISO] Sub-opção inválida.");
            }
        } catch (DadosInvalidosException e) {
            System.out.println("[AVISO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO NO BANCO] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERRO AO EXPORTAR CSV] " + e.getMessage());
        }
    }

    private static void gerenciarLimiares(Scanner scanner, ConfiguracaoSistema config) {
        DadosTerritorio dados = config.getDadosTerritorio();
        System.out.printf("\nLimiares atuais -> ATENÇÃO: %.2f | ALERTA: %.2f | CRÍTICO: %.2f%n",
                dados.getLimiarAtencao(), dados.getLimiarAlerta(), dados.getLimiarCritico());
        try {
            System.out.print("Novo limiar ATENÇÃO: ");
            double atencao = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Novo limiar ALERTA: ");
            double alerta = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Novo limiar CRÍTICO: ");
            double critico = Double.parseDouble(scanner.nextLine().trim());

            config.atualizarLimiares(atencao, alerta, critico);
            System.out.println("[SUCESSO] Limiares atualizados com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("[ERRO DE ENTRADA] Digite valores numéricos válidos (use ponto para decimais).");
        } catch (DadosInvalidosException e) {
            System.out.println("[VALIDAÇÃO] " + e.getMessage());
        }
    }

    private static void exibirStatusCompleto(ConfiguracaoSistema config, ComponenteTerritorial area, List<Sensor> sensores) {
        DadosTerritorio dados = config.getDadosTerritorio();
        System.out.println("\n--- Status Completo do Território ---");
        System.out.println("Território: " + dados.getNomeTerritorio() + " | População: " + dados.getPopulacaoEstimada());
        System.out.println("Componente: " + area.verificarStatus());
        System.out.printf("Limiares configurados: ATENÇÃO >= %.2f | ALERTA >= %.2f | CRÍTICO >= %.2f%n",
                dados.getLimiarAtencao(), dados.getLimiarAlerta(), dados.getLimiarCritico());
        System.out.println("Sensores instalados:");
        for (Sensor s : sensores) {
            System.out.printf(" - %s (%s) | Unidade: %s | Online: %s%n",
                    s.getId(), s.retornarTipo(), s.getUnidadeMedida(), s.verificarFuncionamento() ? "SIM" : "NÃO");
        }
    }
}