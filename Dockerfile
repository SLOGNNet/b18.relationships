FROM isuper/java-oracle:server_jre_8
MAINTAINER Andrew Zubko

RUN groupadd -r bridge18 && useradd -r -g bridge18 bridge18

RUN mkdir /var/log/bridge18
RUN chown -R bridge18:bridge18 /var/log/bridge18

RUN mkdir /project

WORKDIR /project

COPY ./config ./config
COPY ./relationship-impl/target/relationship-impl-1.0-SNAPSHOT-conductr-bundle/relationship-v1/relationship ./

RUN chown -R bridge18:bridge18 /project/

VOLUME /var/log/bridge18

#USER bridge18
USER root

EXPOSE 8080

ENTRYPOINT ./bin/relationship