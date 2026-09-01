import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export type AlertType = 'success' | 'error';

@Component({
  selector: 'app-alert',
  standalone: true,
  templateUrl: './alert.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlertComponent {
  readonly type = input<AlertType>('success');
  readonly message = input.required<string>();
}
