package com.kr.economy.tradebatch.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class KisUtil {

    /**
     * 종합계좌번호 조회
     * @param accountNumber
     * @return
     */
    public static String getCano(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            throw new RuntimeException("[종합계좌번호 조회 실패] accountNumber null");
        }

        // TODO 예외처리 추가
        String[] splittedAccountNumber = accountNumber.split("-");

        return splittedAccountNumber[0];
    }

    /**
     * 계좌상품코드 조회
     * @param accountNumber
     * @return
     */
    public static String getAcntPrdtCd(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            throw new RuntimeException("[계좌상품코드 조회 실패] accountNumber null");
        }

        // TODO 예외처리 추가
        String[] splittedAccountNumber = accountNumber.split("-");

        return splittedAccountNumber[1];
    }
}
