FROM openjdk:11-jre-slim AS jrebase
RUN apt-get update && apt-get install -y fontconfig libfontconfig1 default-mysql-client  \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo 'Asia/Shanghai' > /etc/timezone
RUN apt-get install -y nginx && rm /etc/nginx/sites-enabled/default && rm /etc/nginx/nginx.conf

FROM jrebase AS root

ARG JAVA_OPTS="-Xmx512M"
ENV JAVA_OPTS=$JAVA_OPTS
ARG RUN_ARGS="--spring.profiles.active=demo"
ENV RUN_ARGS=$RUN_ARGS
COPY ./docker/readme.conf /etc/nginx/conf.d/readme.conf
COPY ./docker/nginx.conf /etc/nginx/nginx.conf
COPY ./web/dist /root/web
COPY ./target/readme.jar /readme.jar
EXPOSE 80 25 110
ENTRYPOINT ["sh","-c","nginx && java $JAVA_OPTS -Djava.awt.headless=true -jar /readme.jar $RUN_ARGS"]
