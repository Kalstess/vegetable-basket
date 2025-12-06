package com.example.cailanzi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage<T> {
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(200, "成功", data, LocalDateTime.now());
    }

    public static <T> ResponseMessage<T> success(String message, T data) {
        return new ResponseMessage<>(200, message, data, LocalDateTime.now());
    }

    public static <T> ResponseMessage<T> error(int code, String message) {
        return new ResponseMessage<>(code, message, null, LocalDateTime.now());
    }
}