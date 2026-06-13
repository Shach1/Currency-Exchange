package ru.trukhmanov.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.trukhmanov.dto.response.ExchangeRateDto;
import ru.trukhmanov.entity.ExchangeRate;

@Mapper
public interface ExchangeRateMapper{

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "baseCurrency.fullName", target = "baseCurrency.name")
    @Mapping(source = "targetCurrency.fullName", target = "targetCurrency.name")
    ExchangeRateDto toExchangeRateDto(ExchangeRate exchangeRate);
}
