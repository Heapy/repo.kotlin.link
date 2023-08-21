FROM bellsoft/liberica-openjre-alpine:17.0.8
RUN apk --no-cache add curl
COPY /build/install/repo /repo
ENTRYPOINT /repo/bin/repo
