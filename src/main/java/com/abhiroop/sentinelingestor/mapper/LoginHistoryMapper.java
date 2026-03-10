package com.abhiroop.sentinelingestor.mapper;

import com.abhiroop.sentinelingestor.dto.LoginHistoryDto;
import com.abhiroop.sentinelingestor.entity.LoginHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginHistoryMapper {

    @Mapping(target = "timestamp", expression = "java(com.google.cloud.Timestamp.ofTimeMicroseconds(loginHistoryDto.timestamp() * 1000))")
    LoginHistoryEntity toEntity(LoginHistoryDto loginHistoryDto);
}
