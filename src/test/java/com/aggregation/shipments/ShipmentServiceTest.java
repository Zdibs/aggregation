package com.aggregation.shipments;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private GetShipmentsService getShipmentsService;


    @InjectMocks
    private ShipmentsService shipmentsService;


    void getShipmentInfo_calledFiveTimes() {

    }
}
