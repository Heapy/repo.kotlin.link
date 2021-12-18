package link.kotlin.repo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.AllowedMethodsHandler
import io.undertow.server.handlers.BlockingHandler
import io.undertow.util.Methods
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import kotlin.concurrent.thread

fun main() {
    ApplicationFactory().start()
    // To test with local config
    // LocalApplicationFactory().start()
}

open class ApplicationFactory {
    open val yamlMapper: ObjectMapper by lazy {
        ObjectMapper(YAMLFactory())
    }

    open val httpClient: HttpClient by lazy {
        HttpClients.createDefault().also {
            Runtime.getRuntime().addShutdownHook(thread(start = false) {
                it.close()
            })
        }
    }

    open val configurationService: ConfigurationService by lazy {
        GithubConfigurationService(
            httpClient = httpClient,
            yamlMapper = yamlMapper,
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

open class LocalApplicationFactory : ApplicationFactory() {
    override val configurationService: ConfigurationService by lazy {
        LocalConfigurationService(yamlMapper)
    }
}
