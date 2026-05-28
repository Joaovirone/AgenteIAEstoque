// Esse é o espelho exato do seu ProdutoResponseDTO do Java
export interface Produto {
  id: string; // Lembre-se que mudamos para UUID no backend!
  nome: string;
  sku: string;
  preco: number;
  status: string;
}

// O espelho do seu PerguntaRequestDTO
export interface ChatRequest {
  pergunta: string;
}

// O espelho do seu ChatResponseDTO
export interface ChatResponse {
  resposta: string;
}