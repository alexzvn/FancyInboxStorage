package dev.alexzvn.inboxstorage.http

import dev.alexzvn.inboxstorage.debug
import org.bukkit.configuration.file.FileConfiguration

object API {
    var client: SimpleAPI? = null

    fun setup(config: FileConfiguration) {
        val token = config.getString("api.secret")
        val endpoint = config.getString("api.endpoint")

        val headers = config.getConfigurationSection("api.headers").let {
            if (it == null) {
                "header is null".debug()
                return@let mapOf()
            }

            mutableMapOf<String, String>().apply {
                for (key in it.getKeys(true)) {
                    set(key, it.getString(key, "") as String)
                }
            }
        }

        when (endpoint != null) {
            true -> this.client = SimpleAPI(endpoint, token).also { it.headers = headers }
            false -> "Endpoint client is not configured".debug()
        }
    }
}