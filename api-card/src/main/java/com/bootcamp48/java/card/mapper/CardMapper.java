package com.bootcamp48.java.card.mapper;

import com.bootcamp48.java.card.model.Card;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toModel(com.bootcamp48.java.card.domain.Card card);
    com.bootcamp48.java.card.domain.Card toDocument(Card card);
    Card copy(Card card);
}
