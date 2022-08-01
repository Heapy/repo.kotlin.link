package link.kotlin.repo

import kotlinx.serialization.decodeFromString

fun main() {
     LocalApplicationFactory().start()
}

open class LocalApplicationFactory : ApplicationFactory() {
    override val configProvider: () -> List<Config> = {
        val data = LocalApplicationFactory::class.java.classLoader
            .getResourceAsStream("index.json")!!
            .reader()
            .readText()

        json.decodeFromString(data)
    }
}
