package com.bootcamp48.java.movement.mapper;

import com.bootcamp48.java.movement.domain.Movement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovementMapper {
    com.bootcamp48.java.movement.model.Movement toModel(Movement transaction);
    Movement toDocument(com.bootcamp48.java.movement.model.Movement transaction);
}
