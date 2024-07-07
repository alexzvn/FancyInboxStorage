package dev.alexzvn.inboxstorage.storage

import com.google.gson.Gson
import dev.alexzvn.inboxstorage.FancyInboxStorage
import dev.alexzvn.inboxstorage.encode
import dev.alexzvn.inboxstorage.toBukkitObject
import org.bukkit.inventory.ItemStack
import java.io.File

object Storage {
    private val gson = Gson()

    fun set(player: String, stack: List<ItemStack>) {
        val items = stack.map { item -> item.encode() }.toTypedArray()

        StorageFile(player, "inboxes").write(gson.toJson(items))
    }

    fun get(player: String): MutableList<ItemStack> {
        val data = StorageFile(player, "inboxes").read()

        if (data.isNullOrBlank()) {
            return mutableListOf()
        }

        return data.run {
            val list = gson.fromJson(this, Array<String>::class.java)
            list.map { it.toBukkitObject() as ItemStack }.toMutableList()
        }
    }

    fun pull(player: String): MutableList<ItemStack> {
        return get(player).also { set(player, emptyList()) }
    }

    fun merge(player: String, items: List<ItemStack>) {
        val data = get(player).apply { addAll(items) }

        set(player, data)
    }

    class StorageFile(id: String, folder: String) {
        private val file = File(FancyInboxStorage.instance().dataFolder, "$folder/$id.dat")

        init {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        fun write(data: String) {
            file.writeText(data)
        }

        fun read(): String? {
            return file.readText().run {
                when (this.isBlank()) {
                    true -> null
                    false -> this
                }
            }
        }
    }
}