# Container with application
FROM amazoncorretto:11.0.10
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
