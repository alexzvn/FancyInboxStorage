package dev.alexzvn.inboxstorage

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.apache.http.client.HttpResponseException
import org.apache.http.client.fluent.Request
import org.bukkit.Bukkit
import java.util.UUID

object PlayerUUID {

    val cache = mutableMapOf<String, UUID>()

    fun find(name: String): UUID {
        if (cache.containsKey(name)) {
            return cache[name]!!
        }

        if (Bukkit.getOnlineMode()) {
            Bukkit.getPlayer(name)?.run {
                return uniqueId.also { cache[name] = it }
            }

            for (player in Bukkit.getOfflinePlayers()) {
                if (player.name == name) {
                    return player.uniqueId.also { cache[name] = it }
                }
            }
        }

        generateOfflineUUID(name).also {
            return it.also { cache[name] = it }
        }
    }

    fun generateOfflineUUID(name: String): UUID = UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray())

    fun generateOnlineUUID(name: String): UUID? {
        val content = Request.Get("https://api.mojang.com/users/profiles/minecraft/$name")
            .execute()
            .returnContent()
            .asString()

        val body = Gson().fromJson(content, JsonObject::class.java)

        if (body.has("id")) {
            return dash(body.get("id").asString).also { cache[name] = it }
        }

        return null
    }

    fun dash(uuid: String): UUID = UUID.fromString(uuid.replace(
        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)".toRegex(), "$1-$2-$3-$4-$5" )
    )
}