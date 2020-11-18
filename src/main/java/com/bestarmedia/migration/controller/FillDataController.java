package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.FillDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class FillDataController {

    private final FillDataService fillDataService;

    @Autowired
    public FillDataController(FillDataService fillDataService) {
        this.fillDataService = fillDataService;
    }


    @GetMapping("/v7.0/migration/fill-data")
    public JsonResponse fillData(@RequestParam(value = "index", defaultValue = "0") Integer index) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", fillDataService.fill(index));
        return JsonResponseHandler.success(map);
    }

}
