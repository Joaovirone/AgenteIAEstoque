import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ApiService } from '../../core/services/api/api.services';
import { Produto } from '../../core/models/produto.model';

@Component({
  selector: 'app-estoque',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './estoque.html',
  styleUrl: './estoque.scss',
})
export class Estoque implements OnInit {
  private readonly apiService = inject(ApiService);

  produtos: Produto[] = [];
  carregando = false;
  erro = '';
  paginaAtual = 1;
  readonly itensPorPagina = 10;

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.produtos.length / this.itensPorPagina));
  }

  get paginasDisponiveis(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, index) => index + 1);
  }

  get produtosPaginados(): Produto[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.produtos.slice(inicio, fim);
  }

  ngOnInit(): void {
    this.recarregar();
  }

  recarregar(): void {
    this.carregando = true;
    this.erro = '';

    this.apiService.listarProdutos().subscribe({
      next: (produtos: Produto[]) => {
        this.produtos = produtos;
        this.paginaAtual = 1;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Nao foi possivel carregar os produtos. Verifique se a API esta em execucao.';
        this.carregando = false;
      }
    });
  }

  paginaAnterior(): void {
    if (this.paginaAtual > 1) {
      this.paginaAtual -= 1;
    }
  }

  proximaPagina(): void {
    if (this.paginaAtual < this.totalPaginas) {
      this.paginaAtual += 1;
    }
  }

  irParaPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaAtual = pagina;
    }
  }
}
