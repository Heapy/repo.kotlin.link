# repo.kotlin.link [![Build Status](https://travis-ci.com/Heapy/repo.kotlin.link.svg?branch=main)](https://travis-ci.com/Heapy/repo.kotlin.link)
Maven repository that proxy artifact request to one of know project-based maven repositories

## Gradle

```kotlin
repositories {
    maven {
        url = uri("https://repo.kotlin.link")
    }
}
```

## How to add your repository

Create PR with changes to [index.yml](https://github.com/Heapy/repo.kotlin.link/blob/main/src/main/resources/index.yml):
```yaml
"https://dl.bintray.com/heapy/heap-dev": # repository url without / at the end
  - "io.heapy.komodo" # list of allowed groups
```

## Run own instance

```bash
# Run
docker run --restart=always -p 0.0.0.0:8092:8080 heapy/repo.kotlin.link:b2
```
