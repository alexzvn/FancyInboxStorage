package dev.alexzvn.inboxstorage.gui

import dev.alexzvn.inboxstorage.nextTick
import dev.alexzvn.inboxstorage.storage.Storage
import mc.obliviate.inventory.Gui
import mc.obliviate.inventory.Icon
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack

class MailingConfirmGui(player: Player, private val recipient: String) : Gui(player, "fis-mailing-confirm", "title", 1) {

    private val confirm = Icon(Material.GREEN_WOOL).setName("Confirm")
    private val cancel = Icon(Material.RED_WOOL).setName("Cancel")
    private val item = player.inventory.itemInMainHand.also {
        player.inventory.setItemInMainHand(null)
    }

    private var accept = false


    init {
        confirm.onClick {
            accept = true
            nextTick { player.closeInventory() }
        }

        cancel.onClick {
            nextTick { player.closeInventory() }
        }
    }

    override fun onOpen(event: InventoryOpenEvent?) {
        if (item.type == Material.AIR) {
            event?.isCancelled = true
            return
        }

        fillRow(Icon(Material.GLASS_PANE), 0)

        addItem(0, confirm)
        addItem(4, Icon(item))
        addItem(8, cancel)
    }

    override fun onClose(event: InventoryCloseEvent?) {
        if (accept) {
            return Storage.merge(recipient, listOf(item))
        }

        if (player.inventory.itemInMainHand.type == Material.AIR) {
            return player.inventory.setItemInMainHand(item)
        }

        try {
            player.inventory.addItem(item)
        } catch (e: IllegalArgumentException) {
            player.sendMessage("Vì kho đồ của bạn đầy nên item đã chuyển vào trong inbox")
            Storage.merge(player.name, listOf(item))
        }
    }
}