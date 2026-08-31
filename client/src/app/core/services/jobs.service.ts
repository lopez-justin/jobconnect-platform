import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateJobRequest,
  JobListParams,
  JobResponse,
  JobSummaryResponse,
} from '../../shared/models/job.model';
import { Page } from '../../shared/models/pagination.model';

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
    let httpParams = new HttpParams();

    if (params.status) {
      httpParams = httpParams.set('status', params.status);
    }
    if (params.categoryId) {
      httpParams = httpParams.set('categoryId', params.categoryId);
    }
    if (params.city) {
      httpParams = httpParams.set('city', params.city);
    }
    if (params.minBudget != null) {
      httpParams = httpParams.set('minBudget', params.minBudget.toString());
    }
    if (params.maxBudget != null) {
      httpParams = httpParams.set('maxBudget', params.maxBudget.toString());
    }
    if (params.page != null) {
      httpParams = httpParams.set('page', params.page.toString());
    }
    if (params.size != null) {
      httpParams = httpParams.set('size', params.size.toString());
    }

    return this.http.get<Page<JobSummaryResponse>>(this.jobsApiUrl, { params: httpParams });
  }

  listMyJobs(params: JobListParams = {}): Observable<Page<JobSummaryResponse>> {
    let httpParams = new HttpParams();

    if (params.page != null) {
      httpParams = httpParams.set('page', params.page.toString());
    }
    if (params.size != null) {
      httpParams = httpParams.set('size', params.size.toString());
    }

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
