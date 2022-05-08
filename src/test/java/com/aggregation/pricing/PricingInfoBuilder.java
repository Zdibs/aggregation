package com.aggregation.pricing;

public class PricingInfoBuilder {

    private String countryCode;
    private String price;

    private PricingInfoBuilder() {
        // Not implemented on purpose
    }

    public static PricingInfoBuilder builder() {
        return new PricingInfoBuilder();
    }

    public PricingInfoBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public PricingInfoBuilder price(String price) {
        this.price = price;
        return this;
    }

    public PricingInfo build() {
        return new PricingInfo(countryCode, price);
    }

    public String toString() {
        return "PricingInfo.PricingInfoBuilder(countryCode=" + this.countryCode + ", price=" + this.price + ")";
    }
}
