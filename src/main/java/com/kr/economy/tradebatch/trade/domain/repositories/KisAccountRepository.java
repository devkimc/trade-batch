package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.aggregate.KisAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KisAccountRepository extends JpaRepository<KisAccount, String> {

}
