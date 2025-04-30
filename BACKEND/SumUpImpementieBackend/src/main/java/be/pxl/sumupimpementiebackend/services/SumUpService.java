// src/main/java/com/example/sumup/service/SumUpService.java
package be.pxl.sumupimpementiebackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SumUpService {
    @Value("${sumup.client-id}")     private String clientId;
    @Value("${sumup.client-secret}") private String clientSecret;
    @Value("${sumup.merchant-code}") private String merchantCode;

    private final RestTemplate rest = new RestTemplate();
    private final AtomicReference<String> token = new AtomicReference<>();
    private long tokenExpiry = 0L;

    private String getAccessToken() {
        if (token.get() != null && System.currentTimeMillis() < tokenExpiry) {
            return token.get();
        }

        String url = "https://api.sumup.com/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope",      "payments");
        body.add("client_id",  clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String,String>> req = new HttpEntity<>(body, headers);
        ResponseEntity<Map> resp = rest.postForEntity(url, req, Map.class);
        Map<String,Object> data = resp.getBody();

        String at = (String) data.get("access_token");
        Integer exp = (Integer) data.get("expires_in");
        token.set(at);
        tokenExpiry = System.currentTimeMillis() + (exp - 30)*1000L;
        return at;
    }

    // src/main/java/be/pxl/sumupimpementiebackend/services/SumUpService.java

    public Map<String,Object> createCheckout(String amount) {
        // 1) Haal (en cache) je OAuth token
        String accessToken = getAccessToken();

        // 2) SumUp create-checkout endpoint
        String url = "https://api.sumup.com/v0.1/checkouts";

        // 3) Zet de headers (Bearer & JSON)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4) Bouw de payload
        Map<String,Object> payload = new HashMap<>();
        payload.put("checkout_reference", "order-" + System.currentTimeMillis());
        payload.put("amount",             amount);                   // bv. "10.00"
        payload.put("currency",           "EUR");
        payload.put("merchant_code",      merchantCode);
        payload.put("description",        "Betaling via Spring Boot");
        // Return-URL (SumUp post-payment status callback, én klant-redirect)
        payload.put("return_url",         "http://localhost:5173/status");

        // 5) Schakel Hosted Checkout in zodat je een hosted_checkout_url terugkrijgt
        Map<String,Object> hosted = new HashMap<>();
        hosted.put("enabled", true);
        payload.put("hosted_checkout", hosted);

        // 6) Wrap payload + headers, en stuur ‘m
        HttpEntity<Map<String,Object>> reqEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> resp = rest.exchange(url, HttpMethod.POST, reqEntity, Map.class);

        // 7) Return de volledige JSON body als Map
        return resp.getBody();
    }


    public Map<String,Object> getCheckout(String checkoutId) {
        String accessToken = getAccessToken();
        String url = "https://api.sumup.com/v0.1/checkouts/" + checkoutId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<Map> resp = rest.exchange(url, HttpMethod.GET, req, Map.class);
        return resp.getBody();
    }
}
