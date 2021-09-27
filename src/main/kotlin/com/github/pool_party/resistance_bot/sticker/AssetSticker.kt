package com.github.pool_party.resistance_bot.sticker

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.utils.resolveName
import java.util.concurrent.CompletableFuture

data class AssetSticker(val fileName: String, val emojis: String) {

    private var fileId: String? = null

    fun Bot.send(chatId: Long): CompletableFuture<out Message>? {
        if (fileId == null) {
            val setName = resolveName(StickerSetConfiguration.STICKER_SET_NAME)

            val stickerSet = getStickerSet(setName)
                .handle { value, _ -> value }
                .join()
                ?: return null

            fileId = stickerSet.stickers[StickerSetConfiguration.STICKERS.indexOf(this@AssetSticker)].file_id
        }

        return fileId?.let { sendSticker(chatId, it) }
    }
}

