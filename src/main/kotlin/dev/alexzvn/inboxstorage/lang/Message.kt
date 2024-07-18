package dev.alexzvn.inboxstorage.lang

import dev.alexzvn.inboxstorage.mcText
import org.bukkit.configuration.file.FileConfiguration

object Message {
    lateinit var config: FileConfiguration

    fun setup(config: FileConfiguration) {
        this.config = config
    }

    fun get(key: String) = config.getString(key)?.mcText()
    fun get(key: String, default: String) = get(key) ?: default.mcText()
}

/**
 * Get message from key
 */
fun t(key: String) = Message.get(key) ?: "$key (null)"
fun t(key: String, default: String) = Message.get(key, default)