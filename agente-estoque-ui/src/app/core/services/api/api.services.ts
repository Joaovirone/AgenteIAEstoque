import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { ChatRequest, ChatResponse, Produto } from '../../models/produto.model';

type RuntimeEnv = {
  API_URL?: string;
};

type RuntimeWindow = Window & {
  __env?: RuntimeEnv;
};

@Injectable({
  providedIn: 'root' // Isso garante que o serviço seja um Singleton (instância única)
})
export class ApiService {
  
  // Injeta o HttpClient nativo do Angular
  private http = inject(HttpClient);
  
  // URL base da API definida em runtime pelo container (com fallback local)
  private readonly API_URL =
    (window as RuntimeWindow).__env?.API_URL ?? 'http://localhost:8080/api';

  // Método para falar com a IA
  enviarPerguntaChat(request: ChatRequest): Observable<ChatResponse> {
    return this.http
      .post<ChatResponse>(`${this.API_URL}/estoque/chat/perguntar`, request)
      .pipe(timeout(60000));
  }

  // Método para listar produtos no painel de estoque
  listarProdutos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.API_URL}/produtos`);
  }
}