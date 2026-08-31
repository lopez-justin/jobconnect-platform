import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TransactionsService } from '../../../core/services/transactions.service';
import { Page } from '../../../shared/models/pagination.model';
import { TransactionResponse, TransactionStatus } from '../../../shared/models/transaction.model';

const STATUS_LABELS: Record<TransactionStatus, string> = {
  PENDING: 'Pendiente',
  CAPTURED: 'Capturado',
  RELEASED: 'Liberado',
  REFUNDED: 'Reembolsado',
  FAILED: 'Fallido',
};

@Component({
  selector: 'app-transactions-list',
  templateUrl: './transactions-list.html',
  standalone: true,
})
export class TransactionsListComponent implements OnInit {
  private readonly transactionsService = inject(TransactionsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly transactions = signal<TransactionResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly page = signal(0);
  readonly pageSize = 10;
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  ngOnInit(): void {
    this.loadTransactions();
  }

  previousPage(): void {
    if (this.page() > 0) {
      this.page.update((p) => p - 1);
      this.loadTransactions();
    }
  }

  nextPage(): void {
    if (this.page() < this.totalPages() - 1) {
      this.page.update((p) => p + 1);
      this.loadTransactions();
    }
  }

  statusLabel(status: TransactionStatus): string {
    return STATUS_LABELS[status] ?? status;
  }

  formatCurrency(amount: number, currency: string): string {
    return new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: currency || 'USD',
    }).format(amount);
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleDateString('es-AR');
  }

  private loadTransactions(): void {
    this.loading.set(true);
    this.error.set('');

    this.transactionsService
      .listTransactions({ page: this.page(), size: this.pageSize })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result: Page<TransactionResponse>) => {
          this.transactions.set(result.content);
          this.totalPages.set(result.totalPages);
          this.totalElements.set(result.totalElements);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('No se pudieron cargar las transacciones. Inténtalo de nuevo.');
          this.loading.set(false);
        },
      });
  }
}
