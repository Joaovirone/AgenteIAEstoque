# AI Inventory Agent - Text-to-SQL com Spring AI

Um **agente autônomo de Inteligência Artificial** construído em **Java + Spring Boot** capaz de traduzir perguntas em linguagem natural diretamente em consultas SQL otimizadas e seguras.

> **Objetivo:** Tornar acessível dados complexos de logística e estoque através de perguntas simples em linguagem natural, sem necessidade de conhecimento em SQL.

---

## 📋 Índice

- [O que é](#o-que-é)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Começar Rápido](#começar-rápido)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos de Uso](#exemplos-de-uso)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Desenvolvimento](#desenvolvimento)
- [Troubleshooting](#troubleshooting)

---

## O que é

**AI Inventory Agent** é uma solução que elimina a barreira técnica entre usuários não-técnicos e dados estruturados. Ele funciona assim:

1. **Entrada:** Você faz uma pergunta em português natural
2. **Processamento:** Uma IA (Llama 3) traduz a pergunta para SQL seguro
3. **Execução:** O SQL é executado no PostgreSQL com validações de segurança
4. **Saída:** Você recebe os dados em formato JSON estruturado

**Caso de Uso Real:**
```
Gerente: "Quais produtos estão com estoque baixo?"
↓
Sistema gera e executa SQL seguro
↓
Resposta: [{ nome: "Teclado", quantidade: 5 }, ...]
```

---

## Arquitetura

O sistema utiliza o padrão **Text-to-SQL** com isolamento de segurança entre camadas:

```
┌─────────────────────────────────────────────────────────┐
│  CLIENT (REST API)                                       │
│  POST /api/estoque/chat/perguntar                       │
│  { "pergunta": "..." }                                  │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│  API BACKEND (Spring Boot)                              │
│  - Recebe pergunta em linguagem natural                 │
│  - Prepara contexto (schema + regras de negócio)        │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│  AGENTE LLM (Ollama + Llama 3)                         │
│  - Processa semântica                                   │
│  - Gera SQL semanticamente correto                      │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│  GUARDRAILS DE SEGURANÇA (Java)                        │
│  - Bloqueia: DROP, UPDATE, DELETE, INSERT              │
│  - Permite apenas: SELECT                              │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│  DATABASE (PostgreSQL)                                  │
│  - Executa SQL validado                                 │
│  - Retorna dados                                        │
└─────────────────────────────────────────────────────────┘
```

### Fluxo Detalhado

1. **Pergunta:** Usuário envia pergunta via HTTP
2. **Contextualização:** Backend anexa schema do BD + regras de negócio ao prompt
3. **Geração:** Llama 3 traduz para SQL
4. **Validação:** Sistema bloqueia comandos perigosos (DML/DDL)
5. **Execução:** SQL seguro executado no PostgreSQL
6. **Resposta:** Dados retornados em JSON estruturado

---

## 🛠️ Tecnologias

| Camada | Tecnologias |
|--------|-------------|
| **Backend** | Java 21, Spring Boot 3.5, Spring Web, Spring Security |
| **IA/LLM** | Spring AI, Ollama, Llama 3 |
| **Dados** | PostgreSQL, Spring Data JPA, JdbcTemplate, Flyway |
| **Testes** | JUnit 5, Mockito, Spring Boot Test |
| **DevOps** | Docker, Docker Compose, Multi-stage Builds |

---

## Pré-requisitos

### Mínimo Recomendado

- **Docker Desktop** (v20.10+) - [Download](https://www.docker.com/products/docker-desktop)
- **Docker Compose** (v1.29+) - geralmente incluído no Docker Desktop

### Requisitos de Hardware

- **CPU:** 2+ cores
- **RAM:** 4GB mínimo (6GB+ recomendado para Ollama)
- **Disk:** 10GB livres (5GB para imagens Docker)

### Verificar Instalação

```bash
docker --version
docker-compose --version
```

---

## Começar Rápido

### Clonar/Acessar o Repositório

```bash
cd /caminho/do/projeto
```

### Iniciar o Ecossistema Completo

```bash
docker-compose up --build -d
```

**O que será iniciado:**
-  **PostgreSQL** (porta 5432) - Banco de dados relacional
-  **Ollama + Llama 3** (porta 11434) - Servidor LLM
-  **API Backend** (porta 8080) - Spring Boot Application

### Aguarde o Health Check

```bash
docker-compose ps
```

Espere todos os serviços estarem com status `Up` 

### Testear a API

```bash
curl -X POST http://localhost:8080/api/estoque/chat/perguntar \
  -H "Content-Type: application/json" \
  -d '{"pergunta": "Quais produtos estão com estoque baixo?"}'
```

### Parar o Sistema

```bash
docker-compose down
```

> **Dados Persistem:** O volume Docker mantém os dados do PostgreSQL mesmo após parar os containers.

---

## 📡 Endpoints da API

### Base URL
```
http://localhost:8080
```

### 1. Chat - Perguntar (Text-to-SQL)

**Endpoint:**
```
POST /api/estoque/chat/perguntar
```

**Request:**
```json
{
  "pergunta": "Quais produtos da categoria Eletrônicos estão precisando de reposição (abaixo do estoque de segurança)?"
}
```

**Response (200 OK):**
```json
{
  "sql_utilizado": "SELECT p.nome_produto, e.quantidade_disponivel FROM produtos p JOIN categorias c ON p.categoria_id = c.id JOIN estoque_atual e ON p.id = e.produto_id WHERE c.nome_categoria = 'Eletrônicos' AND e.quantidade_disponivel < e.estoque_minimo_seguranca;",
  "dados": [
    {
      "nome_produto": "Teclado Mecânico",
      "quantidade_disponivel": 5
    },
    {
      "nome_produto": "Monitor 24\"",
      "quantidade_disponivel": 2
    }
  ]
}
```

**Headers Recomendados:**
```
Content-Type: application/json
```

---

## Exemplos de Uso

### Exemplo 1: Produtos com Estoque Baixo
```bash
curl -X POST http://localhost:8080/api/estoque/chat/perguntar \
  -H "Content-Type: application/json" \
  -d '{
    "pergunta": "Quantos produtos temos em estoque de segurança crítico?"
  }'
```

### Exemplo 2: Movimentação de Estoque
```bash
curl -X POST http://localhost:8080/api/estoque/chat/perguntar \
  -H "Content-Type: application/json" \
  -d '{
    "pergunta": "Quais foram as últimas movimentações de entrada de estoque?"
  }'
```

### Exemplo 3: Análise por Categoria
```bash
curl -X POST http://localhost:8080/api/estoque/chat/perguntar \
  -H "Content-Type: application/json" \
  -d '{
    "pergunta": "Qual categoria tem maior valor total em estoque?"
  }'
```

---

## Estrutura do Projeto

```
.
├── src/main/java/com/AgentAIEstoque/application/
│   ├── Application.java                 # Classe main do Spring Boot
│   ├── config/
│   │   └── FerramentasDoAgenteConfig.java   # Configuração do Spring AI
│   ├── controller/
│   │   ├── ChatController.java          # Endpoints do chat
│   │   └── ProdutoController.java       # Endpoints de produtos
│   ├── dto/
│   │   ├── ChatResponseDTO.java         # Response do chat
│   │   ├── PerguntaRequestDTO.java      # Request de pergunta
│   │   └── mapper/                      # Mapeadores DTO ↔ Entity
│   ├── entity/
│   │   ├── Produto.java
│   │   ├── Categoria.java
│   │   ├── EstoqueAtual.java
│   │   └── MovimentacaoEstoque.java
│   ├── repository/                      # Spring Data JPA
│   ├── service/
│   │   ├── AgenteTextToSqlService.java      # Integração LLM
│   │   ├── DatabaseExecutionService.java    # Execução SQL segura
│   │   ├── OrquestradorHibridoService.java  # Orquestração
│   │   └── RagSearchService.java            # Busca RAG
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── RegraNegocioException.java
├── src/main/resources/
│   ├── application.yaml                 # Configurações Spring
│   ├── db/migration/                    # Scripts Flyway
│   │   ├── V1__criar_tabelas_iniciais.sql
│   │   ├── V2__inserir_dados_estoque.sql
│   │   └── V3__criar_extensao_vector.sql
│   └── prompts/
│       ├── system-prompt-estoque.st     # Prompt do sistema
│       └── 1.txt
├── docker-compose.yaml                  # Orquestração dos containers
├── Dockerfile                           # Build multi-stage da API
└── pom.xml                              # Dependências Maven
```

---

##  Desenvolvimento

### Build Local (sem Docker)

**Pré-requisitos:**
- Java 21+
- Maven 3.8+
- PostgreSQL 14+ (rodando localmente)
- Ollama rodando localmente

**Passos:**

1. **Instalar dependências:**
```bash
./mvnw clean install
```

2. **Configurar application.yaml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/estoque_db
    username: seu_usuario
    password: sua_senha
  jpa:
    hibernate:
      ddl-auto: validate
  ai:
    ollama:
      base-url: http://localhost:11434
      model: llama3
```

3. **Executar a aplicação:**
```bash
./mvnw spring-boot:run
```

### Executar Testes

```bash
./mvnw test
```

### Análise de Cobertura de Testes

```bash
./mvnw test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

### Build de Produção

```bash
./mvnw clean package -DskipTests
```

---

## Troubleshooting

### Erro: "Connection refused: ollama"

**Solução:**
```bash
# Verificar se Ollama está rodando
docker-compose ps

# Reiniciar Ollama
docker-compose restart ollama

# Verificar logs
docker-compose logs ollama
```

### Erro: "Database connection failed"

**Solução:**
```bash
# Verificar PostgreSQL
docker-compose logs postgres

# Reiniciar banco de dados
docker-compose restart postgres

# Verificar volume
docker volume ls | grep estoque
```

### API retorna timeout

**Solução:**
- Aumente o timeout no `application.yaml`
- Aumente a RAM disponível para Docker
- Monitore CPU: `docker stats`

### "Permission denied" ao executar docker-compose

**Solução (Linux/Mac):**
```bash
sudo usermod -aG docker $USER
# Faça logout e login novamente
```

### Portas já em uso

**Solução:**
```bash
# Verificar qual processo usa a porta
lsof -i :8080  # API
lsof -i :5432  # PostgreSQL
lsof -i :11434 # Ollama

# Ou mudar ports no docker-compose.yaml
```

---

## Monitoramento

### Ver Logs em Tempo Real

```bash
# Todos os serviços
docker-compose logs -f

# Apenas um serviço
docker-compose logs -f api
docker-compose logs -f postgres
docker-compose logs -f ollama
```

### Health Check

```bash
# PostgreSQL
docker-compose exec postgres pg_isready -U postgres

# Ollama
curl http://localhost:11434/api/tags

# API
curl http://localhost:8080/actuator/health
```

---

## Notas Importantes

-  **Segurança:** O sistema bloqueia automaticamente comandos DDL/DML (DROP, UPDATE, DELETE, INSERT)
-  **Dados:** O PostgreSQL usa volume Docker para persistência
-  **Migrations:** Flyway gerencia versionamento automático do schema
-  **Auth:** O Spring Security está habilitado - configure credenciais em produção
-  **LLM:** Ollama baixa o modelo Llama 3 automaticamente na primeira execução (~4GB)

---

## Suporte

Para dúvidas ou problemas:
1. Consulte os logs: `docker-compose logs`
2. Verifique a seção **Troubleshooting** acima
3. Valide a arquitetura (portas, volumes, healthchecks)

---


---

**Última atualização:** Maio 2026