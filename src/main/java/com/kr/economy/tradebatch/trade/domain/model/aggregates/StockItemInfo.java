package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Slf4j
public class StockItemInfo {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String ticker;                  // 종목 코드

    @Column
    private int parValue;                   // 액면가

    @Column
    private String useYn;                   // 사용 여부
}
