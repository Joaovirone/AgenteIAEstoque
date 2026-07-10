import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api/api.services';
import { ChatResponse } from '../../core/models/produto.model';

interface Mensagem {
  texto: string;
  autor: 'usuario' | 'ia';
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.scss'
})
export class ChatComponent {
  
  private readonly apiService = inject(ApiService);

  mensagens: Mensagem[] = [
    { texto: 'Olá! Sou o seu assistente de estoque logístico (RAG + SQL). Como posso ajudar?', autor: 'ia' }
  ];
  
  novaPergunta: string = '';
  carregando: boolean = false;

  enviarMensagem(): void {
    if (!this.novaPergunta.trim() || this.carregando) return;

    const perguntaTexto = this.novaPergunta;
    this.mensagens.push({ texto: perguntaTexto, autor: 'usuario' });
    this.novaPergunta = '';
    this.carregando = true;

    this.apiService.enviarPerguntaChat({ pergunta: perguntaTexto }).subscribe({
      next: (response: ChatResponse) => {
        this.mensagens.push({ texto: response.resposta, autor: 'ia' });
        this.carregando = false;
      },
      error: (err: unknown) => {
        console.error('Erro na API:', err);

        if (err instanceof HttpErrorResponse) {
          if (err.status === 504) {
            this.mensagens.push({ texto: 'A consulta demorou demais. Tente uma pergunta mais objetiva.', autor: 'ia' });
          } else if (err.status === 503) {
            this.mensagens.push({ texto: 'Os modelos de IA ainda estao inicializando. Aguarde alguns instantes.', autor: 'ia' });
          } else if (err.error?.resposta) {
            this.mensagens.push({ texto: err.error.resposta, autor: 'ia' });
          } else {
            this.mensagens.push({ texto: 'Erro ao conectar com o servidor.', autor: 'ia' });
          }
        } else {
          this.mensagens.push({ texto: 'Erro ao conectar com o servidor.', autor: 'ia' });
        }

        this.carregando = false;
      }
    });
  }
}