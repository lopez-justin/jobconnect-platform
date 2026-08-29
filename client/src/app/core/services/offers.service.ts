import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateOfferRequest, OfferResponse } from '../../shared/models/offer.model';

@Injectable({
  providedIn: 'root',
})
export class OffersService {
  private readonly http = inject(HttpClient);
  private readonly offersApiUrl = `${environment.apiUrl}/offers`;

  createOffer(payload: CreateOfferRequest): Observable<OfferResponse> {
    return this.http.post<OfferResponse>(this.offersApiUrl, payload);
  }
}