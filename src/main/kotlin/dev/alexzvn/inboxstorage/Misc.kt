package dev.alexzvn.inboxstorage

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

fun String.mcText(): String = ChatColor.translateAlternateColorCodes('&', this)

fun World.copyRecursiveTo(folder: File) {
    Files.walk(this.worldFolder.toPath()).forEach {
        it.getName(it.nameCount - 1).toString() == "session.lock" && return@forEach

        val target = File(folder, it.toString().replace(worldFolder.toString(), ""))
        Files.copy(it, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}

fun File.copyRecursiveTo(folder: File) {
    Files.walk(this.toPath()).forEach {
        val target = File(folder, it.toString().replace(toString(), ""))
        Files.copy(it, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}

fun Material.displayName() = this.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

/**
 * Convert a block to base64 string
 */
fun ItemStack.encode(): String = bukkitEncodeBase64(this)

/**
 * Convert a base64 string to bukkit object
 */
fun String.toBukkitObject(): Any = bukkitDecodeBase64(this)

fun bukkitEncodeBase64(obj: Any): String {
    val rawIO = ByteArrayOutputStream()
    val bukkitIO = BukkitObjectOutputStream(rawIO)

    bukkitIO.writeObject(obj)

    val buffer = rawIO.toByteArray()

    return Base64.getEncoder().encodeToString(buffer) ?: "error"
}

fun bukkitDecodeBase64(base64: String): Any {
    val buffer = Base64.getDecoder().decode(base64)
    val io = BukkitObjectInputStream(ByteArrayInputStream(buffer))

    return io.readObject()
}

/**
 * Schedule a task to run after delay by Bukkit scheduler
 */
fun delayTask(delay: Long, task: () -> Unit) {
    Bukkit.getScheduler().runTaskLater(FancyInboxStorage.instance(), task, delay)
}

/**
 * Run async task by Bukkit scheduler
 */
fun asyncTask(task: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(FancyInboxStorage.instance(), task)
}


/**
 * Run task in next server tick by Bukkit scheduler
 */
fun nextTick(task: () -> Unit) {
    Bukkit.getScheduler().runTask(FancyInboxStorage.instance(), task)
}

fun asyncDelayTask(delay: Long, task: () -> Unit) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(FancyInboxStorage.instance(), task, delay)
}

/**
 * Register a listener to Bukkit event system
 */
fun Listener.bind() {
    Bukkit.getPluginManager().registerEvents(this, FancyInboxStorage.instance())
}

/**
 * Unregister a listener from Bukkit event system
 */
fun Listener.unbind() {
    HandlerList.unregisterAll(this)
}


fun String.debug(prefix: String): String {
    return prefix + this.debug()
}

fun String.debug() {
    FancyInboxStorage.instance().logger.info(this)
}

fun Player.heal() {
    health = getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
    foodLevel = 20
    saturation = 20f
}

/**
 * Using PlaceholderAPI to replace placeholder
 *
 * @param player Player to replace placeholder
 * @return String with placeholder replaced by PlaceholderAPI otherwise return colored string
 */
fun String.placeholder(player: Player): String {
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
        return this.mcText()

    return PlaceholderAPI.setPlaceholders(player, this).mcText()
}