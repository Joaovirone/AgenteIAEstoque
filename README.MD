# AI Inventory Agent: Text-to-SQL com Spring AI e Llama 3

Um agente autônomo de Inteligência Artificial construído em **Java + Spring Boot** capaz de traduzir perguntas em linguagem natural diretamente para consultas SQL otimizadas e seguras. 

O projeto resolve o problema de acessibilidade a dados complexos de logística e estoque, permitindo que usuários façam perguntas simples via API REST e recebam dados precisos do banco de dados relacional. A aplicação é entregue em uma arquitetura 100% conteinerizada e automatizada.

## Arquitetura e Fluxo de Execução

O sistema utiliza o padrão de **Text-to-SQL**, isolando o banco de dados principal do Modelo de Linguagem (LLM) por motivos de segurança.

1. **A Pergunta:** O usuário envia uma pergunta em linguagem natural via endpoint HTTP para a API Spring Boot.
2. **Contextualização (Prompt Engineering):** O backend anexa o *Schema* do banco de dados (o Mini-Data Warehouse de estoque) e as regras de negócio ao prompt usando **Spring AI**.
3. **Geração (LLM):** O Llama 3 (rodando localmente via Ollama em um container isolado) processa a semântica e devolve estritamente a sintaxe SQL.
4. **Guardrails de Segurança:** O serviço de banco de dados intercepta o SQL, bloqueando comandos DML/DDL indesejados (`DROP`, `UPDATE`, `INSERT`).
5. **Execução Dinâmica:** O SQL é executado de forma segura no PostgreSQL e os dados são retornados em formato JSON estruturado.

## Tecnologias Utilizadas

*   **Backend:** Java, Spring Boot, Spring Web.
*   **Inteligência Artificial:** Spring AI, Ollama (Llama 3).
*   **Persistência:** Spring Data JPA, JdbcTemplate (para queries dinâmicas).
*   **Migrations:** Flyway DB (versionamento estruturado do banco).
*   **Testes:** JUnit 5, Mockito, Spring Boot Test (MockMvc).
*   **Infraestrutura e DevOps:** Docker, Docker Compose (com Healthchecks e Init Containers), Multi-stage Builds (para otimização de imagem da API).

## Pré-requisitos

Graças à arquitetura baseada em containers Multi-stage, você precisa apenas de uma ferramenta para rodar a aplicação completa:
*   [Docker e Docker Compose](https://www.docker.com/)

## Como Executar

A execução do ecossistema completo (Banco de Dados, Servidor LLM e API Backend) é totalmente automatizada (*Zero-Touch*).

Na raiz do projeto, execute o comando abaixo:
```bash
docker-compose up --build -d

## Endpoints da API 

A aplicação estará disponível em http://localhost:8080.

* POST /api/estoque/chat/perguntar (Gera uma consulta SQL baseada na pergunta e a executa no banco de dados)

    Request:

        {
            "pergunta": "Quais produtos da categoria Eletrônicos estão precisando de reposição (abaixo do estoque de segurança)?"
        }

    Response:

        {
        "sql_utilizado": "SELECT p.nome_produto, e.quantidade_disponivel FROM produtos p JOIN categorias c ON p.categoria_id = c.id JOIN estoque_atual e ON p.id = e.produto_id WHERE c.nome_categoria = 'Eletrônicos' AND e.quantidade_disponivel < e.estoque_minimo_seguranca;",
        "dados": [
            {
            "nome_produto": "Teclado Mecânico",
            "quantidade_disponivel": 5
            }
        ]
        }