package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.Offer;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.JobEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.OfferEntity;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface OfferMapper {


    @Mapping(target = "job", source = "jobId", qualifiedByName = "jobIdToJobEntity")
    @Mapping(target = "professional", source = "professionalId", qualifiedByName = "userIdToUserEntity")
    @Mapping(target = "offeredPriceAmount", source = "offeredPrice.amount")
    @Mapping(target = "offeredPriceCurrency", source = "offeredPrice.currency")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OfferEntity toEntity(Offer offer);



    //@Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "professionalId", source = "professional.id", qualifiedByName = "uuidToUserId")
    @Mapping(target = "offeredPrice", source = ".", qualifiedByName = "toMoneyFromOffer")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Offer toDomain(OfferEntity offerEntity);



    @Named("jobIdToJobEntity")
    default JobEntity jobIdToJobEntity(UUID jobId) {
        if (jobId == null) return null;
        JobEntity jobEntity = new JobEntity();
        jobEntity.setId(jobId);
        return jobEntity;
    }

    @Named("userIdToUserEntity")
    default UserEntity userIdToUserEntity(UserId userId) {
        if (userId == null) return null;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId.value());
        return userEntity;
    }

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID uuid) {
        if (uuid == null) return null;
        return new UserId(uuid);
    }

    @Named("toMoneyFromOffer")
    default Money toMoneyFromOffer(OfferEntity entity) {
        if (entity == null) return null;
        return new Money(entity.getOfferedPriceAmount(), entity.getOfferedPriceCurrency());
    }

}
