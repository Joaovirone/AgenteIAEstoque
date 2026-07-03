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

  ngOnInit(): void {
    this.recarregar();
  }

  recarregar(): void {
    this.carregando = true;
    this.erro = '';

    this.apiService.listarProdutos().subscribe({
      next: (produtos: Produto[]) => {
        this.produtos = produtos;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Nao foi possivel carregar os produtos.';
        this.carregando = false;
      }
    });
  }
}
