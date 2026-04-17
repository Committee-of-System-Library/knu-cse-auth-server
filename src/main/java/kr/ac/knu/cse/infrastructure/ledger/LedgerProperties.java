package kr.ac.knu.cse.infrastructure.ledger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ledger.internal")
public record LedgerProperties(
        String baseUrl,
        String headerName,
        String apiKey
) {
}
