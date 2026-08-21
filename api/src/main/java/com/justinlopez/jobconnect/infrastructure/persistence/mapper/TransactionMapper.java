package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.Transaction;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface TransactionMapper {


    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "amount.currency")
    @Mapping(target = "clientId", source = "clientId.value")
    @Mapping(target = "professionalId", source = "professionalId.value")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TransactionEntity toEntity(Transaction transaction);


    @Mapping(target = "amount", source = ".", qualifiedByName = "toMoney")
    @Mapping(target = "clientId", source = "clientId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "professionalId", source = "professionalId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toDomain(TransactionEntity transactionEntity);


    @Named("toMoney")
    default Money toMoney(TransactionEntity entity) {
        if (entity == null) return null;
        return new Money(entity.getAmount(), entity.getCurrency());
    }

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID id) {
        if (id == null) return null;
        return new UserId(id);
    }
}
