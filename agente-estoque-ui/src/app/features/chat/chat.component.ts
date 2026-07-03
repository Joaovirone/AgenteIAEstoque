import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
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
        this.mensagens.push({ texto: 'Erro ao conectar com o servidor.', autor: 'ia' });
        this.carregando = false;
      }
    });
  }
}