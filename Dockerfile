# Container with application
FROM bellsoft/liberica-openjre-alpine:17.0.5
RUN apk --no-cache add curl
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
