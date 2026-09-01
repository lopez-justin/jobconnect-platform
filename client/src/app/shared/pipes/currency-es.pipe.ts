import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyEs',
  standalone: true,
})
export class CurrencyEsPipe implements PipeTransform {
  transform(amount: number | null | undefined, currency?: string | null): string {
    return new Intl.NumberFormat('es-EC', {
      style: 'currency',
      currency: currency || 'USD',
    }).format(amount ?? 0);
  }
}
