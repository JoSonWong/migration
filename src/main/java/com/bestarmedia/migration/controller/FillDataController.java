package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.FillDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public JsonResponse fillData(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                 @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip_total", fillDataService.fillTotal(from, to));
        map.put("tip_ly", fillDataService.fillLY(from, to));
        map.put("tip_yjx", fillDataService.fillYJX(from, to));
        map.put("tip_bns1", fillDataService.fillBNS(from, to));

        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-data/bns")
    public JsonResponse fillDataBNS(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip_bns1", fillDataService.fillBNS(from, to));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-data/yjx")
    public JsonResponse fillDataYJX(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", fillDataService.fillYJX(from, to));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-data/ly")
    public JsonResponse fillDataLY(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                   @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", fillDataService.fillLY(from, to));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-data/total")
    public JsonResponse fillDataTotal(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", fillDataService.fillTotal(from, to));
        return JsonResponseHandler.success(map);
    }
}
