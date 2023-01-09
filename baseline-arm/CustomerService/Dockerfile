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

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && mvn -Dmaven.test.skip=true clean package"

FROM public.ecr.aws/lts/ubuntu:22.04

RUN apt-get update --fix-missing && apt-get install zip curl -y

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

RUN \
    curl -s "https://get.sdkman.io" | bash; \
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh; \
    sdk install java 17.0.5-amzn"

COPY --from=builder target/CustomerService-0.0.1.jar CustomerService-0.0.1.jar
EXPOSE 8080
CMD ["/home/spring/.sdkman/candidates/java/current/bin/java","-jar","-Dspring.profiles.active=prod","/CustomerService-0.0.1.jar"]
