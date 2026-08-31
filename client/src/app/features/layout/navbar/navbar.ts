import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  standalone: true,
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly session = this.authService.authSession;

  readonly isClient = computed<boolean>(() => this.session()?.roles?.includes('CLIENT') ?? false);

  readonly isProfessional = computed<boolean>(
    () => this.session()?.roles?.includes('PROFESSIONAL') ?? false,
  );

  onLogout(): void {
    this.authService.logout();
    void this.router.navigateByUrl('/auth/login');
  }
}
