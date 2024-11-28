package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.util.AES256;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(KisAccount.class);
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

    /**
     * 복호화 key, iv 로 복호화
     * @param message
     * @return
     */
    public String decryptMessageByKeyAndIv(String message) {
        if (StringUtils.isEmpty(message) || StringUtils.isEmpty(this.socketDecryptKey) || StringUtils.isEmpty(this.socketDecryptIv)) {
            log.error("[AES256 복호화 실패] message: {}, key: {}, iv: {}", message, socketDecryptKey, socketDecryptIv);
            return "";
        }

        return new AES256().decrypt(message, this.socketDecryptKey, this.socketDecryptIv);
    }
}
