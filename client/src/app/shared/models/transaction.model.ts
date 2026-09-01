export type TransactionStatus = 'PENDING' | 'CAPTURED' | 'RELEASED' | 'REFUNDED' | 'FAILED';

export interface TransactionResponse {
  id: string;
  jobId: string;
  jobTitle: string;
  amount: number;
  currency: string;
  status: TransactionStatus;
  stripePaymentIntentId: string | null;
  createdAt: string;
  updatedAt: string;
}
