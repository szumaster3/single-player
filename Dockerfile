FROM openjdk:11-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends git git-lfs && \
    rm -rf /var/lib/apt/lists/*

RUN git clone --depth=1 https://gitlab.com/rs2-emu/530-server.git

WORKDIR /app/530-server

RUN chmod +x run

EXPOSE 43595

CMD ["./run"]
