FROM node:20-bullseye
WORKDIR /workspace

RUN apt-get update \
    && apt-get install -y --no-install-recommends maven chromium \
    && rm -rf /var/lib/apt/lists/*

ENV CHROME_BIN=/usr/bin/chromium

COPY . .

RUN chmod +x /workspace/run-tests.sh

CMD ["/workspace/run-tests.sh"]
