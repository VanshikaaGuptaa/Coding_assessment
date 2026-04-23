package com.truckplanner.config;

import com.truckplanner.dto.OptimizationRequest;
import com.truckplanner.dto.OrderDto;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component("optimizationKeyGenerator")
public class OptimizationKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length > 0 && params[0] instanceof OptimizationRequest) {
            OptimizationRequest req = (OptimizationRequest) params[0];

            // Generate a simple deterministic string representation logic for the cache key
            StringBuilder sb = new StringBuilder();

            if (req.getTruck() != null) {
                sb.append(req.getTruck().getId()).append("|")
                        .append(req.getTruck().getMax_weight_lbs()).append("|")
                        .append(req.getTruck().getMax_volume_cuft()).append("||");
            }

            if (req.getOrders() != null) {
                // Sort by ID to ensure order of orders doesn't alter the result
                String ordersStr = req.getOrders().stream()
                        .sorted(Comparator.comparing(OrderDto::getId, Comparator.nullsLast(String::compareTo)))
                        .map(o -> o.getId() + ":" + o.getPayout_cents() + ":" + o.getWeight_lbs() + ":"
                                + o.getVolume_cuft() + ":" + o.getIs_hazmat())
                        .collect(Collectors.joining(","));
                sb.append(ordersStr);
            }
            return sb.toString();
        }
        return "defaultKey";
    }
}
