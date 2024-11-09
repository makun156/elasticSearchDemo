package com.elastic.elasticsearchdemo.response;

import lombok.Data;

@Data
public class ResponseBean {
    private String code;
    private String msg;
    private Object data;
}
