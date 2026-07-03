import { TestBed } from '@angular/core/testing';
import { LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { of } from 'rxjs';
import { App } from './app';
import { ApiService } from './core/services/api/api.services';
import { Produto } from './core/models/produto.model';

registerLocaleData(localePt);

describe('App', () => {
  const produtosMock: Produto[] = [
    {
      id: '1',
      nome: 'Teclado Mecanico',
      sku: 'TEC-001',
      preco: 199.9,
      status: 'ATIVO'
    },
    {
      id: '2',
      nome: 'Mouse Gamer',
      sku: 'MOU-010',
      preco: 149.5,
      status: 'INATIVO'
    }
  ];

  const apiServiceMock = {
    listarProdutos: () => of(produtosMock),
    enviarPerguntaChat: () => of({ resposta: 'Temos 2 itens cadastrados no estoque.' })
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: ApiService, useValue: apiServiceMock },
        { provide: LOCALE_ID, useValue: 'pt-BR' }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render dashboard title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('AI Inventory Agent');
  });

  it('should load products in stock table', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('tbody tr');

    expect(rows.length).toBe(2);
    expect(compiled.textContent).toContain('Teclado Mecanico');
    expect(compiled.textContent).toContain('Mouse Gamer');
  });

  it('should send a message and render AI response', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;
    const input = compiled.querySelector('.input-area input') as HTMLInputElement;
    const button = compiled.querySelector('.input-area button') as HTMLButtonElement;

    input.value = 'Quantos itens temos?';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    button.click();
    fixture.detectChanges();
    await fixture.whenStable();

    expect(compiled.textContent).toContain('Quantos itens temos?');
    expect(compiled.textContent).toContain('Temos 2 itens cadastrados no estoque.');
  });
});
