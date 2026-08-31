import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TransactionsService } from '@core/services/transactions.service';
import { Page } from '@shared/models/pagination.model';
import { TransactionResponse, TransactionStatus } from '@shared/models/transaction.model';
import { TRANSACTION_STATUS_LABELS } from '@shared/constants/status.constants';
import { CurrencyEsPipe } from '@shared/pipes/currency-es.pipe';
import { DateEsPipe } from '@shared/pipes/date-es.pipe';
import { PaginationComponent } from '@shared/components/pagination/pagination';
import { AlertComponent } from '@shared/components/alert/alert';

@Component({
  selector: 'app-transactions-list',
  imports: [CurrencyEsPipe, DateEsPipe, PaginationComponent, AlertComponent],
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

  onPageChange(page: number): void {
    this.page.set(page);
    this.loadTransactions();
  }

  statusLabel(status: TransactionStatus): string | TransactionStatus {
    return TRANSACTION_STATUS_LABELS[status] ?? status;
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
