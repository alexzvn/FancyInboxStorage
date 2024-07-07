package dev.alexzvn.inboxstorage.commands

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import dev.alexzvn.inboxstorage.gui.InboxInventory
import dev.alexzvn.inboxstorage.storage.Storage
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
}

