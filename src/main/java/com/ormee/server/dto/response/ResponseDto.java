package com.ormee.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
@Getter
public class ResponseDto<T> {
    private String status;
    private int code;
    private T data;

    public static ResponseDto success() {
        return new ResponseDto("success", 200, null);
    }

    public static <T> ResponseDto success(T data) {
        return new ResponseDto("success", 200, data);
    }

    public static ResponseDto fail(int code, String message) {
        return new ResponseDto("fail", code, message);
    }
}
