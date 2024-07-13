package dev.alexzvn.inboxstorage.http.dto

import com.google.gson.Gson
import dev.alexzvn.inboxstorage.displayName
import dev.alexzvn.inboxstorage.encode
import org.apache.commons.codec.digest.DigestUtils
import org.bukkit.inventory.ItemStack

class UploadItem {
    lateinit var name: String
    lateinit var material: String
    lateinit var data: String
    lateinit var sha1: String
    var customModelData: Int? = null

    fun json() = Gson().toJson(this)

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