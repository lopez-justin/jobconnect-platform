package com.justinlopez.jobconnect.infrastructure.persistence.mapper;

import com.justinlopez.jobconnect.domain.model.Category;
import com.justinlopez.jobconnect.infrastructure.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    Category toDomain(CategoryEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    CategoryEntity toEntity(Category category);

}
