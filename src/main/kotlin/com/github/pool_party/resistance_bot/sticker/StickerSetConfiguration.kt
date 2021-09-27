package com.github.pool_party.resistance_bot.sticker

import com.github.pool_party.resistance_bot.Configuration.Stickers
import com.github.pool_party.resistance_bot.utils.ConfigurationUtils.long
import com.github.pool_party.resistance_bot.utils.ConfigurationUtils.string
import java.io.File
import kotlin.reflect.full.memberProperties

object StickerSetConfiguration {

    val STICKERS = Stickers::class.memberProperties.mapNotNull { it.get(Stickers) as? AssetSticker }

    const val TITLE = "@ResistanceOnlineBot Stickers"

    val DEV_USER_ID by long()

    val STICKER_PACK_NAME by string()

    val AssetSticker.file
        get() = File("assets/stickers/$fileName.jpg")
}
