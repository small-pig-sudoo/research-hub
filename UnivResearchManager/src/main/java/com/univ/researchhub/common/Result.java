package com.univ.researchhub.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码 200 成功，400/500 错误
    private String message; // 返回消息
    private T data; // 返回的数据

    // 成功
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    // 失败
    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMessage(message);
        r.setData(null);
        return r;
    }
}
