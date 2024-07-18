package dev.alexzvn.inboxstorage.gui

import dev.alexzvn.inboxstorage.debug
import dev.alexzvn.inboxstorage.lang.t
import dev.alexzvn.inboxstorage.storage.Storage
import mc.obliviate.inventory.Gui
import mc.obliviate.inventory.Icon
import mc.obliviate.inventory.pagination.PaginationManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

class InboxInventory(player: Player) : Gui(player, "fis-inbox", "", 6) {
    private val pagination = PaginationManager(this)

    val prev = Icon(Material.BLUE_STAINED_GLASS_PANE).setName("Prev")
    val next = Icon(Material.GREEN_STAINED_GLASS_PANE).setName("Next")

    init {
        load()
        register()
        title = t("inbox")
    }

    private fun register() {
        next.onClick {
            if (!pagination.isLastPage) {
                pagination.goNextPage()
                pagination.update()
            }
        }

        prev.onClick {
            if (!pagination.isFirstPage) {
                pagination.goPreviousPage()
                pagination.update()
            }
        }
    }

    private fun load() {
        for (material in Storage.pull(player.name)) {
            val icon = InboxIcon(material)
            pagination.addItem(icon)
        }

        pagination.registerPageSlotsBetween(9, 53);
    }

    override fun onOpen(event: InventoryOpenEvent?) {
        fillRow(Icon(Material.GLASS_PANE), 0)
        addItem(0, prev)
        addItem(8, next)

        pagination.update()
    }

    override fun onClose(event: InventoryCloseEvent?) {
        val saved = pagination.items
            .filter { it is InboxIcon && !it.isPicked }
            .map { it.item }

        Storage.merge(player.name, saved)
    }
}