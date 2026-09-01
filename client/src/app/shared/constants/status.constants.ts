import { JobStatus } from '@shared/models/job.model';
import { OfferStatus } from '@shared/models/offer.model';
import { TransactionStatus } from '@shared/models/transaction.model';

export const JOB_STATUS_LABELS: Record<JobStatus, string> = {
  PUBLISHED: 'Publicado',
  IN_PROGRESS: 'En progreso',
  PENDING_CONFIRMATION: 'Pendiente de confirmación',
  COMPLETED: 'Completado',
  CANCELED: 'Cancelado',
  HIDDEN: 'Oculto',
};

export const OFFER_STATUS_LABELS: Record<OfferStatus, string> = {
  PENDING: 'Pendiente',
  ACCEPTED: 'Aceptada',
  REJECTED: 'Rechazada',
  WITHDRAWN: 'Retirada',
};

export const TRANSACTION_STATUS_LABELS: Record<TransactionStatus, string> = {
  PENDING: 'Pendiente',
  CAPTURED: 'Capturado',
  RELEASED: 'Liberado',
  REFUNDED: 'Reembolsado',
  FAILED: 'Fallido',
};
