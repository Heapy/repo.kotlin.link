# repo.kotlin.link [![Build Status](https://travis-ci.com/Heapy/repo.kotlin.link.svg?branch=main)](https://travis-ci.com/Heapy/repo.kotlin.link)
Maven repository that proxy artifact request to one of know project-based maven repositories

```bash
# Run
docker run --restart=always -p 0.0.0.0:8092:8080 heapy/repo.kotlin.link:b1
```

## Gradle

```kotlin
repositories {
    maven {
        url = uri("https://repo.kotlin.link")
    }
}
```
