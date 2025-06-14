FROM ghcr.io/graalvm/jdk-community:24

RUN mkdir /var/app
WORKDIR /var/app

COPY ./startup.sh ./startup.sh

CMD ["./startup.sh"]