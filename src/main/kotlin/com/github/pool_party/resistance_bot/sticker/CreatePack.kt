package com.github.pool_party.resistance_bot.sticker

import com.elbekD.bot.Bot
import com.elbekD.bot.types.StickerSet
import com.github.pool_party.resistance_bot.sticker.StickerSetConfiguration.file
import com.github.pool_party.resistance_bot.utils.resolveName
import mu.KotlinLogging
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun Bot.updateSet() {
    val setName = resolveName(StickerSetConfiguration.STICKER_SET_NAME)
    val stickers = StickerSetConfiguration.STICKERS
    if (stickers.isEmpty()) return
    val firstSticker = stickers.first()

    val stickerSet: StickerSet? = getStickerSet(setName)
        .handle { value, _ -> value }
        .join()

    if (stickerSet != null) {
        logger.info("Using existing sticker set, deleting all stickers")
        stickerSet.stickers.forEach { deleteStickerFromSet(it.file_id).join() }
    } else {
        logger.info("Creating new sticker set")
        val result = createNewStickerSet(
            StickerSetConfiguration.DEV_USER_ID,
            setName,
            StickerSetConfiguration.TITLE,
            firstSticker.emojis,
            firstSticker.file,
        ).join()

        if (!result) {
            println("Failed")
            exitProcess(1)
        }
    }

    logger.info("Adding the rest of the stickers")
    stickers.asSequence()
        .drop(if (stickerSet != null) 0 else 1)
        .forEach {
            addStickerToSet(StickerSetConfiguration.DEV_USER_ID, setName, it.emojis, it.file).join()
        }
    logger.info("Sticker pack successfully updated")
}
