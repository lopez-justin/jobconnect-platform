package com.justinlopez.jobconnect.domain.service;

import com.justinlopez.jobconnect.domain.model.vo.UserId;

public interface JobValidationService {

    // Rule: A client cannot have more than X active jobs simultaneously
    boolean canClientCreateJob(UserId clientId);

}
