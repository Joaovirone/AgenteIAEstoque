import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necessário para o [(ngModel)] do input
import { ApiService } from '../../core/services/api/api.service';

interface Mensagem {
  texto: string;
  autor: 'usuario' | 'ia';
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css' // ou .scss dependendo de como você gerou
})
export class ChatComponent {
  
  private apiService = inject(ApiService);

  mensagens: Mensagem[] = [
    { texto: 'Olá! Sou o seu assistente de estoque logístico (RAG + SQL). Como posso ajudar?', autor: 'ia' }
  ];
  
  novaPergunta: string = '';
  carregando: boolean = false;

  enviarMensagem() {
    if (!this.novaPergunta.trim() || this.carregando) return;

    // 1. Adiciona a pergunta do usuário na tela
    const perguntaTexto = this.novaPergunta;
    this.mensagens.push({ texto: perguntaTexto, autor: 'usuario' });
    this.novaPergunta = ''; // Limpa o input
    this.carregando = true;

    // 2. Chama o Spring Boot através do ApiService
    this.apiService.enviarPerguntaChat({ pergunta: perguntaTexto }).subscribe({
      next: (response) => {
        // Sucesso: Adiciona a resposta da IA na tela
        this.mensagens.push({ texto: response.resposta, autor: 'ia' });
        this.carregando = false;
      },
      error: (err) => {
        // Erro: Mostra mensagem de falha
        console.error('Erro na API:', err);
        this.mensagens.push({ texto: '❌ Erro ao conectar com o servidor.', autor: 'ia' });
        this.carregando = false;
      }
    });
  }
}