import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateEs',
  standalone: true,
})
export class DateEsPipe implements PipeTransform {
  transform(iso: string | null | undefined): string {
    if (!iso) {
      return '';
    }
    return new Date(iso).toLocaleDateString('es-EC');
  }
}
