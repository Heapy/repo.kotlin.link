# repo.kotlin.link [![Build Status](https://travis-ci.com/Heapy/repo.kotlin.link.svg?branch=main)](https://travis-ci.com/Heapy/repo.kotlin.link) ![Docker Image Version (latest semver)](https://img.shields.io/docker/v/heapy/repo.kotlin.link?sort=semver)
Maven repository that proxy artifact request to one of know project-based maven repositories

## Where to host your Maven artifacts

- [JetBrains Space](https://www.jetbrains.com/help/space/create-a-maven-repository.html) 10 GB of total storage, 50 GB of total data transfer per month
- [AWS S3](https://ruslan.ibragimov.by/2021/02/04/publish-maven-artifact-to-s3/) $0.0245 per GB storage, $0.09 per GB transfer. - Space equivalent of storage and transfer will cost about 5$ per month.
- [JitPack](https://jitpack.io): Always free for OSS, [more pricing options on their site](https://jitpack.io/private#subscribe).
- [GitHub Packages](https://docs.github.com/en/packages): free for public repositories. Private repositories: 500MB of storage, 1GB of data transfer per month for free, [more pricing options on their site](https://github.com/features/packages#pricing). Users have to issue personal token, and use it in credentatials section to consume github packages. – Doesn't work with **repo.kotlin.link**
- Please submit more options

## Gradle

```kotlin
repositories {
    maven {
        url = uri("https://repo.kotlin.link")
    }
}
```

## Deploy to Maven Central

repo.kotlin.link is great for quick experiments, and collaboration of couple independent projects under single repository url. I suggest you to use it in home projects, or in environment where you can cache dependencies in case if original dependency not longer available. But for enterprise level softwate you may like to use only maven central. Here some projects that may help to upload your artifacts to maven central:

- [gradle-nexus/publish-plugin](https://github.com/gradle-nexus/publish-plugin) - Gradle Plugin covering the whole releasing process to Maven Central
- [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin) - Gradle Plugin that tries to simplify setup process
- [saket/startship](https://github.com/saket/startship) - CLI tool for publishing

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
