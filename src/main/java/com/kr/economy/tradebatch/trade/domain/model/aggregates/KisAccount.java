package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class KisAccount {

    @Id
    @Column(nullable = false)
    private String account_id;

    @Column
    private String socketKey;

    @Column
    @CreatedDate
    private LocalDateTime regDate;

    @Column
    @LastModifiedDate
    private LocalDateTime modDate;

    /**
     * 소켓키 만료 유무 확인
     * @return
     */
    public boolean isRetired() {
        return modDate.isBefore(getExpirationTime());
    }

    /**
     * 소켓키 만료 시간 조회
     * 만료시간: 매일 자정
     * @return
     */
    public LocalDateTime getExpirationTime() {
        return LocalDateTime.now().toLocalDate().atStartOfDay();
    }
}
