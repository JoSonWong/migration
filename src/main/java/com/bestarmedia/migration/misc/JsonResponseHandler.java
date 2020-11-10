package com.bestarmedia.migration.misc;

import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@ResponseBody
public class JsonResponseHandler {

    @SuppressWarnings("unchecked")
    public static <T> JsonResponse success(HashMap<String, T> data) {
        JsonResponse response = new JsonResponse();
        response.setCode(0);
        response.setMsg("操作成功");
        response.setData(data);
        response.setTips("操作成功");
        return response;
    }

}
