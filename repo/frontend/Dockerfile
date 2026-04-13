FROM node:20-alpine AS build
WORKDIR /app

COPY package.json angular.json tsconfig.json tsconfig.app.json ./
COPY src ./src

RUN npm install && npm run build

FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/city-bus-operation-frontend/ ./

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
