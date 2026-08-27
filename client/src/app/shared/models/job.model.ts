export interface JobResponse {
  id: string;
  title: string;
  description: string;
  categoryName: string;
  budgetAmount: number;
  budgetCurrency: string;
  street: string;
  city: string;
  latitude: number;
  longitude: number;
  clientId: string;
  selectedProfessionalId: string | null;
  status: JobStatus;
  createdAt: string;
}

export interface JobSummaryResponse {
  id: string;
  title: string;
  categoryName: string;
  budgetAmount: number;
  budgetCurrency: string;
  city: string;
  status: JobStatus;
  offersCount: number;
  createdAt: string;
}

export interface CreateJobRequest {
  title: string;
  description: string;
  categoryId: string;
  budgetAmount: number;
  budgetCurrency?: string;
  street: string;
  city: string;
  latitude?: number;
  longitude?: number;
}

export interface JobListParams {
  status?: JobStatus;
  categoryId?: string;
  city?: string;
  minBudget?: number;
  maxBudget?: number;
  page?: number;
  size?: number;
}

export type JobStatus =
  'PUBLISHED' | 'IN_PROGRESS' | 'PENDING_CONFIRMATION' | 'COMPLETED' | 'CANCELED' | 'HIDDEN';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
  numberOfElements: number;
}
