FROM node:23-alpine
WORKDIR /app
COPY frontend/ /app
RUN npm install
RUN npm run build
EXPOSE 3000
CMD ["npm", "run", "start"]
