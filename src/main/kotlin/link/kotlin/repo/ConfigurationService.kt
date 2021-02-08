package link.kotlin.repo

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils

interface ConfigurationService {
    val configuration: Map<String, String>
    fun update()
}

class GithubConfigurationService(
    private val httpClient: HttpClient,
    private val yamlMapper: ObjectMapper
) : ConfigurationService {
    @Volatile
    override var configuration: Map<String, String> = mapOf()

    override fun update() {
        val response = httpClient.execute(HttpGet(CONFIG))
        val data = EntityUtils.toString(response.entity)

        val type = object : TypeReference<Map<String, List<String>>>() {}
        configuration = yamlMapper.readValue(data, type)
            .flatMap { it.value.map { group -> group to it.key.removeSuffix("/") } }
            .toMap()
    }
}

private const val CONFIG =
    "https://raw.githubusercontent.com/Heapy/repo.kotlin.link/main/src/main/resources/index.yml"

class LocalConfigurationService(
    private val yamlMapper: ObjectMapper
) : ConfigurationService {
    @Volatile
    override var configuration: Map<String, String> = mapOf()

    override fun update() {
        val type = object : TypeReference<Map<String, List<String>>>() {}
        configuration = LocalConfigurationService::class.java.classLoader
            .getResourceAsStream("index.yml")!!
            .use { yamlMapper.readValue(it, type) }
            .flatMap { it.value.map { group -> group to it.key.removeSuffix("/") } }
            .toMap()
    }
}
