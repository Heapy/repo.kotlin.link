package link.kotlin.repo

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.ResponseCodeHandler
import io.undertow.util.Headers
import io.undertow.util.StatusCodes
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

fun main() {
    val mapper = ObjectMapper(YAMLFactory())

    val config = RepoRedirectHandler::class.java.classLoader
        .getResourceAsStream("index.yml")!!
        .use { mapper.readValue(it, object : TypeReference<Map<String, List<String>>>() {}) }
        .flatMap { it.value.map { group -> group to it.key } }
        .toMap()

    Undertow.builder()
        .addHttpListener(8080, "0.0.0.0", RepoRedirectHandler(config))
        .build()
        .also { server ->
            server.start()
            Runtime.getRuntime().addShutdownHook(thread(start = false) {
                server.stop()
            })
        }
}

class RepoRedirectHandler(
    private val config: Map<String, String>
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        val parts = exchange.requestPath.split('/')
            .filter(String::isNotEmpty)

        val builder = StringBuilder()
        var first = true

        val url = parts.firstNotNullOrNull { part ->
            if (first) first = false else builder.append(".")
            builder.append(part)
            config[builder.toString()]
        }

        url ?: return ResponseCodeHandler.HANDLE_404.handleRequest(exchange)

        val location = url + exchange.requestPath

        LOGGER.error("Request [{}] to redirected to [{}]", exchange.requestPath, location)

        exchange.responseHeaders.add(Headers.LOCATION, location)
        exchange.statusCode = StatusCodes.FOUND
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RepoRedirectHandler::class.java)
    }

}

inline fun <T, R> Iterable<T>.firstNotNullOrNull(transform: (T) -> R?): R? {
    for (element in this) transform(element)?.let { return it }
    return null
}
