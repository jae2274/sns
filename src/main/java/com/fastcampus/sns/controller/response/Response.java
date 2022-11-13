package com.fastcampus.sns.controller.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private String resultCode;
    T result;

    public static <T> Response<T> error(String errorCode){
        return new Response(errorCode,null);
    }

    public static Response<Void> success(){
        return new Response("SUCCESS",null);
    }

    public static <T> Response<T> success(T Result){
        return new Response("SUCCESS",Result);
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    public String toStream()  {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "JsonProcessingException occurs";
        }
    }
}
