package com.elastic.elasticsearchdemo.response;

import com.elastic.elasticsearchdemo.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBean {
    private boolean flag=true;
    private Integer code;
    private String message;
    private Object data;
    public static ResponseBean success(Object data){
        return new ResponseBean(true, ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getMsg(),data);
    }
    public static ResponseBean fail(ResponseEnum responseStatus){
        return new ResponseBean(true,responseStatus.getCode(),responseStatus.getMsg(),null);
    }
}
