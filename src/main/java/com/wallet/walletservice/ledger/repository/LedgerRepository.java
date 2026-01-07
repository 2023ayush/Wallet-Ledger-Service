package com.wallet.walletservice.ledger.repository;
import com.wallet.walletservice.ledger.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}

