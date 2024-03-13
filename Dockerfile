FROM bellsoft/liberica-openjre-alpine:21.0.2
RUN apk --no-cache add curl
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
