package com.github.pool_party.resistance_bot.sticker

import com.elbekD.bot.Bot
import com.elbekD.bot.types.StickerSet
import com.github.pool_party.resistance_bot.sticker.StickerSetConfiguration.file
import kotlin.system.exitProcess

fun Bot.updatePack() {
    val stickers = StickerSetConfiguration.STICKERS
    if (stickers.isEmpty()) return
    val firstSticker = stickers.first()

    val stickerSet: StickerSet? = getStickerSet(StickerSetConfiguration.STICKER_PACK_NAME)
        .handle { value, _ -> value }
        .join()

    if (stickerSet != null) {
        println("Using existing sticker set, deleting all stickers")
        stickerSet.stickers.forEach { deleteStickerFromSet(it.file_id).join() }
    } else {
        println("Creating new sticker set")
        val result = createNewStickerSet(
            StickerSetConfiguration.DEV_USER_ID,
            StickerSetConfiguration.STICKER_PACK_NAME,
            StickerSetConfiguration.TITLE,
            firstSticker.emojis,
            firstSticker.file,
        ).join()

        if (!result) {
            println("Failed")
            exitProcess(1)
        }
    }

    stickers.asSequence()
        .drop(if (stickerSet != null) 0 else 1)
        .forEach {
            addStickerToSet(
                StickerSetConfiguration.DEV_USER_ID,
                StickerSetConfiguration.STICKER_PACK_NAME,
                it.emojis,
                it.file
            ).join()
        }
}
