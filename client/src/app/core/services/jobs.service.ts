import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import {
  CreateJobRequest,
  JobListParams,
  JobResponse,
  JobSummaryResponse,
} from '@shared/models/job.model';
import { Page } from '@shared/models/pagination.model';
import { buildHttpParams } from '@shared/utils/http.util';

@Injectable({
  providedIn: 'root',
})
export class JobsService {
  private readonly http = inject(HttpClient);
  private readonly jobsApiUrl = `${environment.apiUrl}/jobs`;

  createJob(payload: CreateJobRequest): Observable<JobResponse> {
    return this.http.post<JobResponse>(this.jobsApiUrl, payload);
  }

  listJobs(params: JobListParams = {}): Observable<Page<JobSummaryResponse>> {
    const httpParams = buildHttpParams({
      status: params.status,
      categoryId: params.categoryId,
      city: params.city,
      minBudget: params.minBudget,
      maxBudget: params.maxBudget,
      page: params.page,
      size: params.size,
    });

    return this.http.get<Page<JobSummaryResponse>>(this.jobsApiUrl, { params: httpParams });
  }

  listMyJobs(params: JobListParams = {}): Observable<Page<JobSummaryResponse>> {
    const httpParams = buildHttpParams({
      page: params.page,
      size: params.size,
    });

    return this.http.get<Page<JobSummaryResponse>>(`${this.jobsApiUrl}/mine`, {
      params: httpParams,
    });
  }

  acceptOffer(jobId: string, offerId: string): Observable<JobResponse> {
    return this.http.post<JobResponse>(`${this.jobsApiUrl}/${jobId}/offers/${offerId}/accept`, {});
  }

  markAsPending(jobId: string): Observable<JobResponse> {
    return this.http.post<JobResponse>(`${this.jobsApiUrl}/${jobId}/mark-pending`, {});
  }

  confirmCompletion(jobId: string): Observable<JobResponse> {
    return this.http.post<JobResponse>(`${this.jobsApiUrl}/${jobId}/confirm-completion`, {});
  }
}
