# Build stage
FROM node:22-alpine AS builder
WORKDIR /app
COPY . /app
RUN npm install && npm run build

# Production stage
FROM node:22-alpine
RUN npm install -g serve
WORKDIR /app
COPY --from=builder /app/dist ./dist
EXPOSE 3000
CMD ["serve", "-s", "dist", "-l", "3000"]
