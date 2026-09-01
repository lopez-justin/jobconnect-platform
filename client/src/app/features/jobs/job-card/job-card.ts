import { Component, computed, input, output } from '@angular/core';
import { JobSummaryResponse } from '@shared/models/job.model';
import { CurrencyEsPipe } from '@shared/pipes/currency-es.pipe';
import { JOB_STATUS_LABELS } from '@shared/constants/status.constants';
import { StatusBadgeComponent, StatusTone } from '@shared/components/status-badge/status-badge';

const JOB_STATUS_TONES: Record<string, StatusTone> = {
  PUBLISHED: 'green',
  IN_PROGRESS: 'blue',
  PENDING_CONFIRMATION: 'amber',
  COMPLETED: 'slate',
};

@Component({
  selector: 'app-job-card',
  standalone: true,
  imports: [CurrencyEsPipe, StatusBadgeComponent],
  templateUrl: './job-card.html',
})
export class JobCardComponent {
  readonly job = input.required<JobSummaryResponse>();
  readonly isClient = input(false);
  readonly isProfessional = input(false);
  readonly showOffers = input(false);
  readonly offerActive = input(false);

  readonly viewOffers = output<void>();
  readonly confirmCompletion = output<void>();
  readonly offerToggle = output<void>();
  readonly markCompleted = output<void>();

  readonly statusLabel = computed(
    () => JOB_STATUS_LABELS[this.job().status] ?? this.job().status,
  );
  readonly statusTone = computed(() => JOB_STATUS_TONES[this.job().status] ?? 'slate');
}
