package dev.alexzvn.inboxstorage.http

import org.apache.commons.lang.StringUtils
import org.apache.http.client.fluent.Request

class SimpleAPI(url: String, private var token: String?) {
    val endpoint: String = StringUtils.stripEnd(url, "/")

    val bearer: String
        get() = "Bearer $token"

    constructor(url: String) : this(url, null) {}


    private fun join(uri: String) = "$endpoint/${StringUtils.strip(uri, "/")}"
    private fun wrap(request: Request) = request.also {
        if (token != null) {
            it.addHeader("Authorization", bearer)
        }
    }

    fun get(uri: String) = wrap(Request.Get(join(uri)))
    fun post(uri: String) = wrap(Request.Post(join(uri)))
    fun put(uri: String) = wrap(Request.Put(join(uri)))
    fun delete(uri: String) = wrap(Request.Delete(join(uri)))
    fun patch(uri: String) = wrap(Request.Patch(join(uri)))
    fun head(uri: String) = wrap(Request.Head(join(uri)))
    fun options(uri: String) = wrap(Request.Options(join(uri)))
}