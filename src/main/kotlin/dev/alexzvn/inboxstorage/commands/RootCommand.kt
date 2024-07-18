package dev.alexzvn.inboxstorage.commands

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import dev.alexzvn.inboxstorage.*
import dev.alexzvn.inboxstorage.gui.InboxInventory
import dev.alexzvn.inboxstorage.gui.MailingConfirmGui
import dev.alexzvn.inboxstorage.http.API
import dev.alexzvn.inboxstorage.http.dto.UploadItem
import dev.alexzvn.inboxstorage.lang.t
import dev.alexzvn.inboxstorage.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RootCommand {

    @Command(name = "", desc = "Fancy inbox command")
    fun root(@Sender sender: CommandSender) {
        sender.sendMessage("Use /inbox")

        if (sender is Player) {
            Storage.set(sender.name, listOf(ItemStack(Material.APPLE)))
        }
    }

    @Command(name = "inbox", aliases = ["inbox"], desc = "View inbox")
    fun inbox(@Sender sender: CommandSender) {
        if (sender !is Player) {
            return sender.sendMessage(t("command.require.player"))
        }

        InboxInventory(sender).open()
    }

    @Command(name = "mail", desc = "mail current held an item to other player")
    fun mail(@Sender sender: CommandSender, recipient: String) {
        if (sender !is Player) {
            return sender.sendMessage(t("command.require.player"))
        }

        val player = Bukkit.getOfflinePlayer(PlayerUUID.find(recipient))

        if (sender.uniqueId == player.uniqueId) {
            return sender.sendMessage(t("command.mail.invalid.self"))
        }

        asyncTask {
            if (! player.hasPlayedBefore() || player.name == null) {
                return@asyncTask sender.sendMessage(t("command.mail.invalid.not-found"))
            }

            nextTick {
                MailingConfirmGui(sender, player.name!!).open()
            }
        }
    }

    @Command(name = "upload", desc = "Upload current item to cloud")
    @Require("fis.admin.upload")
    fun upload(@Sender sender: CommandSender) {
        if (sender !is Player) {
            return
        }

        val item = sender.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            return
        }

        val uploadItem = UploadItem.from(item).apply {
            generateImagePreview(item, sender)
        }

        asyncTask {
            API.client.post("/upload")
            .body(uploadItem.formData())
            .execute()
            .handleResponse {
                val code = it.statusLine.statusCode
                val message = String(it.entity.content.readAllBytes())

                try {
                    val body = Gson().fromJson(message, JsonObject::class.java)

                    when (body.has("message")) {
                        true -> sender.sendMessage("($code) ${body.get("message").asString}".mcText())
                        else -> sender.sendMessage("($code) $message")
                    }


                } catch (e: Exception) {
                    sender.sendMessage("($code) $message")
                }
            }
        }
    }

    @Command(name = "reload", desc = "Reload plugin")
    @Require("fis.admin.reload")
    fun reload(@Sender sender: CommandSender) {
        FancyInboxStorage.instance().reload()

        sender.sendMessage("plugin reloaded")
    }
}

