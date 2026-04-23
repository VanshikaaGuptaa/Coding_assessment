package com.truckplanner.service;

import com.truckplanner.dto.OptimizationRequest;
import com.truckplanner.dto.OptimizationResponse;
import com.truckplanner.dto.OrderDto;
import com.truckplanner.dto.TruckDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;

@Service
public class OptimizationService {

    @Cacheable(value = "optimizationResults", keyGenerator = "optimizationKeyGenerator")
    public OptimizationResponse optimize(OptimizationRequest request) {
        TruckDto truck = request.getTruck();
        List<OrderDto> allOrders = request.getOrders();

        if (allOrders == null || allOrders.isEmpty()) {
            return buildEmptyResponse(truck.getId());
        }

        List<OrderDto> validOrders = new ArrayList<>();
        for (OrderDto order : allOrders) {
            if (order.getWeight_lbs() <= truck.getMax_weight_lbs() &&
                    order.getVolume_cuft() <= truck.getMax_volume_cuft()) {
                validOrders.add(order);
            }
        }

        int n = validOrders.size();
        if (n == 0) {
            return buildEmptyResponse(truck.getId());
        }

        // DP and bitmask logic
        int[] compatMask = buildCompatibilityMatrix(validOrders);

        int bound = 1 << n;
        long[] weight = new long[bound];
        long[] volume = new long[bound];
        long[] payout = new long[bound];
        boolean[] valid = new boolean[bound];

        valid[0] = true;
        weight[0] = 0;
        volume[0] = 0;
        payout[0] = 0;

        long bestPayout = -1;
        List<Integer> bestMasks = new ArrayList<>();

        for (int mask = 1; mask < bound; mask++) {
            int lastBit = Integer.numberOfTrailingZeros(mask);
            int prevMask = mask ^ (1 << lastBit);

            if (!valid[prevMask]) {
                valid[mask] = false;
                continue;
            }

            if ((prevMask & ~compatMask[lastBit]) != 0) {
                valid[mask] = false;
                continue;
            }

            OrderDto newOrder = validOrders.get(lastBit);
            long w = weight[prevMask] + newOrder.getWeight_lbs();
            long v = volume[prevMask] + newOrder.getVolume_cuft();

            if (w > truck.getMax_weight_lbs() || v > truck.getMax_volume_cuft()) {
                valid[mask] = false;
                continue;
            }

            valid[mask] = true;
            weight[mask] = w;
            volume[mask] = v;
            long currentPayout = payout[prevMask] + newOrder.getPayout_cents();
            payout[mask] = currentPayout;

            if (currentPayout > bestPayout) {
                bestPayout = currentPayout;
                bestMasks.clear();
                bestMasks.add(mask);
            } else if (currentPayout == bestPayout) {
                bestMasks.add(mask);
            }
        }

        if (bestMasks.isEmpty() && bestPayout <= 0) {
            return buildEmptyResponse(truck.getId());
        }
        int optimalMask = bestMasks.get(0);
        return buildResponse(truck, validOrders, optimalMask, bestMasks, bestPayout, weight[optimalMask],
                volume[optimalMask]);
    }

    private int[] buildCompatibilityMatrix(List<OrderDto> orders) {
        int n = orders.size();
        int[] compat = new int[n];

        for (int i = 0; i < n; i++) {
            int mask = 0;
            OrderDto o1 = orders.get(i);
            for (int j = 0; j < n; j++) {
                OrderDto o2 = orders.get(j);
                if (isCompatible(o1, o2)) {
                    mask |= (1 << j);
                }
            }
            compat[i] = mask;
        }

        return compat;
    }

    private boolean isCompatible(OrderDto o1, OrderDto o2) {
        if (!o1.getOrigin().equals(o2.getOrigin()))
            return false;
        if (!o1.getDestination().equals(o2.getDestination()))
            return false;

        if (!o1.getIs_hazmat().equals(o2.getIs_hazmat()))
            return false;

        boolean overlap = true;
        if (o1.getPickup_date().isAfter(o2.getDelivery_date()) ||
                o2.getPickup_date().isAfter(o1.getDelivery_date())) {
            overlap = false;
        }
        return overlap;
    }

    private OptimizationResponse buildResponse(TruckDto truck, List<OrderDto> validOrders, int optimalMask,
            List<Integer> bestMasks, long totalPayout, long totalWeight, long totalVolume) {
        OptimizationResponse response = new OptimizationResponse();
        response.setTruck_id(truck.getId());
        response.setTotal_payout_cents(totalPayout);
        response.setTotal_weight_lbs(totalWeight);
        response.setTotal_volume_cuft(totalVolume);

        List<String> selectedIds = new ArrayList<>();
        for (int i = 0; i < validOrders.size(); i++) {
            if ((optimalMask & (1 << i)) != 0) {
                selectedIds.add(validOrders.get(i).getId());
            }
        }
        response.setSelected_order_ids(selectedIds);
        double wUtil = (double) totalWeight / truck.getMax_weight_lbs() * 100.0;
        double vUtil = (double) totalVolume / truck.getMax_volume_cuft() * 100.0;

        response.setUtilization_weight_percent(roundDouble(wUtil));
        response.setUtilization_volume_percent(roundDouble(vUtil));

        if (bestMasks.size() > 1) {
            List<List<String>> alternatives = new ArrayList<>();
            for (int i = 1; i < bestMasks.size(); i++) {
                int mask = bestMasks.get(i);
                List<String> altIds = new ArrayList<>();
                for (int j = 0; j < validOrders.size(); j++) {
                    if ((mask & (1 << j)) != 0) {
                        altIds.add(validOrders.get(j).getId());
                    }
                }
                alternatives.add(altIds);
            }
            response.setAlternative_optimal_subsets(alternatives);
        }

        return response;
    }

    private OptimizationResponse buildEmptyResponse(String truckId) {
        OptimizationResponse resp = new OptimizationResponse();
        resp.setTruck_id(truckId);
        resp.setSelected_order_ids(new ArrayList<>());
        resp.setTotal_payout_cents(0L);
        resp.setTotal_weight_lbs(0L);
        resp.setTotal_volume_cuft(0L);
        resp.setUtilization_weight_percent(0.0);
        resp.setUtilization_volume_percent(0.0);
        return resp;
    }

    private Double roundDouble(double val) {
        if (Double.isNaN(val) || Double.isInfinite(val))
            return 0.0;
        BigDecimal bd = new BigDecimal(Double.toString(val));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
