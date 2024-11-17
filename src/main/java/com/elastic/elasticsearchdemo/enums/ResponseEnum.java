package com.elastic.elasticsearchdemo.enums;
public enum ResponseEnum {
    SUCCESS(200,"操作成功"),
    FAIL(200,"操作失败"),
    PARAM_ERROR(400,"参数错误"),
    NOT_LOGIN(401,"未登录"),
    NOT_PERMISSION(403,"无权限"),
    NOT_FOUND(404,"未找到"),
    SERVER_ERROR(500,"服务器错误"),
    ;

    private Integer code;
    private String msg;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}