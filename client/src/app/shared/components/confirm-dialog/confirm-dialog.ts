import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

export type ConfirmTone = 'slate' | 'danger';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  templateUrl: './confirm-dialog.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmDialogComponent {
  readonly title = input.required<string>();
  readonly message = input.required<string>();
  readonly confirmLabel = input.required<string>();
  readonly cancelLabel = input<string>('Cancelar');
  readonly processingLabel = input<string>('Procesando...');
  readonly processing = input(false);
  readonly tone = input<ConfirmTone>('slate');

  readonly confirm = output<void>();
  readonly cancel = output<void>();

  readonly confirmButtonClass = (): string =>
    this.tone() === 'danger'
      ? 'rounded-lg bg-red-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-red-500 disabled:cursor-not-allowed disabled:bg-red-400'
      : 'rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-500';
}
