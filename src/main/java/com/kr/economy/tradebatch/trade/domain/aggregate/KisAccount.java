package com.kr.economy.tradebatch.trade.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KisAccount {

    @Id
    @Column(nullable = false)
    private String account_id;

    @Column
    private String socketKey;
}
