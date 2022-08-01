package link.kotlin.repo

import kotlinx.serialization.Serializable

interface ConfigurationService {
    val configuration: Map<String, Config>
    fun update()
}

class GithubConfigurationService(
    private val configProvider: () -> List<Config>,
) : ConfigurationService {
    @Volatile
    override var configuration: Map<String, Config> = mapOf()

    override fun update() {
        configuration = configProvider()
            .associateBy { it.groupId }
    }
}

@Serializable
data class Config(
    val repo: String,
    val groupId: String,
    val artifactId: String? = null,
)
