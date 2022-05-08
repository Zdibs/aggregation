package com.aggregation.pricing;

import com.aggregation.pricing.api.PricingResource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PricingService {


    public List<PricingResource>  getPricingInfo(List<String> countryCodes) {
        return Collections.emptyList();
    }
}
