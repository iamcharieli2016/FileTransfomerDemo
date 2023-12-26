package org.example.controller;

import org.example.service.DistributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class DistributorController {
    @Autowired
    DistributorService distributorService;

    @RequestMapping(path= {"distributor"}, method = {RequestMethod.POST})
    public void distributor(@RequestParam String param) {
        System.out.println("distributor: param" + param);
//        distributorService.send(file);
    }
}
