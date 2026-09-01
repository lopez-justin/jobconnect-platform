import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { TransactionResponse } from '@shared/models/transaction.model';
import { Page } from '@shared/models/pagination.model';
import { buildHttpParams } from '@shared/utils/http.util';

interface TransactionListParams {
  page?: number;
  size?: number;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionsService {
  private readonly http = inject(HttpClient);
  private readonly transactionsApiUrl = `${environment.apiUrl}/transactions`;

  listTransactions(params: TransactionListParams = {}): Observable<Page<TransactionResponse>> {
    const httpParams = buildHttpParams({
      page: params.page,
      size: params.size,
    });

    return this.http.get<Page<TransactionResponse>>(this.transactionsApiUrl, {
      params: httpParams,
    });
  }
}
