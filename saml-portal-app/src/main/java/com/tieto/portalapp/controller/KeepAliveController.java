package com.tieto.portalapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeepAliveController {
    public static final String KEEP_ALIVE_PAGE = "/keep-alive";

    public static final Logger LOGGER = LoggerFactory.getLogger(KeepAliveController.class);

    @RequestMapping( method = RequestMethod.GET, path = "/keepAlive", headers = "Accept=*/*")
    public ResponseEntity keepAlive(){
        LOGGER.info("Processing keep alive request");
        return new ResponseEntity(HttpStatus.OK);
    }
}
