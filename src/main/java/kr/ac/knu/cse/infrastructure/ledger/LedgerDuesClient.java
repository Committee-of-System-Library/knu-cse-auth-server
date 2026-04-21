package kr.ac.knu.cse.infrastructure.ledger;

import kr.ac.knu.cse.global.exception.snack.LedgerLookupFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class LedgerDuesClient {

    private final RestClient restClient;
    private final LedgerProperties properties;

    public LedgerDuesClient(LedgerProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public boolean isPaid(String studentNumber, String semester) {
        try {
            DuesStatusResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/ledger/internal/dues/{studentNumber}")
                            .queryParam("semester", semester)
                            .build(studentNumber))
                    .headers(this::addInternalApiKey)
                    .retrieve()
                    .body(DuesStatusResponse.class);

            return response != null && response.paid();
        } catch (RestClientException e) {
            log.error("ledger dues lookup failed for studentNumber={}, semester={}", studentNumber, semester, e);
            throw new LedgerLookupFailedException();
        }
    }

    private void addInternalApiKey(HttpHeaders headers) {
        if (properties.apiKey() != null && !properties.apiKey().isBlank()
                && properties.headerName() != null && !properties.headerName().isBlank()) {
            headers.add(properties.headerName(), properties.apiKey());
        }
    }

    public record DuesStatusResponse(String studentNumber, String semester, boolean paid) {
    }
}
