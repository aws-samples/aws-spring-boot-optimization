FROM ghcr.io/graalvm/graalvm-ce:22 AS build-aot
LABEL maintainer="Sascha Möllering <smoell@amazon.de>"

RUN microdnf install -y unzip zip

RUN \
    curl -s "https://get.sdkman.io" | bash; \
    source "$HOME/.sdkman/bin/sdkman-init.sh"; \
    sdk install maven; \
    gu install native-image;

COPY ./pom.xml ./pom.xml
COPY src ./src/

ENV MAVEN_OPTS='-Xmx8g'

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn -Dmaven.test.skip=true clean package

FROM public.ecr.aws/lts/ubuntu:22.04
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
COPY --from=build-aot /target/CustomerService /
EXPOSE 8080

CMD ["./CustomerService", "-Dspring.profiles.active=prod"]