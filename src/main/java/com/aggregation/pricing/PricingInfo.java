package com.aggregation.pricing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PricingInfo {

    private String countryCode;
    private String price;

    public PricingInfo(String countryCode, String price) {
        this.countryCode = countryCode;
        this.price = price;
    }
}
