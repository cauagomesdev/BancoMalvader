# 💰 Banco Malvader

> Sistema bancário completo com autenticação segura, operações financeiras, hierarquia de funcionários, auditoria e gerenciamento de contas, desenvolvido em Java com persistência em MySQL.

## 👨‍💻 Desenvolvido por
- **Cauã Aguiar**
- **Camila Graner**
- **Amanda Mendes**

## 🎯 Objetivo

O sistema **Banco Malvader** visa simular uma instituição bancária real, permitindo operações como:
- Cadastro e login com **senha + OTP**
- **Gerenciamento de contas** (Poupança, Corrente e Investimento)
- **Operações financeiras** (depósito, saque, transferência, extrato)
- **Cadastro e gestão de funcionários** com hierarquia
- **Geração de relatórios**
- **Auditoria e segurança de dados**

## 🧱 Arquitetura

- **Linguagem:** Java 8+
- **Banco de dados:** MySQL 8+
- **Interface gráfica:** Java Swing
- **Padrões usados:** MVC, DAO, Factory, Observer
- **Bibliotecas auxiliares:** JDBC, Apache Commons Codec (MD5), iText (PDF), Apache POI (Excel)

## 🗃️ Estrutura de Pastas

```bash
ProjetoBancoMalvader_prod/
│
├── controller/         # Lógica de negócios
├── dao/                # Data Access Object
├── model/              # Classes que representam as tabelas
├── util/               # Conexão com o banco e utilitários
├── view/               # Interfaces gráficas Swing
└── Main.java           # Classe principal para iniciar a aplicação
```

## ⚙️ Funcionalidades Implementadas

### 🧍 Cliente
- Consultar saldo e extrato
- Realizar depósitos, saques e transferências
- Visualizar limite e projeção de rendimento
- Logout com registro de auditoria

### 🧑‍💼 Funcionário
- Abertura, encerramento e alteração de contas
- Consulta e alteração de dados de clientes/funcionários
- Cadastro de funcionários com hierarquia
- Geração de relatórios em PDF e Excel

### 🔐 Autenticação
- Login com senha **criptografada (MD5)**
- **OTP** gerado via procedure e válido por 5 minutos
- Bloqueio após 3 tentativas falhas
- Registro de login/logout em tabela de auditoria

## 🔍 Banco de Dados

### Principais Tabelas
- `usuario`, `funcionario`, `cliente`
- `conta`, `conta_corrente`, `conta_poupanca`, `conta_investimento`
- `transacao`, `auditoria`, `relatorio`, `endereco`, `agencia`

### Gatilhos (Triggers)
- Atualização automática de saldo após transação
- Validação de senha forte
- Limite de depósito diário (R$10.000)

### Stored Procedures
- `gerar_otp(IN id_usuario INT)`
- `calcular_score_credito(IN id_cliente INT)`

### Views
- `vw_resumo_contas`
- `vw_movimentacoes_recentes`

## 📊 Relatórios

Funcionários podem gerar relatórios:
- De movimentações
- De clientes inadimplentes
- De desempenho de funcionários  
Exportados em **PDF ou Excel**.

## 🛠️ Instalação

### Pré-requisitos
- Java 8+
- MySQL 8.0+
- IDE (NetBeans, IntelliJ, Eclipse)

### Banco de Dados
1. Execute os scripts:
   - `schema.sql`
   - `triggers.sql`
   - `procedures.sql`
   - `views.sql`

2. Configure `database.properties` com os dados da sua conexão.

### Rodando a aplicação
```bash
# Compile e execute a classe:
BancoMalvaderApp.java
```

## 🔒 Segurança
- Senhas criptografadas com MD5
- OTP com expiração automática
- Auditoria de ações sensíveis
- Controle de permissões por cargo

## 🤖 Inteligência Artificial
Inclui módulo de **empréstimo com análise de risco por Machine Learning** (modelo externo simulado com resposta `"Aprovado"`, `"Negado"`, ou `"Aguardando Análise"`).

## ✅ Conclusão

O **Banco Malvader** entrega uma experiência bancária robusta, segura e didática, simulando operações reais de uma instituição financeira. Desenvolvido com foco em qualidade de software, segurança de dados e modelagem orientada a objetos, atende aos critérios técnicos exigidos pela disciplina de Banco de Dados e POO.