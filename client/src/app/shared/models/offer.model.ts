export interface CreateOfferRequest {
  jobId: string;
  offeredPrice: number;
  message?: string;
}

export type OfferStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN';

export interface OfferResponse {
  id: string;
  jobId: string;
  professionalId: string;
  professionalFullName?: string;
  offeredPrice: number;
  currency: string;
  message: string;
  status: OfferStatus;
  createdAt: string;
}