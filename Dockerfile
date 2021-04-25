# Container with application
FROM amazoncorretto:11.0.11
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
