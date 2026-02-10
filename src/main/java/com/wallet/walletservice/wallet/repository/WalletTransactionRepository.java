package com.wallet.walletservice.wallet.repository;


import com.wallet.walletservice.ledger.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);

}
