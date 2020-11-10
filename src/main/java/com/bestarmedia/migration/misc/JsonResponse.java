package com.bestarmedia.migration.misc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResponse<T> implements Serializable {

    //数据对象，可以是单个对象，数组，哈希表等
    @JsonProperty(value = "data")
    private HashMap<String, T> data;

    //错误码，默认为0
    @JsonProperty(value = "code")
    private Integer code;

    //系统消息，默认为""
    @JsonProperty(value = "msg")
    private String msg;

    //客户端提示语，默认为""
    @JsonProperty(value = "tips")
    private String tips;

    //资源的绝对url，方便调试，默认为""，一般POST,PUT请求会返回此值，但不作要求
    @JsonProperty(value = "url")
    private String url;

    public JsonResponse() {
        this.data = null;
        this.code = 0;
        this.msg = "";
        this.tips = "";
        this.url = "";
    }

    @JsonCreator
    public JsonResponse(@JsonProperty(value = "data") HashMap<String, T> data,
                        @JsonProperty(value = "code") Integer code,
                        @JsonProperty(value = "msg") String msg,
                        @JsonProperty(value = "tips") String tips,
                        @JsonProperty(value = "url") String url) {
        this.data = data;
        this.code = code;
        this.msg = msg == null ? "" : msg;
        this.tips = tips == null ? "" : tips;
        this.url = url == null ? "" : url;
    }
}
