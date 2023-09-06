package link.kotlin.repo

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.AllowedMethodsHandler
import io.undertow.server.handlers.BlockingHandler
import io.undertow.util.Methods
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread

fun main() {
    ApplicationFactory().start()
}

open class ApplicationFactory {
    open val json: Json by lazy {
        Json
    }

    open val configuration: Map<String, List<Repo>> by lazy {
        val data = ApplicationFactory::class.java.classLoader
            .getResourceAsStream("index.json")!!
            .reader()
            .readText()

        json.decodeFromString<List<Repo>>(data)
            .groupBy { it.groupId }
    }

    open val prometheusMeterRegistry: PrometheusMeterRegistry by lazy {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    open val metricsHandlerWrapper: MetricsHandlerWrapper by lazy {
        MetricsHandlerWrapper(
            prometheusMeterRegistry = prometheusMeterRegistry,
        )
    }

    open val healthCheckHandler: HttpHandler by lazy {
        AllowedMethodsHandler(
            metricsHandlerWrapper.wrap(
                key = "healthcheck",
                HealthCheckHandler()
            ),
            Methods.GET,
        )
    }

    open val homePageHandler: HttpHandler by lazy {
        AllowedMethodsHandler(
            metricsHandlerWrapper.wrap(
                key = "home",
                HomePageHandler(configuration)
            ),
            Methods.GET,
        )
    }

    open val prometheusHandler: HttpHandler by lazy {
        AllowedMethodsHandler(
            BlockingHandler(
                metricsHandlerWrapper.wrap(
                    key = "metrics",
                    PrometheusHandler(
                        prometheusMeterRegistry = prometheusMeterRegistry
                    )
                ),
            ),
            Methods.GET,
        )
    }

    open val notFoundHandler: HttpHandler by lazy {
        metricsHandlerWrapper.wrap(
            key = "metrics",
            NotFoundHandler()
        )
    }

    open val rootHandler: HttpHandler by lazy {
        RootHandlerProvider(
            configuration = configuration,
            homePageHandler = homePageHandler,
            healthCheckHandler = healthCheckHandler,
            prometheusHandler = prometheusHandler,
            notFoundHandler = notFoundHandler,
            metricsHandlerWrapper = metricsHandlerWrapper,
        ).get()
    }

    open val undertow: Undertow by lazy {
        Undertow.builder()
            .addHttpListener(8080, "0.0.0.0", rootHandler)
            .build()
            .also { server ->
                Runtime.getRuntime().addShutdownHook(thread(start = false) {
                    server.stop()
                })
            }
    }

    open fun start() {
        undertow.start()
    }
}
