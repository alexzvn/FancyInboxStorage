package dev.alexzvn.inboxstorage.gui

import dev.alexzvn.inboxstorage.debug
import mc.obliviate.inventory.Icon
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class InboxIcon(item: ItemStack) : Icon(item) {
    private var origin: ItemStack? = null
    private var isObtained = false

    val isPicked: Boolean
        get() = isObtained

    init {
        origin = this.item

        onClick {

            it.isCancelled = it.cursor?.type != Material.AIR || isObtained

            if (!it.isCancelled) {
                isObtained = true
            }
        }
    }
}