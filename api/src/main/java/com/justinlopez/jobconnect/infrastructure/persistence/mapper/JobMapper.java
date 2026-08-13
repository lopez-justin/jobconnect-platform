package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.Job;
import com.justinlopez.jobconnect.domain.model.vo.Address;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CategoryMapper.class, UserMapper.class}
)
public interface JobMapper {


    @Mapping(target = "budgetAmount", source = "budget.amount")
    @Mapping(target = "budgetCurrency", source = "budget.currency")
    @Mapping(target = "street", source = "location.street")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    @Mapping(target = "client", source = "clientId", qualifiedByName = "userIdToUserEntity")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "selectedProfessionalId", source = "selectedProfessionalId.value")
    @Mapping(target = "selectedOfferId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JobEntity toEntity(Job job);



    @Mapping(target = "budget", source = ".", qualifiedByName = "toMoney")
    @Mapping(target = "location", source = ".", qualifiedByName = "toAddress")
    @Mapping(target = "clientId", source = "client.id", qualifiedByName = "uuidToUserId")
    @Mapping(target = "selectedProfessionalId", source = "selectedProfessionalId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Job toDomain(JobEntity jobEntity);



    @Named("toMoney")
    default Money toMoney(JobEntity jobEntity) {
        if (jobEntity == null) return null;
        return new Money(jobEntity.getBudgetAmount(), jobEntity.getBudgetCurrency());
    }

    @Named("toAddress")
    default Address toAddress(JobEntity jobEntity) {
        if (jobEntity == null) return null;
        return new Address(
                jobEntity.getStreet(),
                jobEntity.getCity(),
                jobEntity.getLatitude(),
                jobEntity.getLongitude()
        );
    }

    @Named("userIdToUserEntity")
    default UserEntity userIdToUserEntity(UserId userId) {
        if (userId == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(userId.value());
        return entity;
    }

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID id) {
        if (id == null) return null;
        return new UserId(id);
    }

}
