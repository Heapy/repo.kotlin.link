FROM bellsoft/liberica-openjre-alpine:25
RUN apk --no-cache add curl
COPY /build/install/repo /repo
ENTRYPOINT exec /repo/bin/repo
