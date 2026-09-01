import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar/navbar';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, NavbarComponent],
  templateUrl: './layout.html',
})
export class LayoutComponent {}
