package dev.alexzvn.inboxstorage.http.dto

import com.google.gson.Gson
import com.loohp.interactivechat.api.InteractiveChatAPI
import com.loohp.interactivechatdiscordsrvaddon.api.InteractiveChatDiscordSrvAddonAPI
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils
import dev.alexzvn.inboxstorage.FancyInboxStorage
import dev.alexzvn.inboxstorage.displayName
import dev.alexzvn.inboxstorage.encode
import dev.alexzvn.inboxstorage.serverHasPlugin
import org.apache.commons.codec.digest.DigestUtils
import org.apache.http.HttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ByteArrayBody
import org.apache.http.entity.mime.content.StringBody
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

fun String.body() = StringBody(this, ContentType.DEFAULT_TEXT)

class UploadItem {
    lateinit var name: String
    lateinit var material: String
    lateinit var data: String
    lateinit var sha1: String
    var preview: BufferedImage? = null
    var icon: BufferedImage? = null
    var customModelData: Int? = null

    fun generateImagePreview(item: ItemStack, player: Player) {
        if (!serverHasPlugin("InteractiveChat") || !serverHasPlugin("InteractiveChatDiscordSrvAddon")) {
            return
        }

        InteractiveChatAPI.getICPlayer(player).apply {
            icon = ImageGeneration.getItemStackImage(item, this)
            preview = DiscordItemStackUtils.getToolTip(item, this).let {
                ImageGeneration.getToolTipImage(it.components)
            }
        }
    }

    fun json() = Gson().toJson(this)

    fun formData(): HttpEntity {
        return MultipartEntityBuilder.create().run {
            setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
            addPart("name", name.body())
            addPart("material", material.body())
            addPart("data", data.body())
            addPart("sha1", sha1.body())

            if (customModelData != null) {
                addPart("customModelData", customModelData.toString().body())
            }

            if (preview != null) {
                val file = ByteArrayBody(
                    ImageUtils.toArray(preview),
                    ContentType.create("image/png"),
                    "preview.png"
                )

                addPart("preview", file)
            }

            if (icon != null) {
                val file = ByteArrayBody(
                    ImageUtils.toArray(preview),
                    ContentType.create("image/png"),
                    "icon.png"
                )
            }

            build()
        }
    }

    companion object {
        fun from(item: ItemStack): UploadItem {
            return UploadItem().apply {
                name = when (item.itemMeta?.displayName.isNullOrBlank()) {
                    true -> item.type.displayName()
                    else -> item.itemMeta!!.displayName
                }

                when(item.itemMeta?.hasCustomModelData() == true) {
                    true -> customModelData = item.itemMeta?.customModelData
                    else -> {}
                }

                material = item.type.name
                data = item.encode()
                sha1 = DigestUtils.sha1Hex(data)
            }
        }
    }
}