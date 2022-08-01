package link.kotlin.repo

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.AllowedMethodsHandler
import io.undertow.server.handlers.BlockingHandler
import io.undertow.util.Methods
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager
import kotlin.concurrent.thread

fun main() {
    ApplicationFactory().start()
}

open class ApplicationFactory {
    open val json: Json by lazy {
        Json
    }

    open val httpClient: HttpClient by lazy {
        HttpClients
            .createMinimal(BasicHttpClientConnectionManager())
            .also {
                Runtime.getRuntime().addShutdownHook(thread(start = false) {
                    it.close()
                })
            }
    }

    open val configurationService: ConfigurationService by lazy {
        GithubConfigurationService(
            configProvider = configProvider,
        )
    }

    open val prometheusMeterRegistry: PrometheusMeterRegistry by lazy {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    open val metricsHandlerWrapper: MetricsHandlerWrapper by lazy {
        MetricsHandlerWrapper(
            prometheusMeterRegistry = prometheusMeterRegistry,
        )
    }

    open val delegatingHandler: DelegatingHandler by lazy {
        DelegatingHandler()
    }

    open val updateReposHandler: HttpHandler by lazy {
        AllowedMethodsHandler(
            BlockingHandler(
                metricsHandlerWrapper.wrap(
                    key = "update",
                    UpdateReposHandler(
                        updateNotificationService = updateNotificationService
                    )
                )
            ),
            Methods.POST,
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
                HomePageHandler(configurationService)
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

    open val rootHandlerProvider: RootHandlerProvider by lazy {
        RootHandlerProvider(
            configurationService = configurationService,
            homePageHandler = homePageHandler,
            updateReposHandler = updateReposHandler,
            healthCheckHandler = healthCheckHandler,
            prometheusHandler = prometheusHandler,
            notFoundHandler = notFoundHandler,
        )
    }

    open val updateNotificationService: UpdateNotificationService by lazy {
        UpdateNotificationService()
    }

    open val configProvider: () -> List<Config> = {
        val data = httpClient.execute(
            HttpGet("https://raw.githubusercontent.com/Heapy/repo.kotlin.link/main/src/main/resources/index.json"),
            BasicHttpClientResponseHandler()
        )

        json.decodeFromString(data)
    }

    open val undertow: Undertow by lazy {
        Undertow.builder()
            .addHttpListener(8080, "0.0.0.0", delegatingHandler)
            .build()
            .also { server ->
                Runtime.getRuntime().addShutdownHook(thread(start = false) {
                    server.stop()
                })
            }
    }

    open fun start() {
        updateNotificationService.subscribe {
            configurationService.update()
            delegatingHandler.setHandler(rootHandlerProvider.get())
        }
        updateNotificationService.publish()
        undertow.start()
    }
}
