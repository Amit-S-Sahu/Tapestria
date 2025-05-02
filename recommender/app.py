from flask import Flask, request, jsonify
import pickle
import numpy as np

app = Flask(__name__)

# Load pickled data
popular_df = pickle.load(open('./models/popular.pkl', 'rb'))
pt = pickle.load(open('./models/pt.pkl', 'rb'))
books = pickle.load(open('./models/books.pkl', 'rb'))
similarity_scores = pickle.load(open('./models/similarity_scores.pkl', 'rb'))

@app.route('/')
def home():
    return "Book Recommendation API is running."

@app.route('/popular', methods=['GET'])
def popular():
    data = popular_df.to_dict(orient='records')
    return jsonify(data)

@app.route('/recommend', methods=['POST'])
def recommend():
    try:
        data = request.get_json()
        book_name = data['book_name']
        
        if book_name not in pt.index:
            return jsonify({'error': 'Book not found'}), 404

        index = np.where(pt.index == book_name)[0][0]
        similar_items = sorted(list(enumerate(similarity_scores[index])), key=lambda x: x[1], reverse=True)[1:6]
        recommended_books = [pt.index[i[0]] for i in similar_items]

        return jsonify({'recommended_books': recommended_books})

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
