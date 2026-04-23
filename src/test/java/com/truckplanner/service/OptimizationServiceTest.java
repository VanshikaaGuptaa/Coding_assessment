package com.truckplanner.service;

import com.truckplanner.dto.OptimizationRequest;
import com.truckplanner.dto.OptimizationResponse;
import com.truckplanner.dto.OrderDto;
import com.truckplanner.dto.TruckDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptimizationServiceTest {

    private OptimizationService service;

    @BeforeEach
    void setUp() {
        service = new OptimizationService();
    }

    @Test
    void testBasicOptimization_SelectAll() {
        OptimizationRequest req = new OptimizationRequest();
        TruckDto truck = new TruckDto();
        truck.setId("T1");
        truck.setMax_weight_lbs(100L);
        truck.setMax_volume_cuft(100L);
        req.setTruck(truck);

        List<OrderDto> orders = new ArrayList<>();
        orders.add(createOrder("O1", 100, 40, 40, "A", "B", "2024-01-01T10:00:00Z", "2024-01-02T10:00:00Z", false));
        orders.add(createOrder("O2", 150, 50, 50, "A", "B", "2024-01-01T11:00:00Z", "2024-01-02T12:00:00Z", false));
        req.setOrders(orders);

        OptimizationResponse res = service.optimize(req);

        assertEquals("T1", res.getTruck_id());
        assertEquals(2, res.getSelected_order_ids().size());
        assertEquals(250L, res.getTotal_payout_cents());
        assertEquals(90.0, res.getUtilization_weight_percent());
        assertEquals(90.0, res.getUtilization_volume_percent());
    }

    @Test
    void testExceedCapacity_SelectMostValuable() {
        OptimizationRequest req = new OptimizationRequest();
        TruckDto truck = new TruckDto();
        truck.setId("T1");
        truck.setMax_weight_lbs(100L);
        truck.setMax_volume_cuft(100L);
        req.setTruck(truck);

        List<OrderDto> orders = new ArrayList<>();
        orders.add(createOrder("O1", 500, 60, 60, "A", "B", "2024-01-01T10:00:00Z", "2024-01-02T10:00:00Z", false));
        orders.add(createOrder("O2", 600, 55, 55, "A", "B", "2024-01-01T11:00:00Z", "2024-01-02T12:00:00Z", false));
        req.setOrders(orders);

        // They conflict over weight limit, only O2 should be picked
        OptimizationResponse res = service.optimize(req);
        assertEquals(1, res.getSelected_order_ids().size());
        assertTrue(res.getSelected_order_ids().contains("O2"));
        assertEquals(600L, res.getTotal_payout_cents());
    }

    @Test
    void testTimeConflict() {
        OptimizationRequest req = new OptimizationRequest();
        TruckDto truck = new TruckDto();
        truck.setId("T1");
        truck.setMax_weight_lbs(1000L);
        truck.setMax_volume_cuft(1000L);
        req.setTruck(truck);

        List<OrderDto> orders = new ArrayList<>();
        // O1 ends Jan 1
        orders.add(createOrder("O1", 100, 10, 10, "A", "B", "2024-01-01T10:00:00Z", "2024-01-01T15:00:00Z", false));
        // O2 starts Jan 2, completely isolated -> incompatible interval intersection
        orders.add(createOrder("O2", 200, 10, 10, "A", "B", "2024-01-02T10:00:00Z", "2024-01-02T15:00:00Z", false));
        req.setOrders(orders);

        OptimizationResponse res = service.optimize(req);
        // Returns the most valuable order (O2)
        assertEquals(1, res.getSelected_order_ids().size());
        assertEquals("O2", res.getSelected_order_ids().get(0));
    }

    @Test
    void testHazmatConflict() {
        OptimizationRequest req = new OptimizationRequest();
        TruckDto truck = new TruckDto();
        truck.setId("T1");
        truck.setMax_weight_lbs(1000L);
        truck.setMax_volume_cuft(1000L);
        req.setTruck(truck);

        List<OrderDto> orders = new ArrayList<>();
        orders.add(createOrder("O1", 100, 10, 10, "A", "B", "2024-01-01T10:00:00Z", "2024-01-02T10:00:00Z", true));
        orders.add(createOrder("O2", 150, 10, 10, "A", "B", "2024-01-01T10:00:00Z", "2024-01-02T10:00:00Z", false));
        req.setOrders(orders);

        OptimizationResponse res = service.optimize(req);
        assertEquals(1, res.getSelected_order_ids().size());
        assertEquals("O2", res.getSelected_order_ids().get(0));
    }

    @Test
    void testPerformanceScale_22Items() {
        OptimizationRequest req = new OptimizationRequest();
        TruckDto truck = new TruckDto();
        truck.setId("T1");
        truck.setMax_weight_lbs(100000L);
        truck.setMax_volume_cuft(100000L);
        req.setTruck(truck);

        List<OrderDto> orders = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            orders.add(createOrder("O" + i, 100 + i, 10, 10, "A", "B", "2024-01-01T10:00:00Z", "2024-03-01T10:00:00Z",
                    false));
        }
        req.setOrders(orders);

        long start = System.currentTimeMillis();
        OptimizationResponse res = service.optimize(req);
        long duration = System.currentTimeMillis() - start;

        assertEquals(22, res.getSelected_order_ids().size());
        assertTrue(duration < 2000, "Performance was slower than 2s: " + duration + "ms");
    }

    private OrderDto createOrder(String id, long payout, long weight, long volume,
            String origin, String dest, String start, String end, boolean hazmat) {
        OrderDto o = new OrderDto();
        o.setId(id);
        o.setPayout_cents(payout);
        o.setWeight_lbs(weight);
        o.setVolume_cuft(volume);
        o.setOrigin(origin);
        o.setDestination(dest);
        o.setPickup_date(OffsetDateTime.parse(start));
        o.setDelivery_date(OffsetDateTime.parse(end));
        o.setIs_hazmat(hazmat);
        return o;
    }
}
