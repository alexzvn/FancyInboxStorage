package dev.alexzvn.inboxstorage.http

import org.apache.commons.lang.StringUtils
import org.apache.http.client.fluent.Request
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContextBuilder
import javax.net.ssl.SSLContext


class SimpleAPI(url: String, private var token: String?) {
    val endpoint: String = StringUtils.stripEnd(url, "/")
    var headers = mapOf<String, String>()

    val bearer: String
        get() = "Bearer $token"

    constructor(url: String) : this(url, null) {}

    private fun join(uri: String) = "$endpoint/${StringUtils.strip(uri, "/")}"
    private fun wrap(request: Request) = request.let {
        var req = it

        if (token != null) {
            req = req.addHeader("Authorization", bearer)
        }

        for (header in headers.entries) {
            req = req.addHeader(header.key, header.value)
        }

        req
    }

    fun get(uri: String) = wrap(Request.Get(join(uri)))
    fun post(uri: String) = wrap(Request.Post(join(uri)))
    fun put(uri: String) = wrap(Request.Put(join(uri)))
    fun delete(uri: String) = wrap(Request.Delete(join(uri)))
    fun patch(uri: String) = wrap(Request.Patch(join(uri)))
    fun head(uri: String) = wrap(Request.Head(join(uri)))
    fun options(uri: String) = wrap(Request.Options(join(uri)))
}