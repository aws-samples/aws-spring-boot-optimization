FROM ghcr.io/graalvm/graalvm-ce:22.3.0 AS build-aot
LABEL maintainer="Sascha Möllering <smoell@amazon.de>"

RUN microdnf install -y unzip zip

RUN \
    curl -s "https://get.sdkman.io" | bash; \
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh; \
    sdk install maven;"

COPY ./pom.xml ./pom.xml
COPY src ./src/

ENV MAVEN_OPTS='-Xmx8g'

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && mvn -Dmaven.test.skip=true clean package -Pnative && cd target && unzip CustomerService-0.0.1.jar"

#FROM gcr.io/distroless/static-debian11

FROM public.ecr.aws/lts/ubuntu:22.04

COPY --from=build-aot /app/target/CustomerService /

EXPOSE 8080
#USER nonroot

CMD ["./CustomerService", "-Dspring.profiles.active=prod"]
