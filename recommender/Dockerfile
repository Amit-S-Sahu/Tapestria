FROM python:3.12-slim
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y netcat-openbsd
RUN pip install -r requirements.txt
RUN chmod +x entrypoint.sh
EXPOSE 5000
ENTRYPOINT ["./entrypoint.sh"]
