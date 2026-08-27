import { Component, OnInit, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { JobsService } from '../../../core/services/jobs.service';
import { JobSummaryResponse, Page } from '../../../shared/models/job.model';

const STATUS_LABELS: Record<string, string> = {
  PUBLISHED: 'Publicado',
  IN_PROGRESS: 'En progreso',
  PENDING_CONFIRMATION: 'Pendiente de confirmación',
  COMPLETED: 'Completado',
  CANCELED: 'Cancelado',
  HIDDEN: 'Oculto',
};

@Component({
  selector: 'app-job-list',
  imports: [],
  templateUrl: './job-list.html',
})
export class JobListComponent implements OnInit {
  private readonly jobsService = inject(JobsService);
  private readonly authService = inject(AuthService);

  readonly jobs = signal<JobSummaryResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly page = signal(0);
  readonly pageSize = 10;
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  get isClient(): boolean {
    return this.authService.authSession()?.roles?.includes('CLIENT') ?? false;
  }

  get heading(): string {
    return this.isClient ? 'Mis trabajos' : 'Trabajos disponibles';
  }

  get emptyMessage(): string {
    return this.isClient
      ? 'Aún no has publicado ningún trabajo.'
      : 'No hay trabajos disponibles en este momento.';
  }

  ngOnInit(): void {
    this.loadJobs();
  }

  previousPage(): void {
    if (this.page() > 0) {
      this.page.update((p) => p - 1);
      this.loadJobs();
    }
  }

  nextPage(): void {
    if (this.page() < this.totalPages() - 1) {
      this.page.update((p) => p + 1);
      this.loadJobs();
    }
  }

  statusLabel(status: string): string {
    return STATUS_LABELS[status] ?? status;
  }

  formatCurrency(amount: number, currency: string): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: currency || 'USD',
    }).format(amount);
  }

  private loadJobs(): void {
    this.loading.set(true);
    this.error.set('');

    this.jobsService.listJobs({ page: this.page(), size: this.pageSize }).subscribe({
      next: (result: Page<JobSummaryResponse>) => {
        this.jobs.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los trabajos. Inténtalo de nuevo.');
        this.loading.set(false);
      },
    });
  }
}
