# ERP Custom Garage (Centro Automotivo & Customização)

Protótipo funcional de ERP desenvolvido em Java (Spring Boot) com estilização moderna usando **Tailwind CSS v4** e gráficos de vendas/custos do **Chart.js**.

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem**: Java 17
- **Backend**: Spring Boot 3.x (Web, JPA, H2, Thymeleaf)
- **Frontend**: Thymeleaf, Tailwind CSS v4, Vanilla JavaScript
- **Gráficos**: Chart.js
- **Banco de Dados**: H2 Database (Persistido localmente em arquivo para não perder os dados ao reiniciar)

---

## 🚀 Como Executar o Projeto

Como o projeto inclui o Maven Wrapper (`mvnw.cmd`), você **não precisa ter o Maven instalado globalmente**. O único pré-requisito é ter o **Java 17** instalado (que já validamos que seu sistema possui!).

1. Abra um terminal na pasta do projeto:
   `C:\Users\cassi\.gemini\antigravity-ide\scratch\erp-prototype`

2. Execute o comando para rodar a aplicação:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

3. Assim que o servidor iniciar, acesse o sistema no navegador:
   - **Dashboard & ERP**: [http://localhost:8080](http://localhost:8080)
   - **Console do Banco H2**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (Credenciais pré-configuradas: JDBC URL: `jdbc:h2:file:./db/erp_db`, User: `sa`, Senha em branco).

---

## 📊 Regras de Negócio e Funcionalidades Pré-configuradas

O sistema inicia com **dados mockados realistas** (clientes, insumos, histórico de ordens e custos) para que o Dashboard exiba gráficos interativos de pizza e barras na primeira inicialização.

- **Atualização Automática de Estoque**: Quando uma Ordem de Serviço (OS) com status `Em Andamento` é alterada para `Concluído`, o sistema realiza a baixa automática dos insumos de estoque atrelados.
- **Lançamento de Custos de Insumo**: Sempre que você registrar uma nova **Entrada de Estoque** para algum produto, o sistema calcula o valor (`Quantidade * Preço Custo`) e insere uma despesa do tipo `Aquisição de Estoque` no fluxo financeiro automaticamente.
- **Especialidades Integradas**: As OS são divididas entre as 7 especialidades mecânicas e estéticas:
  1. Especialização em Marcenaria Automotiva
  2. Instalação de Som e Acessórios
  3. Personalização em Geral
  4. Reforma de Roda
  5. Centro Automotivo
  6. Envelopamento e Insulfilm
  7. Pintura Automotiva

---

## 🧪 Rodando os Testes Unitários

Para validar a integridade das regras de negócio de estoque e finanças, execute:
```powershell
.\mvnw.cmd test
```
Os testes cobrem:
- Entrada de estoque gerando custo.
- Finalização de ordem debitando o estoque de insumos.
- Validação/bloqueio de finalização de OS se o estoque for insuficiente.
