# 🔧 Piston & Wood — ERP Custom Garage

> Protótipo funcional de sistema ERP para gestão de um centro automotivo de customização.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-green?style=flat-square&logo=springboot)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-template_engine-005F0F?style=flat-square&logo=thymeleaf)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4-38BDF8?style=flat-square&logo=tailwindcss)
![H2 Database](https://img.shields.io/badge/H2-Database-blue?style=flat-square)

---

## 📋 Sobre o Projeto

Sistema ERP desenvolvido para cobrir o ciclo completo de operação de uma garagem de customização automotiva. O sistema integra gestão de ordens de serviço, controle de estoque, apuração financeira, cadastro de clientes e envio automático de e-mails — tudo em uma interface dark moderna.

---

## ✨ Funcionalidades

- **📊 Dashboard** — KPIs em tempo real (receita, custos, lucro líquido) com gráficos interativos de pizza e barras
- **🔧 Ordens de Serviço** — Abertura, acompanhamento, conclusão e cancelamento de OS com 7 categorias de serviço
- **📦 Estoque** — Controle de insumos com alertas de estoque mínimo e baixa automática na conclusão da OS
- **💰 Financeiro** — Lançamento de despesas e apuração de resultados integrada ao dashboard
- **👤 Clientes** — Cadastro com informações do veículo (modelo, placa, ano)
- **📧 E-mails Automáticos** — Cliente notificado na criação e na conclusão da OS via Gmail SMTP

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17 |
| Backend | Spring Boot 4.1.0 (Web MVC, JPA, Mail) |
| Template Engine | Thymeleaf |
| CSS | Tailwind CSS v4 (CDN) |
| Gráficos | Chart.js (CDN) |
| Banco de Dados | H2 (arquivo local persistente) |
| Build | Maven Wrapper (`mvnw.cmd`) |
| Testes | JUnit 5 + Spring Boot Test |

---

## 🚀 Como Executar

> **Pré-requisito único:** Java 17 instalado. O Maven Wrapper já está incluído — não é necessário instalar o Maven.

**1. Clone o repositório e acesse a pasta:**
```bash
git clone https://github.com/Cassiano-Behemonth/erp-prototype.git
cd erp-prototype
```

**2. Suba a aplicação:**
```powershell
.\mvnw.cmd spring-boot:run
```

**3. Acesse no navegador:**

| Recurso | URL |
|---------|-----|
| Sistema ERP | http://localhost:8085 |
| Console do Banco H2 | http://localhost:8085/h2-console |

> **Credenciais H2:** JDBC URL `jdbc:h2:file:./db/erp_db` · Usuário `sa` · Senha *(em branco)*

---

## ⚙️ Regras de Negócio

### Ordens de Serviço
- Ao **concluir** uma OS, o estoque é debitado automaticamente pelos insumos vinculados.
- Se algum insumo estiver com estoque insuficiente, a conclusão é **bloqueada** com mensagem de erro.
- E-mail HTML é enviado ao cliente com o resumo do serviço e lista de itens utilizados.

### Estoque
- Toda **entrada de estoque** gera automaticamente uma despesa do tipo `Aquisição de Estoque` no financeiro.
- Produtos abaixo do nível mínimo disparam alertas no dashboard e na listagem.

### Categorias de Serviço
1. Especialização em Marcenaria Automotiva
2. Instalação de Som e Acessórios
3. Personalização em Geral
4. Reforma de Roda
5. Centro Automotivo
6. Envelopamento e Insulfilm
7. Pintura Automotiva

---

## 🧪 Testes

```powershell
.\mvnw.cmd test
```

Coberturas:
- ✅ Entrada de estoque gera lançamento de custo automático
- ✅ Conclusão de OS debita insumos do estoque
- ✅ Bloqueio de conclusão com estoque insuficiente

---

## 📁 Estrutura do Projeto

```
src/main/java/com/erp/prototype/
├── config/         # DataLoader — dados iniciais (mock)
├── controller/     # Controllers MVC
├── model/          # Entidades JPA
├── repository/     # Spring Data Repositories
└── service/        # Lógica de negócio (OS, Estoque, Financeiro, E-mail)

src/main/resources/
├── application.properties   # Porta, banco, SMTP
└── templates/               # Templates Thymeleaf por módulo
    ├── dashboard.html
    ├── ordens/
    ├── estoque/
    ├── clientes/
    └── custos/
```

---

## 📧 Configuração de E-mail

Edite `src/main/resources/application.properties` para usar sua conta:

```properties
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-de-app
```

> Utilize uma **Senha de App** do Google (não a senha da conta). Acesse: *Google Account → Segurança → Senhas de app*.

---

## 📦 Dados Iniciais

Na primeira execução o sistema popula automaticamente:
- **3 clientes** com dados de veículo
- **5 produtos** (Película Carbono, MDF Naval, Tinta Automotiva, Alto-falantes JBL, Wrap Vinyl)
- **3 despesas operacionais** (aluguel, energia, ferramentas)

---

<p align="center">Desenvolvido por <a href="https://github.com/Cassiano-Behemonth">Cassiano Behemonth</a></p>
