package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KisAccountRepository extends JpaRepository<KisAccount, String> {

}
