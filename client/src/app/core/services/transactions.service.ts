import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TransactionResponse } from '../../shared/models/transaction.model';
import { Page } from '../../shared/models/pagination.model';

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
    let httpParams = new HttpParams();

    if (params.page != null) {
      httpParams = httpParams.set('page', params.page.toString());
    }
    if (params.size != null) {
      httpParams = httpParams.set('size', params.size.toString());
    }

    return this.http.get<Page<TransactionResponse>>(this.transactionsApiUrl, {
      params: httpParams,
    });
  }
}
