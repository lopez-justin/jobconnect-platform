package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.User;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                RoleMapper.class,
                EmailMapper.class
        }
)
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User user);

    /*@Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoleEntitiesToDomain")*/
    @InheritInverseConfiguration
    User toDomain(UserEntity userEntity);

}
