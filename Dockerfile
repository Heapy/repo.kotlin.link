# Container with application
FROM amazoncorretto:17.0.4
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
