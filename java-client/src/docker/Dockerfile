FROM bellsoft/liberica-openjdk-alpine-musl:21.0.3-10-cds@sha256:e15432895286badf76a372bdc7f88400f57bfea63d384de162be13c818b3b495

ADD distDockerTar.tar /
COPY docker-entrypoint.sh /opt/docker-entrypoint.sh
RUN chmod +x /opt/docker-entrypoint.sh
COPY certs /opt/certs/

ENV CERTIFICATES_DIRECTORY=/opt/certs
ENV DATABASE_HOST=postgres-mtls
ENV DATABASE_PORT=5432

RUN apk --update-cache --no-cache add curl bash "libssl3>3.1.4-r0" "libcrypto3>3.1.4-r0"

ENTRYPOINT ["/opt/docker-entrypoint.sh"]