package org.orury.common.error.code;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GymErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 암장이 존재하지 않습니다."),
    INVALID_GYM_TYPE(HttpStatus.NOT_FOUND.value(), "유효하지 않은 암장 타입입니다.");

    private final int status;
    private final String message;

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
