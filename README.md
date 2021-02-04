# repo.kotlin.link [![Build Status](https://travis-ci.com/Heapy/repo.kotlin.link.svg?branch=main)](https://travis-ci.com/Heapy/repo.kotlin.link) ![Docker Image Version (latest semver)](https://img.shields.io/docker/v/heapy/repo.kotlin.link?sort=semver)
Maven repository that proxy artifact request to one of know project-based maven repositories

## Where to host your Maven artifacts

- [JetBrains Space](https://www.jetbrains.com/help/space/create-a-maven-repository.html) 10 GB of total storage, 50 GB of total data transfer per month
- [AWS S3](https://ruslan.ibragimov.by/2021/02/04/publish-maven-artifact-to-s3/)
- [JitPack](https://jitpack.io): Always free for OSS, [more pricing options on their site](https://jitpack.io/private#subscribe).
- [GitHub Packages](https://docs.github.com/en/packages): free for public repositories. Private repositories: 500MB of storage, 1GB of data transfer per month for free, [more pricing options on their site](https://github.com/features/packages#pricing).
- Please submit more options

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
"https://dl.bintray.com/heapy/heap-dev": # repository url
  - "io.heapy.komodo" # list of allowed groups
```

## Run own instance

```bash
# Run
docker run --detach --name repo.kotlin.link --restart=always -p 0.0.0.0:8092:8080 heapy/repo.kotlin.link:b7
```

## Update instance

```bash
docker pull heapy/repo.kotlin.link:b7
docker stop repo.kotlin.link
docker rm repo.kotlin.link
docker run --detach --name repo.kotlin.link --restart=always -p 0.0.0.0:8092:8080 heapy/repo.kotlin.link:b7
```
