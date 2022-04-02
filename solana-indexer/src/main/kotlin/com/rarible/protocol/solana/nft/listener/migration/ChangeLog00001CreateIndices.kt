package com.rarible.protocol.solana.nft.listener.migration

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.rarible.protocol.solana.common.repository.BalanceRepository
import com.rarible.protocol.solana.common.repository.MetaplexMetaRepository
import com.rarible.protocol.solana.common.repository.MetaplexOffChainMetaRepository
import com.rarible.protocol.solana.common.repository.OrderRepository
import com.rarible.protocol.solana.common.repository.SolanaAuctionHouseOrderRecordsRepository
import com.rarible.protocol.solana.common.repository.SolanaBalanceRecordsRepository
import com.rarible.protocol.solana.common.repository.TokenRepository
import io.changock.migration.api.annotations.NonLockGuarded
import kotlinx.coroutines.runBlocking

@ChangeLog(order = "00001")
class ChangeLog00001CreateIndices {

    @ChangeSet(
        id = "ChangeLog00001CreateIndices.createIndicesForAllCollections",
        order = "00001",
        author = "protocol",
        runAlways = true
    )
    fun createIndicesForAllCollections(
        @NonLockGuarded balanceRepository: BalanceRepository,
        @NonLockGuarded tokenRepository: TokenRepository,
        @NonLockGuarded metaplexMetaRepository: MetaplexMetaRepository,
        @NonLockGuarded metaplexOffChainMetaRepository: MetaplexOffChainMetaRepository,
        @NonLockGuarded orderRepository: OrderRepository,

        @NonLockGuarded balanceRecordsRepository: SolanaBalanceRecordsRepository,
        @NonLockGuarded orderRecordsRepository: SolanaAuctionHouseOrderRecordsRepository
        ) = runBlocking {
        balanceRepository.createIndexes()
        tokenRepository.createIndexes()
        metaplexMetaRepository.createIndexes()
        metaplexOffChainMetaRepository.createIndexes()
        orderRepository.createIndexes()

        balanceRecordsRepository.createIndexes()
        orderRecordsRepository.createIndexes()
    }
}