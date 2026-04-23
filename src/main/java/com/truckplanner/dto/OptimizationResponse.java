package com.truckplanner.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptimizationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String truck_id;
    private List<String> selected_order_ids;
    private Long total_payout_cents;
    private Long total_weight_lbs;
    private Long total_volume_cuft;
    private Double utilization_weight_percent;
    private Double utilization_volume_percent;

    private List<List<String>> alternative_optimal_subsets;

    public String getTruck_id() {
        return truck_id;
    }

    public void setTruck_id(String truck_id) {
        this.truck_id = truck_id;
    }

    public List<String> getSelected_order_ids() {
        return selected_order_ids;
    }

    public void setSelected_order_ids(List<String> selected_order_ids) {
        this.selected_order_ids = selected_order_ids;
    }

    public Long getTotal_payout_cents() {
        return total_payout_cents;
    }

    public void setTotal_payout_cents(Long total_payout_cents) {
        this.total_payout_cents = total_payout_cents;
    }

    public Long getTotal_weight_lbs() {
        return total_weight_lbs;
    }

    public void setTotal_weight_lbs(Long total_weight_lbs) {
        this.total_weight_lbs = total_weight_lbs;
    }

    public Long getTotal_volume_cuft() {
        return total_volume_cuft;
    }

    public void setTotal_volume_cuft(Long total_volume_cuft) {
        this.total_volume_cuft = total_volume_cuft;
    }

    public Double getUtilization_weight_percent() {
        return utilization_weight_percent;
    }

    public void setUtilization_weight_percent(Double utilization_weight_percent) {
        this.utilization_weight_percent = utilization_weight_percent;
    }

    public Double getUtilization_volume_percent() {
        return utilization_volume_percent;
    }

    public void setUtilization_volume_percent(Double utilization_volume_percent) {
        this.utilization_volume_percent = utilization_volume_percent;
    }

    public List<List<String>> getAlternative_optimal_subsets() {
        return alternative_optimal_subsets;
    }

    public void setAlternative_optimal_subsets(List<List<String>> alternative_optimal_subsets) {
        this.alternative_optimal_subsets = alternative_optimal_subsets;
    }
}
