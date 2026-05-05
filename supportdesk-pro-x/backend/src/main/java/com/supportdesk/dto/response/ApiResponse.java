package com.supportdesk.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Data @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private ApiMeta meta;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().status(200).message("Success").data(data).build();
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder().status(200).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder().status(201).message("Created").data(data).build();
    }

    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder().status(204).message("No Content").build();
    }
}
