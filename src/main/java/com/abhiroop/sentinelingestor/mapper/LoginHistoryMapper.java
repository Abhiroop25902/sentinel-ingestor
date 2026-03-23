package com.abhiroop.sentinelingestor.mapper;

import com.abhiroop.sentinelingestor.dto.LoginHistoryDto;
import com.abhiroop.sentinelingestor.entity.LoginHistoryEntity;
import com.google.cloud.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {Timestamp.class})
public interface LoginHistoryMapper {

    @Mapping(target = "timestamp", expression = "java(Timestamp.ofTimeMicroseconds(loginHistoryDto.timestamp() * 1000))")
    @Mapping(target = "expireAt", expression = "java(calculateExpireAt(loginHistoryDto.timestamp()))")
    LoginHistoryEntity toEntity(LoginHistoryDto loginHistoryDto);

    default Timestamp calculateExpireAt(long timestampMs) {
        // 24 hours in milliseconds = 86,400,000
        long oneDayInMs = 24L * 60 * 60 * 1000;
        long expiryMs = timestampMs + oneDayInMs;

        return Timestamp.ofTimeMicroseconds(expiryMs * 1000);
    }
}
