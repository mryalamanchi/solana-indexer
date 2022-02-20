package com.rarible.protocol.solana.nft.api.controller

import com.rarible.protocol.solana.common.converter.TokenConverter
import com.rarible.protocol.solana.common.converter.TokenMetaConverter
import com.rarible.protocol.solana.common.meta.TokenMetaService
import com.rarible.protocol.solana.nft.api.service.TokenService
import com.rarible.solana.protocol.api.controller.TokenControllerApi
import com.rarible.solana.protocol.dto.TokenDto
import com.rarible.solana.protocol.dto.TokenMetaDto
import com.rarible.solana.protocol.dto.TokensDto
import kotlinx.coroutines.flow.toList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val tokenService: TokenService,
    private val tokenMetaService: TokenMetaService
) : TokenControllerApi {
    override suspend fun getTokenByAddress(tokenAddress: String): ResponseEntity<TokenDto> {
        val token = tokenService.getToken(tokenAddress)
        return ResponseEntity.ok(TokenConverter.convert(token))
    }

    override suspend fun getTokenMetaByAddress(tokenAddress: String): ResponseEntity<TokenMetaDto> {
        val tokenMeta = tokenMetaService.getTokenMeta(tokenAddress)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(TokenMetaConverter.convert(tokenMeta))
    }

    override suspend fun getTokensByCollection(collection: String): ResponseEntity<TokensDto> {
        val tokensByOnChainCollection = tokenService.getTokensByMetaplexCollectionAddress(collection).toList()
        val tokensByOffChainCollection = tokenService.getTokensByOffChainCollectionHash(collection).toList()
        return ResponseEntity.ok(TokenConverter.convert(tokensByOnChainCollection + tokensByOffChainCollection))
    }
}
