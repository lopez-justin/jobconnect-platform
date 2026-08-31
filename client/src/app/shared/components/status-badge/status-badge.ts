import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export type StatusTone = 'green' | 'blue' | 'amber' | 'red' | 'slate';

const TONE_CLASSES: Record<StatusTone, string> = {
  green: 'bg-green-50 text-green-700',
  blue: 'bg-blue-50 text-blue-700',
  amber: 'bg-amber-50 text-amber-700',
  red: 'bg-red-50 text-red-700',
  slate: 'bg-slate-100 text-slate-600',
};

@Component({
  selector: 'app-status-badge',
  standalone: true,
  templateUrl: './status-badge.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatusBadgeComponent {
  readonly label = input.required<string>();
  readonly tone = input<StatusTone>('slate');

  readonly toneClass = (): string => TONE_CLASSES[this.tone()];
}
