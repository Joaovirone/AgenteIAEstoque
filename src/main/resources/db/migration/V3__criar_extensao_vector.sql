CREATE EXTENSION IF NOT EXISTS vector;

-- Tabela padrão exigida pelo Spring AI para armazenar os trechos de documentos
CREATE TABLE vector_store (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content TEXT, -- O pedaço do texto (ex: parágrafo do manual)
    metadata JSONB, -- Dados extras (ex: nome do PDF, número da página)
    embedding vector(768) -- O vetor matemático representando o texto
);

-- Cria um índice HNSW para buscas ultra-rápidas, crucial para larga escala
CREATE INDEX ON vector_store USING hnsw (embedding vector_cosine_ops);