package com.truckplanner.controller;

import com.truckplanner.dto.OptimizationRequest;
import com.truckplanner.dto.OptimizationResponse;
import com.truckplanner.service.OptimizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class OptimizationController {

    private final OptimizationService optimizationService;

    public OptimizationController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResponse> optimize(@Valid @RequestBody OptimizationRequest request) {
        if (request.getOrders() != null && request.getOrders().size() > 22) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        OptimizationResponse response = optimizationService.optimize(request);
        return ResponseEntity.ok(response);
    }
}
