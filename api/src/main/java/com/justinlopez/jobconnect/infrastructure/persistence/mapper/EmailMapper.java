package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.vo.Email;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmailMapper {

    default Email toDomain(String email) {
        return email == null
                ? null
                : new Email(email);
    }

    default String toEntity(Email email) {
        return email == null
                ? null
                : email.value();
    }

}
