CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY,
    nome_categoria VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS produtos (
    id UUID PRIMARY KEY,
    categoria_id UUID NOT NULL,
    sku VARCHAR(255) NOT NULL,
    nome_produto VARCHAR(255) NOT NULL,
    preco_custo DECIMAL (10, 2) NOT NULL,
    status_produto VARCHAR(50) NOT NULL,
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS estoque_atual (
    id UUID PRIMARY KEY,
    produto_id UUID NOT NULL,
    local_armazenamento VARCHAR(255) NOT NULL,
    quantidade_disponivel INT NOT NULL,
    estoque_minimo_seguranca INT NOT NULL,
    CONSTRAINT fk_estoque_produtp FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE movimentacoes_estoque (
    id UUID PRIMARY KEY,
    produto_id UUID NOT NULL,
    tipo_movimento VARCHAR(50) NOT NULL,
    quantidade_movimentada INT NOT NULL,
    data_movimento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimentacao_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);