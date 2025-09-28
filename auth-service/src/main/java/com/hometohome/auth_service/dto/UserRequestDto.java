package com.hometohome.auth_service.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    private UUID credentialId;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    // @NotBlank(message = "La contraseña es obligatoria")
    // @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    // @Pattern(
    //     regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
    //     message = "La contraseña debe ser alfanumérica y contener al menos un carácter especial"
    // )
    // private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;
    
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;
    
    private String image;
}