package com.bootcamp48.java.product.mapper;

import com.bootcamp48.java.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toModel(com.bootcamp48.java.product.domain.Product product);
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toModelWithoutDate(com.bootcamp48.java.product.domain.Product product);
    com.bootcamp48.java.product.domain.Product toDocument(Product product);
}