import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly session = this.authService.authSession;

  get isClient(): boolean {
    return this.session()?.roles?.includes('CLIENT') ?? false;
  }

  get isProfessional(): boolean {
    return this.session()?.roles?.includes('PROFESSIONAL') ?? false;
  }

  onLogout(): void {
    this.authService.logout();
    void this.router.navigateByUrl('/auth/login');
  }
}
