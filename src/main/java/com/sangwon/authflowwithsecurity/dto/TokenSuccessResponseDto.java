package com.sangwon.authflowwithsecurity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@Jacksonized
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Schema(name = "TokenSuccessResponse", accessMode = Schema.AccessMode.READ_ONLY)
public class TokenSuccessResponseDto {
    private String accessToken;
    private String refreshToken;

}
