package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.Role;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {

    Role toDomain(RoleEntity entity);

    RoleEntity toEntity(Role role);

}
