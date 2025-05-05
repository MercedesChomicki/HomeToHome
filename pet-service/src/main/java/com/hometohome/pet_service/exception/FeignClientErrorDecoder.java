package com.hometohome.pet_service.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new UserNotFoundException("El ID de usuario no es válido");
            case 503 -> new ServiceUnavailableException("Servicio externo no disponible");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
