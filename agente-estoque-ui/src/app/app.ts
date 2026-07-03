import { Component } from '@angular/core';
import { ChatComponent } from './features/chat/chat.component';
import { Estoque } from './features/estoque/estoque';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ChatComponent, Estoque],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  readonly titulo = 'AI Inventory Agent';
}
