# Territory Predictive Insights — Monitoramento Climático e Risco de Alagamentos

## Integrantes do Grupo
* *Pedro Del Neri Correia* — RM: 562168
* *Arthur da Silva Alencar* — RM: 563684
* *Lucas de Freitas Barbosa* — RM: 564685

---

Plataforma de monitoramento territorial desenvolvida em Java (Programação Orientada a Objetos) com foco em *CitizenTech*, voltada à prevenção de impactos causados por chuvas intensas, elevação do nível de córregos e saturação do solo em áreas urbanas vulneráveis.

---

## 1. Público Beneficiado e Problema Resolvido

* *Quem sofre o problema:* Moradores, comerciantes, equipamentos comunitários e equipes locais de Defesa Civil que atuam em regiões de várzea e microbacias urbanas sujeitas a alagamentos rápidos (modelado no sistema pela *Bacia do Córrego Pirajuçara — Zona Sul*, sem utilização de dados pessoais de indivíduos).
* *Qual decisão o sistema apoia:* Responder antecipadamente se há tendência de transbordamento ou acúmulo crítico nas próximas horas, permitindo emitir alertas preventivos (ATENÇÃO, ALERTA ou CRÍTICO), acionar a limpeza preventiva de drenagem, orientar o bloqueio de vias alagáveis e mobilizar equipes locais antes do pico da precipitação.

---

## 2. Como Executar o Projeto e o Banco de Dados

### Pré-requisitos
* *Java JDK 17* ou superior
* *Apache Maven 3.8+*

### Configuração do Banco de Dados (H2 em Arquivo Local)
Não é necessário instalar nenhum servidor de banco de dados externo na máquina de correção. O sistema utiliza o *H2 Database* em modo arquivo (jdbc:h2:file:./dados/territorio;AUTO_SERVER=TRUE), criando automaticamente o arquivo local e as tabelas medicoes e alertas na primeira execução. Todo o histórico de medições e alertas sobrevive ao encerramento e reinício do programa.

### Comandos para Compilar, Testar e Executar

1. *Executar a suíte de testes unitários (JUnit 5 + Mockito):*
   ```bash
   mvn test



   Padrões Criacionais Aplicados
Singleton (ConfiguracaoSistema): Garante uma única instância compartilhada com os limiares do território (ATENÇÃO, ALERTA, CRÍTICO), mantendo menu e motor de alerta sempre sincronizados.

Factory (FabricaPrevisao): Centraliza a criação das estratégias polimórficas (PrevisaoMediaMovel e PrevisaoComparacao), eliminando if ou instanceof no código principal.

4. Reflexão Crítica
Quando o sistema deve se calar: Quando o histórico está vazio, possui poucas medições consecutivas (menos de 3 a 5 leituras) ou sofre falhas de sensor (FonteIndisponivelException), pois calcular tendências sem base contínua gera previsões não confiáveis.

O que os dados não representam: Sensores pontuais medem apenas variáveis físicas locais (mm/h, nível em m e umidade %), não enxergando bueiros entupidos no quarteirão vizinho, obras nas galerias ou a vulnerabilidade específica de cada família.

Por que o alerta apoia em vez de decidir: O alerta automatizado agiliza a triagem de risco com transparência, mas decisões como interditar vias ou evacuar moradores exigem julgamento humano sobre trânsito, abrigos e contexto social.

5. Uso de Inteligência Artificial
Onde foi usada: Apoio na configuração inicial do Maven (pom.xml), estruturação de testes isolados com Mockito e revisão da documentação.

Validação: Todo o código foi adaptado ao domínio pela equipe e validado com mvn test e testes completos no console.

6. Divisão de Tarefas
Pedro Del Neri Correia (562168): Padrões criacionais (ConfiguracaoSistema e FabricaPrevisao), serviços de monitoramento e alertas, exportação CSV, menu principal e suíte de testes unitários com JUnit 5 e Mockito (servico/, SistemaMonitoramento.java, src/test/java/).

Arthur da Silva Alencar (563684): Exceções customizadas (DadosInvalidosException e FonteIndisponivelException), configuração do projeto Maven e camada de persistência JDBC com banco H2 (excecao/, persistencia/, pom.xml, .gitignore).

Lucas de Freitas Barbosa (564685): Modelagem completa do domínio, abstração e herança (ComponenteTerritorial), interface Sensor (3 implementações), estratégias de previsão polimórficas, validações e documentação (dominio/*, README.md).
