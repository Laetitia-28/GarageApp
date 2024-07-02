package io.tutoriel.spring.garageApp.controllers;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class changePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;

}
