package com.justinlopez.jobconnect.domain.model.enums;

public enum JobStatus {
    PUBLISHED,
    IN_PROGRESS,
    PENDING_CONFIRMATION,
    COMPLETED,
    CANCELED,
    HIDDEN;

    public boolean canTransitionTo(JobStatus target) {
        return switch (this) {
            case PUBLISHED -> target == IN_PROGRESS || target == CANCELED || target == HIDDEN;
            case IN_PROGRESS -> target == PENDING_CONFIRMATION || target == CANCELED;
            case PENDING_CONFIRMATION -> target == COMPLETED || target == CANCELED;
            case COMPLETED, CANCELED, HIDDEN -> false;
        };
    }
}
