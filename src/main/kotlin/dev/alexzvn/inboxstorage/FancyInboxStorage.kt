package dev.alexzvn.inboxstorage

import com.jonahseguin.drink.Drink
import dev.alexzvn.inboxstorage.commands.RootCommand
import dev.alexzvn.inboxstorage.http.API
import mc.obliviate.inventory.InventoryAPI
import org.bukkit.plugin.java.JavaPlugin

class FancyInboxStorage : JavaPlugin() {

    companion object {
        private var instance: FancyInboxStorage? = null

        public fun instance() = instance ?: throw IllegalArgumentException("FancyInboxStorage is disabled")
    }

    init { instance = this }

    override fun onEnable() {
        val command = Drink.get(this).apply {
            register(RootCommand(), "fancyinboxstorage", "fis")
        }

        InventoryAPI(this).init()
        command.registerCommands()
        saveDefaultConfig()
        API.setup(config)
    }

    fun reload() {
        reloadConfig()
        API.setup(config)
    }
}