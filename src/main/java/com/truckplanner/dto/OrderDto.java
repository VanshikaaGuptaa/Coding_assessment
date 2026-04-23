package com.truckplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class OrderDto {

    @NotBlank(message = "Order id is required")
    private String id;

    @NotNull(message = "payout_cents is required")
    @Min(value = 0, message = "payout_cents cannot be negative")
    private Long payout_cents;

    @NotNull(message = "weight_lbs is required")
    @Min(value = 0, message = "weight_lbs cannot be negative")
    private Long weight_lbs;

    @NotNull(message = "volume_cuft is required")
    @Min(value = 0, message = "volume_cuft cannot be negative")
    private Long volume_cuft;

    @NotBlank(message = "origin is required")
    private String origin;

    @NotBlank(message = "destination is required")
    private String destination;

    @NotNull(message = "pickup_date is required")
    private OffsetDateTime pickup_date;

    @NotNull(message = "delivery_date is required")
    private OffsetDateTime delivery_date;

    @NotNull(message = "is_hazmat is required")
    @JsonProperty("is_hazmat")
    private Boolean is_hazmat;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPayout_cents() {
        return payout_cents;
    }

    public void setPayout_cents(Long payout_cents) {
        this.payout_cents = payout_cents;
    }

    public Long getWeight_lbs() {
        return weight_lbs;
    }

    public void setWeight_lbs(Long weight_lbs) {
        this.weight_lbs = weight_lbs;
    }

    public Long getVolume_cuft() {
        return volume_cuft;
    }

    public void setVolume_cuft(Long volume_cuft) {
        this.volume_cuft = volume_cuft;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public OffsetDateTime getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(OffsetDateTime pickup_date) {
        this.pickup_date = pickup_date;
    }

    public OffsetDateTime getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(OffsetDateTime delivery_date) {
        this.delivery_date = delivery_date;
    }

    public Boolean getIs_hazmat() {
        return is_hazmat;
    }

    public void setIs_hazmat(Boolean is_hazmat) {
        this.is_hazmat = is_hazmat;
    }
}
