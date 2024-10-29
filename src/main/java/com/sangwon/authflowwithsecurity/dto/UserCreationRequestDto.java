package com.sangwon.authflowwithsecurity.dto;

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
@Schema(title = "UserCreationRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class UserCreationRequestDto {

    @NotBlank(message = "first name must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "first name of user", example = "teddy")
    private String firstName;

    @NotBlank(message = "last name must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "last name of user", example = "kwak")
    private String lastName;

    @NotBlank(message = "email-id must not be empty")
    @Email(message = "email-id must be of valid format")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "first name of user", example = "teddy")
    private String emailId;

    @NotBlank(message = "password must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "secure password to enable user login", example = "Password@123")
    private String password;
}
