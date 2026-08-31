import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { JobsService } from '../../../core/services/jobs.service';
import { OffersService } from '../../../core/services/offers.service';
import { JobSummaryResponse, Page } from '../../../shared/models/job.model';
import { CreateOfferRequest, OfferResponse } from '../../../shared/models/offer.model';

const STATUS_LABELS: Record<string, string> = {
  PUBLISHED: 'Publicado',
  IN_PROGRESS: 'En progreso',
  PENDING_CONFIRMATION: 'Pendiente de confirmación',
  COMPLETED: 'Completado',
  CANCELED: 'Cancelado',
  HIDDEN: 'Oculto',
};

const OFFER_STATUS_LABELS: Record<string, string> = {
  PENDING: 'Pendiente',
  ACCEPTED: 'Aceptada',
  REJECTED: 'Rechazada',
  WITHDRAWN: 'Retirada',
};

@Component({
  selector: 'app-job-list',
  imports: [ReactiveFormsModule],
  templateUrl: './job-list.html',
  standalone: true,
})
export class JobListComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly jobsService = inject(JobsService);
  private readonly offersService = inject(OffersService);
  private readonly authService = inject(AuthService);

  readonly jobs = signal<JobSummaryResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly page = signal(0);
  readonly pageSize = 10;
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly activeOfferJobId = signal<string | null>(null);
  readonly isSubmitting = signal(false);
  readonly offerError = signal('');
  readonly successMessage = signal('');

  readonly activeOffersJobId = signal<string | null>(null);
  readonly offers = signal<OfferResponse[]>([]);
  readonly offersLoading = signal(false);
  readonly offersError = signal('');
  readonly acceptingOfferId = signal<string | null>(null);

  readonly offerForm = this.fb.nonNullable.group({
    offeredPrice: [0, [Validators.required, Validators.min(0.01)]],
    message: [''],
  });

  get isClient(): boolean {
    return this.authService.authSession()?.roles?.includes('CLIENT') ?? false;
  }

  get isProfessional(): boolean {
    return this.authService.authSession()?.roles?.includes('PROFESSIONAL') ?? false;
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

  offerStatusLabel(status: string): string {
    return OFFER_STATUS_LABELS[status] ?? status;
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleDateString('es-AR');
  }

  formatCurrency(amount: number, currency: string): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: currency || 'USD',
    }).format(amount);
  }

  toggleOfferForm(jobId: string): void {
    this.activeOfferJobId.update((current) => (current === jobId ? null : jobId));
    this.offerForm.reset({ offeredPrice: 0, message: '' });
    this.offerError.set('');
  }

  toggleOffers(jobId: string): void {
    this.activeOffersJobId.update((current) => (current === jobId ? null : jobId));
    this.offersError.set('');

    if (this.activeOffersJobId() === jobId) {
      this.loadOffers(jobId);
    } else {
      this.offers.set([]);
    }
  }

  acceptOffer(jobId: string, offerId: string): void {
    if (this.acceptingOfferId()) {
      return;
    }

    this.acceptingOfferId.set(offerId);
    this.successMessage.set('');

    this.jobsService.acceptOffer(jobId, offerId).subscribe({
      next: () => {
        this.acceptingOfferId.set(null);
        this.successMessage.set('Oferta aceptada. El trabajo está en progreso.');
        this.loadOffers(jobId);
        this.loadJobs();
      },
      error: (error: HttpErrorResponse) => {
        const message =
          typeof error.error?.message === 'string'
            ? error.error.message
            : 'No se pudo aceptar la oferta. Inténtalo de nuevo.';
        this.offersError.set(message);
        this.acceptingOfferId.set(null);
      },
    });
  }

  private loadOffers(jobId: string): void {
    this.offersLoading.set(true);
    this.offersError.set('');

    this.offersService.listOffersByJob(jobId).subscribe({
      next: (offers: OfferResponse[]) => {
        this.offers.set(offers);
        this.offersLoading.set(false);
      },
      error: () => {
        this.offersError.set('No se pudieron cargar las ofertas. Inténtalo de nuevo.');
        this.offersLoading.set(false);
      },
    });
  }

  submitOffer(jobId: string): void {
    if (this.offerForm.invalid || this.isSubmitting()) {
      this.offerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.offerError.set('');

    const message = this.offerForm.controls.message.value.trim();
    const payload: CreateOfferRequest = {
      jobId,
      offeredPrice: this.offerForm.controls.offeredPrice.value,
      ...(message ? { message } : {}),
    };

    this.offersService.createOffer(payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.successMessage.set('Oferta enviada correctamente.');
        this.activeOfferJobId.set(null);
        this.offerForm.reset({ offeredPrice: 0, message: '' });
        this.loadJobs();
      },
      error: (error: HttpErrorResponse) => {
        const message =
          typeof error.error?.message === 'string'
            ? error.error.message
            : 'No se pudo enviar la oferta. Inténtalo de nuevo.';
        this.offerError.set(message);
        this.isSubmitting.set(false);
      },
    });
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
