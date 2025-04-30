// src/main/java/com/example/sumup/controller/SumUpController.java
package be.pxl.sumupimpementiebackend.api.Controllers;
import be.pxl.sumupimpementiebackend.api.Requests.CreateCheckoutRequest;
import be.pxl.sumupimpementiebackend.services.SumUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@CrossOrigin  // in prod: beperk tot je frontend-domein
public class SumUpController {
    private final SumUpService sumUpService;

    public SumUpController(SumUpService sumUpService) {
        this.sumUpService = sumUpService;
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<?> create(@RequestBody CreateCheckoutRequest req) {
        Map<String,Object> sumupResp = sumUpService.createCheckout(req.getAmount());
        String checkoutId  = sumupResp.get("id").toString();
        String checkoutUrl = sumupResp.get("hosted_checkout_url").toString();

        return ResponseEntity.ok(Map.of(
                "checkoutId",  checkoutId,
                "checkoutUrl", checkoutUrl
        ));
    }


    @GetMapping("/checkout/{checkoutId}")
    public ResponseEntity<Map<String,Object>> status(@PathVariable String checkoutId) {
        Map<String,Object> checkout = sumUpService.getCheckout(checkoutId);
        // retourneer alleen relevante velden
        return ResponseEntity.ok(Map.of(
                "status",   checkout.get("status"),
                "amount",   checkout.get("amount"),
                "currency", checkout.get("currency")
        ));
    }
}
