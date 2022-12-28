# Container with application
FROM bellsoft/liberica-openjre-alpine:17.0.5
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
