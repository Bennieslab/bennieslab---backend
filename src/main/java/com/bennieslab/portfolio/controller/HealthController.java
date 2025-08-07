package com.bennieslab.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    @RequestMapping(value = "/health", method = {RequestMethod.GET, RequestMethod.HEAD})
    public Map<String, String> healthCheck() {
        return Map.of("status", "ok");
    }
}