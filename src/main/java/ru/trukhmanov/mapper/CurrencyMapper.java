package ru.trukhmanov.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.trukhmanov.dto.response.CurrencyDto;
import ru.trukhmanov.entity.Currency;

@Mapper
public interface CurrencyMapper{

    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(target = "name", source = "fullName")
    CurrencyDto toCurrencyDto(Currency currency);
}
