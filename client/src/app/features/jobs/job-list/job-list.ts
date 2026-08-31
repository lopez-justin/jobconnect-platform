import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { JobsService } from '../../../core/services/jobs.service';
import { OffersService } from '../../../core/services/offers.service';
import { JobSummaryResponse } from '../../../shared/models/job.model';
import { Page } from '../../../shared/models/pagination.model';
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
  private readonly destroyRef = inject(DestroyRef);

  readonly jobs = signal<JobSummaryResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly page = signal(0);
  readonly pageSize = 10;
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly activeTab = signal<'available' | 'myJobs'>('available');

  readonly myJobs = signal<JobSummaryResponse[]>([]);
  readonly myJobsLoading = signal(false);
  readonly myJobsError = signal('');
  readonly myJobsPage = signal(0);
  readonly myJobsTotalPages = signal(0);
  readonly myJobsTotalElements = signal(0);

  readonly activeOfferJobId = signal<string | null>(null);
  readonly isSubmitting = signal(false);
  readonly offerError = signal('');
  readonly successMessage = signal('');

  readonly activeOffersJobId = signal<string | null>(null);
  readonly offers = signal<OfferResponse[]>([]);
  readonly offersLoading = signal(false);
  readonly offersError = signal('');
  readonly acceptingOfferId = signal<string | null>(null);

  readonly pendingCompletionJobId = signal<string | null>(null);
  readonly confirmCompletionJobId = signal<string | null>(null);
  readonly actionInProgress = signal(false);

  readonly offerForm = this.fb.nonNullable.group({
    offeredPrice: [0, [Validators.required, Validators.min(0.01)]],
    message: [''],
  });

  readonly isClient = computed<boolean>(
    () => this.authService.authSession()?.roles?.includes('CLIENT') ?? false,
  );

  readonly isProfessional = computed<boolean>(
    () => this.authService.authSession()?.roles?.includes('PROFESSIONAL') ?? false,
  );

  readonly heading = computed<string>(() => (this.isClient() ? 'Mis trabajos' : 'Trabajos'));

  readonly emptyMessage = computed<string>(() => {
    if (this.isClient()) {
      return 'Aún no has publicado ningún trabajo.';
    }
    return this.activeTab() === 'myJobs'
      ? 'Todavía no tienes trabajos asignados.'
      : 'No hay trabajos disponibles en este momento.';
  });

  ngOnInit(): void {
    if (this.isProfessional()) {
      this.loadMyJobs();
    }
    this.loadJobs();
  }

  switchTab(tab: 'available' | 'myJobs'): void {
    this.activeTab.set(tab);
    this.error.set('');
    this.myJobsError.set('');
    this.successMessage.set('');
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

  previousMyJobsPage(): void {
    if (this.myJobsPage() > 0) {
      this.myJobsPage.update((p) => p - 1);
      this.loadMyJobs();
    }
  }

  nextMyJobsPage(): void {
    if (this.myJobsPage() < this.myJobsTotalPages() - 1) {
      this.myJobsPage.update((p) => p + 1);
      this.loadMyJobs();
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

    this.jobsService
      .acceptOffer(jobId, offerId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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

  markAsCompleted(jobId: string): void {
    if (this.actionInProgress()) {
      return;
    }

    this.actionInProgress.set(true);
    this.successMessage.set('');

    this.jobsService
      .markAsPending(jobId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: () => {
        this.actionInProgress.set(false);
        this.pendingCompletionJobId.set(null);
        this.successMessage.set(
          'Trabajo marcado como completado. Se notificó al cliente para su confirmación.',
        );
        this.loadJobs();
        this.refreshActiveList();
      },
      error: (error: HttpErrorResponse) => {
        this.actionInProgress.set(false);
        this.pendingCompletionJobId.set(null);
        const message =
          typeof error.error?.message === 'string'
            ? error.error.message
            : 'No se pudo marcar el trabajo como completado.';
        this.myJobsError.set(message);
      },
    });
  }

  confirmCompletion(jobId: string): void {
    if (this.actionInProgress()) {
      return;
    }

    this.actionInProgress.set(true);
    this.successMessage.set('');

    this.jobsService
      .confirmCompletion(jobId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: () => {
        this.actionInProgress.set(false);
        this.confirmCompletionJobId.set(null);
        this.successMessage.set(
          'Trabajo confirmado como completado. El pago fue liberado al profesional.',
        );
        this.loadJobs();
      },
      error: (error: HttpErrorResponse) => {
        this.actionInProgress.set(false);
        this.confirmCompletionJobId.set(null);
        const message =
          typeof error.error?.message === 'string'
            ? error.error.message
            : 'No se pudo confirmar la finalización.';
        this.offersError.set(message);
      },
    });
  }

  closePendingCompletionModal(): void {
    if (!this.actionInProgress()) {
      this.pendingCompletionJobId.set(null);
    }
  }

  closeConfirmCompletionModal(): void {
    if (!this.actionInProgress()) {
      this.confirmCompletionJobId.set(null);
    }
  }

  private refreshActiveList(): void {
    if (this.isProfessional()) {
      if (this.activeTab() === 'myJobs') {
        this.loadMyJobs();
      }
    }
  }

  private loadOffers(jobId: string): void {
    this.offersLoading.set(true);
    this.offersError.set('');

    this.offersService
      .listOffersByJob(jobId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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

    this.offersService
      .createOffer(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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

    this.jobsService
      .listJobs({ page: this.page(), size: this.pageSize })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
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

  private loadMyJobs(): void {
    this.myJobsLoading.set(true);
    this.myJobsError.set('');

    this.jobsService
      .listMyJobs({ page: this.myJobsPage(), size: this.pageSize })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result: Page<JobSummaryResponse>) => {
          this.myJobs.set(result.content);
          this.myJobsTotalPages.set(result.totalPages);
          this.myJobsTotalElements.set(result.totalElements);
          this.myJobsLoading.set(false);
        },
        error: () => {
          this.myJobsError.set('No se pudieron cargar tus trabajos. Inténtalo de nuevo.');
          this.myJobsLoading.set(false);
        },
      });
  }
}
