# ESTÁGIO 1: Compilação (Build)
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Compila o projeto Angular para produção
RUN npm run build --configuration=production

# ESTÁGIO 2: Servidor Web (Nginx)
FROM nginx:alpine
# Copia os arquivos gerados no passo anterior para a pasta pública do Nginx
# ATENÇÃO: Confirme se a pasta gerada dentro de /dist é "agente-estoque-ui/browser" ou apenas "agente-estoque-ui"
COPY --from=build /app/dist/agente-estoque-ui/browser /usr/share/nginx/html
# Copia a nossa regrinha de rotas
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/40-generate-env.sh /docker-entrypoint.d/40-generate-env.sh
RUN chmod +x /docker-entrypoint.d/40-generate-env.sh

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]