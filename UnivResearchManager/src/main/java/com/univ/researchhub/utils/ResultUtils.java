package com.univ.researchhub.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultUtils {
    /**
     * 成功响应（带数据）
     */
    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static Map<String, Object> success() {
        return success(null);
    }

    /**
     * 失败响应
     */
    public static Map<String, Object> fail(String message, int code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        return result;
    }

    /**
     * 失败响应（默认code=400）
     */
    public static Map<String, Object> fail(String message) {
        return fail(message, 400);
    }
}