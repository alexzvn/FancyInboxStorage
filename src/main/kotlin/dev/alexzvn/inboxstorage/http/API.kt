package dev.alexzvn.inboxstorage.http

import org.bukkit.configuration.file.FileConfiguration

object API {
    var uploader: SimpleAPI? = null
    var repository: SimpleAPI? = null

    fun setup(config: FileConfiguration) {

        val token = config.getString("api.secret")
        val repository = config.getString("api.repository")
        val uploader = config.getString("api.uploader")

        when (uploader != null) {
            true -> this.uploader = SimpleAPI(uploader, token)
            false -> this.uploader = null
        }

        when (repository != null) {
            true -> this.repository = SimpleAPI(repository, token)
            false -> this.repository = null
        }
    }
}