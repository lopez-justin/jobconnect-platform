import { Component, computed, input, output } from '@angular/core';
import { OfferResponse } from '@shared/models/offer.model';
import { CurrencyEsPipe } from '@shared/pipes/currency-es.pipe';
import { DateEsPipe } from '@shared/pipes/date-es.pipe';
import { OFFER_STATUS_LABELS } from '@shared/constants/status.constants';

@Component({
  selector: 'app-offer-list',
  standalone: true,
  imports: [CurrencyEsPipe, DateEsPipe],
  templateUrl: './offer-list.html',
})
export class OfferListComponent {
  readonly offers = input.required<OfferResponse[]>();
  readonly loading = input(false);
  readonly error = input<string>('');
  readonly jobStatus = input<string>('');
  readonly acceptingOfferId = input<string | null>(null);

  readonly accept = output<{ jobId: string; offerId: string }>();

  readonly showAccept = computed(
    () => this.jobStatus() === 'PUBLISHED' && this.offers().some((o) => o.status === 'PENDING'),
  );

  offerStatusLabel(status: string): string {
    return OFFER_STATUS_LABELS[status as keyof typeof OFFER_STATUS_LABELS] ?? status;
  }
}
