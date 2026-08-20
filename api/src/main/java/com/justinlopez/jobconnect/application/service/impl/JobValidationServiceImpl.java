package com.justinlopez.jobconnect.application.service.impl;

import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.domain.service.JobValidationService;
import org.springframework.stereotype.Service;

@Service
public class JobValidationServiceImpl implements JobValidationService {

    @Override
    public boolean canClientCreateJob(UserId clientId) {
        // TODO: Implement the logic to check if the client can create a job
        // For example, check if the client has sufficient credits or is not banned
        return true;
    }

}
