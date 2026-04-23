package com.truckplanner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OptimizationRequest {

    @NotNull(message = "truck details are required")
    @Valid
    private TruckDto truck;

    @NotNull(message = "orders are required")
    @Valid
    private List<OrderDto> orders;

    public TruckDto getTruck() {
        return truck;
    }

    public void setTruck(TruckDto truck) {
        this.truck = truck;
    }

    public List<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
    }
}
