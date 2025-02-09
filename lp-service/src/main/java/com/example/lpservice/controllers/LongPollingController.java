package com.example.lpservice.controllers;

import com.example.lpservice.services.LongPollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class LongPollingController {

    @Autowired
    private LongPollingService longPollingService;


    @PostMapping(value = "/submit", produces = "text/plain")
    public String submit() {
        return longPollingService.submitRequest().toString();
    }

    @GetMapping(value = "/poll/{uuid}", produces = "text/plain")
    public String poll(@PathVariable("uuid") UUID uuid) {
        return longPollingService.getStatus(uuid);
    }

}