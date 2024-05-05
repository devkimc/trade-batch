package com.kr.economy.tradebatch.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SERVER_ERROR("0099", "서비스 접속이 원활하지 않습니다. 잠시 후 다시 이용해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String resCode;
    private final String resMessage;
    private final HttpStatus httpStatus;
}
