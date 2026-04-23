package com.truckplanner.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TruckDto {

    @NotBlank(message = "Truck id is required")
    private String id;

    @NotNull(message = "max_weight_lbs is required")
    @Min(value = 0, message = "max_weight_lbs cannot be negative")
    private Long max_weight_lbs;

    @NotNull(message = "max_volume_cuft is required")
    @Min(value = 0, message = "max_volume_cuft cannot be negative")
    private Long max_volume_cuft;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMax_weight_lbs() {
        return max_weight_lbs;
    }

    public void setMax_weight_lbs(Long max_weight_lbs) {
        this.max_weight_lbs = max_weight_lbs;
    }

    public Long getMax_volume_cuft() {
        return max_volume_cuft;
    }

    public void setMax_volume_cuft(Long max_volume_cuft) {
        this.max_volume_cuft = max_volume_cuft;
    }
}
