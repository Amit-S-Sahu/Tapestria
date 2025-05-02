#!/bin/sh

echo "Waiting for MySQL..."
until nc -z tapestria-db 3306; do sleep 1; done

echo "Running one-time books import (if needed)..."
python3 load_books.py

echo "Starting recommender service..."
exec python3 app.py
