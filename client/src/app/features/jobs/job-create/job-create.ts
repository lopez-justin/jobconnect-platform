import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CategoriesService } from '../../../core/services/categories.service';
import { JobsService } from '../../../core/services/jobs.service';
import { CategoryResponse } from '../../../shared/models/category.model';

@Component({
  selector: 'app-job-create',
  imports: [ReactiveFormsModule],
  templateUrl: './job-create.html',
  standalone: true,
})
export class JobCreateComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly jobsService = inject(JobsService);
  private readonly categoriesService = inject(CategoriesService);
  protected readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal('');
  readonly categories = signal<CategoryResponse[]>([]);
  readonly loadingCategories = signal(true);

  readonly jobForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    description: ['', [Validators.required, Validators.minLength(10)]],
    categoryId: ['', [Validators.required]],
    budgetAmount: [0, [Validators.required, Validators.min(1)]],
    street: ['', [Validators.required]],
    city: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.categoriesService
      .getCategories()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: (categories) => {
        this.categories.set(categories);
        this.loadingCategories.set(false);
      },
      error: () => {
        this.errorMessage.set('No se pudieron cargar las categorías.');
        this.loadingCategories.set(false);
      },
    });
  }

  onSubmit(): void {
    if (this.jobForm.invalid || this.isSubmitting()) {
      this.jobForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set('');

    this.jobsService
      .createJob(this.jobForm.getRawValue())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: () => {
        this.isSubmitting.set(false);
        void this.router.navigateByUrl('/jobs');
      },
      error: (error: HttpErrorResponse) => {
        const message =
          typeof error.error?.message === 'string'
            ? error.error.message
            : 'No se pudo crear el trabajo. Inténtalo de nuevo.';
        this.errorMessage.set(message);
        this.isSubmitting.set(false);
      },
    });
  }
}
