package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KisAccountRepository extends JpaRepository<KisAccount, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select k from KisAccount k where k.accountId = :accountId")
    KisAccount findByIdForUpdate(String accountId);
}
