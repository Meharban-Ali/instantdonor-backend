package com.blood_donor.ai_service.config;

import com.blood_donor.exception.ApiException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class AiServiceErrorHandler
implements ResponseErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(AiServiceErrorHandler.class);

    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        int statusCode = response.getStatusCode().value();
        String statusText = response.getStatusText();
        log.error("AI service HTTP error: status={}, text={}", (Object)statusCode, (Object)statusText);
        if (response.getStatusCode().is4xxClientError()) {
            throw ApiException.badRequest("AI service rejected request: " + statusText);
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw ApiException.internalServerError("AI service unavailable (HTTP " + statusCode + "): " + statusText);
        }
    }
}

