FROM public.ecr.aws/lts/ubuntu:22.04 AS builder
LABEL maintainer="Sascha Möllering <smoell@amazon.de>"

RUN apt-get update
RUN apt-get install -y wget unzip binutils gnupg2 software-properties-common
RUN wget https://apt.corretto.aws/corretto.key -P /tmp
RUN apt-key add /tmp/corretto.key
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'
RUN apt-get install -y java-17-amazon-corretto-jdk
RUN mkdir -p /usr/share/man/man1

RUN wget https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -P /tmp
RUN tar xf /tmp/apache-maven-3.8.6-bin.tar.gz -C /opt
RUN ln -s /opt/apache-maven-3.8.6 /opt/maven

ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}

COPY ./pom.xml ./pom.xml
COPY src ./src/

ENV MAVEN_OPTS='-Xmx6g'

RUN mvn -Dmaven.test.skip=true clean package && cd target && unzip CustomerService-0.0.1.jar

RUN jdeps --ignore-missing-deps \
    --multi-release 17 --print-module-deps \
    --class-path target/BOOT-INF/lib/* \
    target/CustomerService-0.0.1.jar > jre-deps.info

RUN export JAVA_TOOL_OPTIONS="-Djdk.lang.Process.launchMechanism=vfork" && \
    jlink --verbose --compress 2 --strip-java-debug-attributes \
    --no-header-files --no-man-pages --output custom-jre \
    --add-modules $(cat jre-deps.info)

FROM public.ecr.aws/lts/ubuntu:22.04
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
COPY --from=builder target/CustomerService-0.0.1.jar CustomerService-0.0.1.jar
COPY --from=builder custom-jre custom-jre

EXPOSE 8080
CMD ["./custom-jre/bin/java","-jar","-XX:TieredStopAtLevel=1", "-noverify", "-Dspring.profiles.active=prod","/CustomerService-0.0.1.jar"]
