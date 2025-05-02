import pandas as pd
import numpy as np
from sqlalchemy import create_engine, text
import os

CHUNK_SIZE = 10000

engine = create_engine(f"mysql+pymysql://root:{os.getenv('MYSQL_PASSWORD')}@tapestria-db:3306/tapestria")

with engine.connect() as conn:
    exists = conn.execute(text("SHOW TABLES LIKE 'books'")).first()
    empty = conn.execute(text("SELECT COUNT(*) FROM books")).scalar() == 0 if exists else True

if empty:
    for chunk in pd.read_csv("data/books.csv", chunksize=CHUNK_SIZE):
        chunk.columns = [c.lower().replace("-", "_") for c in chunk.columns]
        chunk["numcopies"] = np.random.randint(10, 31, size=len(chunk))
        chunk.to_sql("books", con=engine, if_exists="append", index=False, method="multi")
