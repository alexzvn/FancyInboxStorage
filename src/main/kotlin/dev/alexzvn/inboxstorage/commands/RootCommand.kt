package dev.alexzvn.inboxstorage.commands

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import dev.alexzvn.inboxstorage.FancyInboxStorage
import dev.alexzvn.inboxstorage.asyncTask
import dev.alexzvn.inboxstorage.gui.InboxInventory
import dev.alexzvn.inboxstorage.gui.MailingConfirmGui
import dev.alexzvn.inboxstorage.http.API
import dev.alexzvn.inboxstorage.http.dto.UploadItem
import dev.alexzvn.inboxstorage.nextTick
import dev.alexzvn.inboxstorage.storage.Storage
import org.apache.http.client.fluent.Async
import org.apache.http.entity.ContentType
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
            return
        }

        InboxInventory(sender).open()
    }

    @Command(name = "mail", desc = "mail current held an item to other player")
    fun mail(@Sender sender: CommandSender, recipient: String) {
        if (sender !is Player) {
            return
        }

        asyncTask {
            val player = Bukkit.getOfflinePlayer(recipient)
            if (! player.hasPlayedBefore() || player.name == null) {
                return@asyncTask sender.sendMessage("This player is not played before")
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

        API.client!!.post("/")
            .bodyString(UploadItem.from(item).json(), ContentType.APPLICATION_JSON)
            .execute()
            .handleResponse {
                val code = it.statusLine.statusCode
                val message = String(it.entity.content.readAllBytes())
                sender.sendMessage("($code) $message")
            }
    }

    @Command(name = "reload", desc = "Reload plugin")
    @Require("fis.admin.reload")
    fun reload(@Sender sender: CommandSender) {
        FancyInboxStorage.instance().reload()

        sender.sendMessage("plugin reloaded")
    }
}

