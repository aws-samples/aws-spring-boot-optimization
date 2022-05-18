FROM public.ecr.aws/lts/ubuntu:22.04 AS builder
LABEL maintainer="Sascha Möllering <smoell@amazon.de>"

RUN apt-get update --fix-missing && apt-get install wget gnupg2 software-properties-common -y
RUN mkdir -p /usr/share/man/man1
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add -
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'
RUN apt install -y java-11-amazon-corretto-jdk

RUN wget https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz -P /tmp
RUN tar xf /tmp/apache-maven-3.8.5-bin.tar.gz -C /opt
RUN ln -s /opt/apache-maven-3.8.5 /opt/maven

ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}

COPY ./pom.xml ./pom.xml
COPY src ./src/

ENV MAVEN_OPTS='-Xmx6g'

RUN mvn -Dmaven.test.skip=true clean package

FROM public.ecr.aws/lts/ubuntu:22.04

RUN apt-get update && apt-get install wget gnupg2 software-properties-common -y
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add -
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'
RUN apt install -y java-11-amazon-corretto-jdk

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
COPY --from=builder target/CustomerService-0.0.1.jar CustomerService-0.0.1.jar
EXPOSE 8080
CMD ["java","-jar","-Dspring.profiles.active=prod","/CustomerService-0.0.1.jar"]