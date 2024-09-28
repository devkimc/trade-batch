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
    private String accountId;

    @Column
    private String socketKey;

    @Column(length = 1000)
    private String accessToken;

    @Column
    private String socketDecryptIv;

    @Column
    private String socketDecryptKey;

    @Column
    @CreatedDate
    private LocalDateTime regDate;

    @Column
    @LastModifiedDate
    private LocalDateTime modDate;

    public void renewAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void renewSocketKey(String socketKey) {
        this.socketKey = socketKey;
    }

    public void updateSocketDecryptKey(String iv, String key) {
        this.socketDecryptIv = iv;
        this.socketDecryptKey = key;
    }
}
