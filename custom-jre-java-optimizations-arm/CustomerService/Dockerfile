FROM public.ecr.aws/lts/ubuntu:22.04 AS builder
LABEL maintainer="Sascha Möllering <smoell@amazon.de>"

RUN apt-get update --fix-missing && apt-get install zip curl -y

RUN \
    curl -s "https://get.sdkman.io" | bash; \
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh; \
    sdk install java 17.0.5-amzn; \
    sdk install maven;"

COPY ./pom.xml ./pom.xml
COPY src ./src/

ENV MAVEN_OPTS='-Xmx6g'

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && mvn -Dmaven.test.skip=true clean package && cd target && unzip CustomerService-0.0.1.jar"

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && jdeps --ignore-missing-deps \
    --multi-release 17 --print-module-deps \
    --class-path target/BOOT-INF/lib/* \
    target/CustomerService-0.0.1.jar > jre-deps.info"

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && export JAVA_TOOL_OPTIONS=\"-Djdk.lang.Process.launchMechanism=vfork\" && \
    jlink --verbose --compress 2 --strip-java-debug-attributes \
    --no-header-files --no-man-pages --output custom-jre \
    --add-modules $(cat jre-deps.info)"

FROM public.ecr.aws/lts/ubuntu:22.04
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
COPY --from=builder target/CustomerService-0.0.1.jar CustomerService-0.0.1.jar
COPY --from=builder custom-jre custom-jre

EXPOSE 8080
CMD ["./custom-jre/bin/java","-jar","-XX:TieredStopAtLevel=1", "-noverify", "-Dspring.profiles.active=prod","/CustomerService-0.0.1.jar"]
