package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.kr.economy.tradebatch.common.util.DateUtil.getTodayLocalDateTime;

@Entity
@Getter
@ToString
@Slf4j
public class StockItemInfo {

    @Id
    private String ticker;                  // 종목 코드

    @Column
    private String tickerName;              // 종목 명

    @Column
    private int parValue;                   // 액면가

    @Column
    private String useYn;                   // 사용 여부

    /**
     * 하루 최대 손실 금액
     *   - 최대 손실 금액 이상일 경우 당일 매수를 금지한다.
     * @return
     */
    public int getDailyLossLimitPrice() {
        return this.parValue * 10;
    }

    /**
     * 손절매 필요 여부
     * @param buyPrice
     * @param quotedPrice
     * @return
     */
    public boolean haveToStopLoss(int buyPrice, int quotedPrice) {
        return quotedPrice <= buyPrice - this.parValue * 3;
    }

    /**
     * 익절매 필요 여부
     * @param buyPrice
     * @param quotedPrice
     * @return
     */
    public boolean haveToTakeProfit(int buyPrice, int quotedPrice) {
        return quotedPrice >= buyPrice + this.parValue * 3;
    }

    /**
     * 주식 판매 필요 여부
     * @param buyPrice
     * @param quotedPrice
     * @return
     */
    public boolean haveToSell(int buyPrice, int quotedPrice) {
        // 오후 3시 25분일 경우 모두 매도
        if (LocalDateTime.now().isAfter(getTodayLocalDateTime(15, 25, 0))) {
            log.info("[매도 신호] 장 마감 5분 전 매도 : {}", LocalDateTime.now());
            return true;
        }


        return this.haveToStopLoss(buyPrice, quotedPrice) || this.haveToTakeProfit(buyPrice, quotedPrice);

        // 매수 후 13분 초과 시 매도 로직 중단
//        String mm = String.valueOf(now.getMonth().getValue());
//        String dd = String.valueOf(now.getDayOfMonth());
//
//        if (mm.length() == 1) {
//            mm = "0" + mm;
//        }
//
//        if (dd.length() == 1) {
//            dd = "0" + dd;
//        }
//
//        LocalDate date = LocalDate.parse(now.getYear() + "-" + mm + "-" + dd);
//        LocalDateTime tradingLdt = date.atTime(Integer.parseInt(tradingTime.substring(0, 2)), Integer.parseInt(tradingTime.substring(2, 4)), Integer.parseInt(tradingTime.substring(4, 6)));
//        LocalDateTime currentTradingLdt = date.atTime(Integer.parseInt(currentTradingTime.substring(0, 2)), Integer.parseInt(currentTradingTime.substring(2, 4)), Integer.parseInt(currentTradingTime.substring(4, 6)));
//
//        // 매수 후 13분 초과 시 매도
//        boolean isLimitTimeout = currentTradingLdt.isAfter(tradingLdt.plusMinutes(13));

//        if (isLimitTimeout) {
//            log.info("[매도 신호] 매수 후 13분 초과 - 매수 시간 : {}", tradingTime);
//        }
    }
}
