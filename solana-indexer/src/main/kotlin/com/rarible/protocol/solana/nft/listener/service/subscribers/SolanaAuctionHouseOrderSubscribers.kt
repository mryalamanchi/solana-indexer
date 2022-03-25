package com.rarible.protocol.solana.nft.listener.service.subscribers

import com.rarible.blockchain.scanner.solana.client.SolanaBlockchainBlock
import com.rarible.blockchain.scanner.solana.client.SolanaBlockchainLog
import com.rarible.blockchain.scanner.solana.model.SolanaDescriptor
import com.rarible.blockchain.scanner.solana.subscriber.SolanaLogEventSubscriber
import com.rarible.protocol.solana.borsh.Buy
import com.rarible.protocol.solana.borsh.Cancel
import com.rarible.protocol.solana.borsh.ExecuteSale
import com.rarible.protocol.solana.borsh.Sell
import com.rarible.protocol.solana.borsh.parseAuctionHouseInstruction
import com.rarible.protocol.solana.common.records.SolanaAuctionHouseOrderRecord
import com.rarible.protocol.solana.common.util.toBigInteger
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AuctionHouseOrderSellSubscriber : SolanaLogEventSubscriber {
    override fun getDescriptor(): SolanaDescriptor = object : SolanaDescriptor(
        programId = SolanaProgramId.AUCTION_HOUSE_PROGRAM,
        id = "auction_house_order_sell",
        groupId = SubscriberGroup.AUCTION_HOUSE_ORDER.id,
        entityType = SolanaAuctionHouseOrderRecord.SellRecord::class.java,
        collection = SubscriberGroup.AUCTION_HOUSE_ORDER.collectionName
    ) {}

    override suspend fun getEventRecords(
        block: SolanaBlockchainBlock,
        log: SolanaBlockchainLog
    ): List<SolanaAuctionHouseOrderRecord.SellRecord> {
        val record = when (val instruction = log.instruction.data.parseAuctionHouseInstruction(log.instruction.accounts.size)) {
            is Sell -> SolanaAuctionHouseOrderRecord.SellRecord(
                maker = log.instruction.accounts[0],
                sellPrice = instruction.price.toBigInteger(),
                // Only the token account is available in the record.
                // Mint will be set in the SolanaBalanceLogEventFilter by account <-> mint association.
                tokenAccount = log.instruction.accounts[1],
                mint = "",
                amount = instruction.size.toBigInteger(),
                auctionHouse = log.instruction.accounts[4],
                log = log.log,
                timestamp = Instant.ofEpochSecond(block.timestamp)
            )
            else -> return emptyList()
        }

        return listOf(record)
    }
}

@Component
class AuctionHouseOrderBuySubscriber : SolanaLogEventSubscriber {
    override fun getDescriptor(): SolanaDescriptor = object : SolanaDescriptor(
        programId = SolanaProgramId.AUCTION_HOUSE_PROGRAM,
        id = "auction_house_buy",
        groupId = SubscriberGroup.AUCTION_HOUSE_ORDER.id,
        entityType = SolanaAuctionHouseOrderRecord.BuyRecord::class.java,
        collection = SubscriberGroup.AUCTION_HOUSE_ORDER.collectionName
    ) {}

    override suspend fun getEventRecords(
        block: SolanaBlockchainBlock,
        log: SolanaBlockchainLog
    ): List<SolanaAuctionHouseOrderRecord.BuyRecord> {
        val record = when (val instruction = log.instruction.data.parseAuctionHouseInstruction(log.instruction.accounts.size)) {
            is Buy -> SolanaAuctionHouseOrderRecord.BuyRecord(
                maker = log.instruction.accounts[0],
                buyPrice = instruction.price.toBigInteger(),
                // Only the token account is available in the record.
                // Mint will be set in the SolanaBalanceLogEventFilter by account <-> mint association.
                tokenAccount = log.instruction.accounts[4],
                mint = "",
                amount = instruction.size.toBigInteger(),
                auctionHouse = log.instruction.accounts[8],
                log = log.log,
                timestamp = Instant.ofEpochSecond(block.timestamp)
            )
            else -> return emptyList()
        }

        return listOf(record)
    }
}

@Component
class AuctionHouseOrderExecuteSaleSubscriber : SolanaLogEventSubscriber {
    override fun getDescriptor(): SolanaDescriptor = object : SolanaDescriptor(
        programId = SolanaProgramId.AUCTION_HOUSE_PROGRAM,
        id = "auction_house_execute_sale",
        groupId = SubscriberGroup.AUCTION_HOUSE_ORDER.id,
        entityType = SolanaAuctionHouseOrderRecord.ExecuteSaleRecord::class.java,
        collection = SubscriberGroup.AUCTION_HOUSE_ORDER.collectionName
    ) {}

    override suspend fun getEventRecords(
        block: SolanaBlockchainBlock,
        log: SolanaBlockchainLog
    ): List<SolanaAuctionHouseOrderRecord.ExecuteSaleRecord> =
        when (val instruction = log.instruction.data.parseAuctionHouseInstruction(log.instruction.accounts.size)) {
            is ExecuteSale -> {
                val sellRecord = SolanaAuctionHouseOrderRecord.ExecuteSaleRecord(
                    buyer = log.instruction.accounts[0],
                    seller = log.instruction.accounts[1],
                    price = instruction.buyerPrice.toBigInteger(),
                    mint = log.instruction.accounts[3],
                    treasuryMint = log.instruction.accounts[5],
                    amount = instruction.size.toBigInteger(),
                    auctionHouse = log.instruction.accounts[10],
                    log = log.log,
                    timestamp = Instant.ofEpochSecond(block.timestamp),
                    direction = SolanaAuctionHouseOrderRecord.ExecuteSaleRecord.Direction.SELL
                )
                listOf(
                    sellRecord,
                    sellRecord.copy(direction = SolanaAuctionHouseOrderRecord.ExecuteSaleRecord.Direction.BUY)
                )
            }
            else -> emptyList()
        }
}

@Component
class AuctionHouseOrderCancelSubscriber : SolanaLogEventSubscriber {
    override fun getDescriptor(): SolanaDescriptor = object : SolanaDescriptor(
        programId = SolanaProgramId.AUCTION_HOUSE_PROGRAM,
        id = "auction_house_cancel",
        groupId = SubscriberGroup.AUCTION_HOUSE_ORDER.id,
        entityType = SolanaAuctionHouseOrderRecord.CancelRecord::class.java,
        collection = SubscriberGroup.AUCTION_HOUSE_ORDER.collectionName
    ) {}

    override suspend fun getEventRecords(
        block: SolanaBlockchainBlock,
        log: SolanaBlockchainLog
    ): List<SolanaAuctionHouseOrderRecord.CancelRecord> {
        val record = when (val instruction = log.instruction.data.parseAuctionHouseInstruction(log.instruction.accounts.size)) {
            is Cancel -> SolanaAuctionHouseOrderRecord.CancelRecord(
                owner = log.instruction.accounts[0],
                mint = log.instruction.accounts[2],
                price = instruction.price.toBigInteger(),
                amount = instruction.size.toBigInteger(),
                log = log.log,
                timestamp = Instant.ofEpochSecond(block.timestamp),
                auctionHouse = log.instruction.accounts[4]
            )
            else -> return emptyList()
        }

        return listOf(record)
    }
}