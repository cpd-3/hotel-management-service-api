package com.cpd.hotel_system.hotel_management_service_api.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// http://localhost:9091/hotel-management/api/v1/test/check

@RestController
@RequestMapping("/hotel-management/api/v1/test")
public class TestController {

    @GetMapping("/check")
    public String Test(){
        return "Connected!.";
    }
}
