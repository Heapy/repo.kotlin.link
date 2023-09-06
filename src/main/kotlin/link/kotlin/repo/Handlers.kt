package link.kotlin.repo

import io.micrometer.prometheus.PrometheusMeterRegistry
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.PathHandler
import io.undertow.util.Headers
import io.undertow.util.StatusCodes
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.li
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.strong
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.util.function.Supplier

class HomePageHandler(
    private val configuration: Map<String, List<Repo>>,
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.statusCode = StatusCodes.OK
        exchange.responseHeaders.add(Headers.CONTENT_TYPE, "text/html; charset=utf-8")
        val page = createHTMLDocument()
            .html {
                lang = "en"
                head {
                    meta(charset = "utf-8")
                    meta {
                        name = "viewport"
                        content = "width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"
                    }
                    meta {
                        httpEquiv = "X-UA-Compatible"
                        content = "ie=edge"
                    }
                    title("repo.kotlin.link")
                }
                body {
                    h1 { +"repo.kotlin.link" }
                    p {
                        a {
                            href = "https://github.com/Heapy/repo.kotlin.link"
                            +"Github"
                        }
                    }
                    h2 { +"Hosted packages" }
                    ul {
                        configuration.forEach { (groupId, repos) ->
                            if (repos.size == 1) with(repos.single()) {
                                li {
                                    strong {
                                        +groupId
                                        artifactId?.let { +":$it" }
                                    }
                                    +": $repo"
                                }
                            } else with(repos) {
                                li {
                                    strong {
                                        +"$groupId:"
                                    }
                                    ul {
                                        forEach { repo ->
                                            li {
                                                strong {
                                                    +repo.groupId
                                                    repo.artifactId?.let { +":$it" }
                                                }
                                                +": ${repo.repo}"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .serialize(prettyPrint = true)
        exchange.responseSender.send(page)
    }
}

class RepoRedirectHandler(
    private val repos: List<Repo>,
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        val repo = repos
            .sortedByDescending { it.artifactId?.length }
            .find { it.pathRegex.matches(exchange.requestPath) }
            ?: return run {
                exchange.statusCode = StatusCodes.NOT_FOUND
            }

        val location = repo.repo + exchange.requestPath

        log.info("Request for [{}] redirected to [{}]", exchange.requestPath, location)

        exchange.responseHeaders.add(Headers.LOCATION, location)
        exchange.statusCode = StatusCodes.FOUND
    }

    companion object {
        private val log = LoggerFactory.getLogger(RepoRedirectHandler::class.java)
    }
}

@Serializable
data class Repo(
    val repo: String,
    val groupId: String,
    val artifactId: String? = null,
) {
    val pathRegex: Regex by lazy {
        Regex(buildString {
            append(
                groupId
                    .split(".")
                    .joinToString(prefix = "^\\/", separator = "\\/", postfix = "\\/")
            )
            artifactId?.let {
                append(artifactId.replace("*", "[a-b\\-]*"))
                append("\\/")
            }
            append(".*$")
        })
    }
}

class HealthCheckHandler : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.statusCode = StatusCodes.OK
        exchange.responseSender.send("""{"status":"ok"}""")
    }
}

class PrometheusHandler(
    private val prometheusMeterRegistry: PrometheusMeterRegistry,
) : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.responseHeaders.add(Headers.CONTENT_TYPE, "text/plain; version=0.0.4")
        exchange.responseSender.send(prometheusMeterRegistry.scrape())
    }
}

class RootHandlerProvider(
    private val configuration: Map<String, List<Repo>>,
    private val homePageHandler: HttpHandler,
    private val healthCheckHandler: HttpHandler,
    private val prometheusHandler: HttpHandler,
    private val notFoundHandler: HttpHandler,
    private val metricsHandlerWrapper: MetricsHandlerWrapper,
) : Supplier<HttpHandler> {
    override fun get(): HttpHandler {
        return PathHandler(10000)
            .addExactPath("/", homePageHandler)
            .addExactPath("/healthcheck", healthCheckHandler)
            .addExactPath("/metrics", prometheusHandler)
            .also { handler ->
                configuration.forEach {
                    val prefix = it.key.split(".").joinToString(prefix = "/", separator = "/", postfix = "/")
                    handler.addPrefixPath(
                        prefix,
                        metricsHandlerWrapper.wrap(
                            key = "redirect",
                            RepoRedirectHandler(it.value)
                        )
                    )
                }
            }
            .addPrefixPath("/", notFoundHandler)
    }
}

class NotFoundHandler : HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange) {
        log.info("Unknown path [${exchange.requestPath}]")
        exchange.statusCode = StatusCodes.NOT_FOUND
    }

    companion object {
        private val log = LoggerFactory.getLogger(NotFoundHandler::class.java)
    }
}

class MetricsHandlerWrapper(
    private val prometheusMeterRegistry: PrometheusMeterRegistry,
) {
    fun wrap(key: String, handler: HttpHandler): HttpHandler {
        return HttpHandler { exchange ->
            prometheusMeterRegistry
                .timer("http", "endpoint", key)
                .record(Runnable {
                    handler.handleRequest(exchange)
                })
        }
    }
}
