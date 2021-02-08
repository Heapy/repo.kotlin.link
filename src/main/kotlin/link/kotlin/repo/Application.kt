package link.kotlin.repo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.BlockingHandler
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
            yamlMapper = yamlMapper
        )
    }

    open val delegatingHandler: DelegatingHandler by lazy {
        DelegatingHandler()
    }

    open val updateReposHandler: HttpHandler by lazy {
        BlockingHandler(
            UpdateReposHandler(
                updateNotificationService = updateNotificationService
            )
        )
    }

    open val homePageHandler: HttpHandler by lazy {
        HomePageHandler(configurationService)
    }

    open val rootHandlerProvider: RootHandlerProvider by lazy {
        RootHandlerProvider(
            configurationService = configurationService,
            homePageHandler = homePageHandler,
            updateReposHandler = updateReposHandler
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
