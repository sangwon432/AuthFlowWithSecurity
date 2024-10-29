package com.sangwon.authflowwithsecurity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@Schema(title = "UserLoginRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class UserLoginRequestDto {
    @NotBlank(message = "email-id must not be empty")
    @Email(message="email-id must be of valid format")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "email-id of user", example = "abc@abc.com")
    private String emailId;

    @NotBlank(message = "password must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "secure password to enable user login", example="Password@123")
    private String password;
}
