package be.intec.linguacards.health.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/secure")
    public Map<String, String> secureHealth() {
        return Map.of("status", "ok");
    }
}
