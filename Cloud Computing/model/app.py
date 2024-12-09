from flask import Flask, request, jsonify
import numpy as np
import pandas as pd
from tensorflow.keras.models import load_model
import tensorflow as tf

app = Flask(__name__)

model = load_model("model_destinasi_wisatav4.h5")
df = pd.read_csv("DataDestinasi.csv")

def create_label_mappings(csv_df):
    # Baca file CSV ke dalam dataframe
    df = csv_df

    # Buat label_encoder: Dictionary berbasis nama tempat
    label_encoder = {
        row['Place_Name']: {
            "index": idx,
            "Place_Id": row['Place_Id'],
            "Address": row['Alamat Detail'],
            "Price": row["Price"],
            "Category": row["Category"],
            "Rating": row["Rating"],
            "City": row["City"]
        }
        for idx, row in df.iterrows()
    }

    # Buat label_decoder: Dictionary berbasis indeks
    label_decoder = {
        value["index"]: {
            "Place_Name": key,
            "Place_Id": value['Place_Id'],
            "Address": value['Address'],
            "Price": value["Price"],
            "Category": value["Category"],
            "Rating": value["Rating"],
            "City": value["City"]
        }
        for key, value in label_encoder.items()
    }

    return label_encoder, label_decoder

label_encoder, label_decoder = create_label_mappings(df)

def convertPrice(price):
    if price == 0 or price == "0":
        return "Gratis"
    return price

def predict_destination(price, rating, city, category, top_n=5):
    # Pastikan input berada dalam bentuk tensor dengan tipe data yang sesuai
    price_input = tf.constant([price], dtype=tf.float32)
    rating_input = tf.constant([rating], dtype=tf.float32)
    city_input = tf.constant([city], dtype=tf.string)
    category_input = tf.constant([category], dtype=tf.string)

    # Prediksi probabilitas
    probabilities = model.predict([price_input, city_input, rating_input, category_input])[0]

    # Ambil indeks dengan probabilitas tertinggi
    top_indices = np.argsort(probabilities)[::-1]  # Urutkan skor tertinggi ke terendah

    # Filter awal dengan kategori
    recommendations = []
    for idx in top_indices:
        if idx not in label_decoder:
            continue

        # Ambil detail tempat dari label_decoder
        place_data = label_decoder[idx]
        Place_Id = place_data['Place_Id']
        place_name = place_data["Place_Name"]
        place_address = place_data["Address"]
        place_rating = place_data['Rating']
        place_city = place_data["City"]
        place_price = place_data["Price"]
        place_category = place_data["Category"]

        # Terapkan filter awal
        if place_city.lower() == city.lower() and place_price < price and place_category.lower() == category.lower():
            recommendations.append({
                "id": Place_Id,
                "Place": place_name,
                "City": place_city,
                "Rating": place_rating,
                "Price": convertPrice(place_price),
                "Category": place_category,
                "Address": place_address
            })

        if len(recommendations) == top_n:
            break

    # Jika rekomendasi masih kosong, abaikan filter kategori
    if len(recommendations) == 0:
        for idx in top_indices:
            if idx not in label_decoder:
                continue

            # Ambil detail tempat dari label_decoder
            place_data = label_decoder[idx]
            Place_Id = place_data['Place_Id']
            place_name = place_data["Place_Name"]
            place_address = place_data["Address"]
            place_rating = place_data['Rating']
            place_city = place_data["City"]
            place_price = place_data["Price"]
            place_category = place_data["Category"]

            # Terapkan filter tanpa kategori
            if place_city == city and place_price < price:
                recommendations.append({
                    "id": Place_Id,
                    "Place": place_name,
                    "City": place_city,
                    "Rating": place_rating,
                    "Price": convertPrice(place_price),
                    "Category": place_category,
                    "Address": place_address
                })

            if len(recommendations) == top_n:
                break

    return recommendations

@app.route('/', methods=['GET'])
def health_check():
    return jsonify({"message": "API is running"}), 200

@app.route("/home/recomendation", methods=['POST'])
def recommend():
    try :
        data = request.json
        price = data.get('price')
        city = data.get('city')
        category = data.get('category')
        rating = data.get('rating')

        if price is None or city is None or category is None or rating is None:
            return jsonify({"error": "Missing required fields"}), 400
        
        recommendations = predict_destination(price, rating, city, category)
        return jsonify({
            "status" : "success",
            "data" : recommendations
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500
    

@app.route("/home", methods=['GET'])
def getPopulerPlace():
    place = df.sort_values(by='Rating', ascending=False)
    data = [
        {
            "id": row["Place_Id"],
            "Place": row["Place_Name"],
            "City": row["City"],
            "Price": convertPrice(row["Price"]),
            "Rating": row["Rating"],
            "Category": row["Category"],
            "Address": row["Alamat Detail"],
        }
        for _, row in place.iterrows()  # Iterasi baris DataFrame
    ]
    return jsonify({
        "status": "success",
        "data": data
    })

    
@app.route("/home/recomendation/<int:Place_Id>", methods=['GET'])
def getDetailPlace(Place_Id) :
    data = df[df['Place_Id'] == Place_Id].iloc[0]
    return jsonify({
        "status" : "success",
        "data": {
            "id": int(data["Place_Id"]),
            "Place": data['Place_Name'],
            "City": data['City'],
            "Address": data['Alamat Detail'],
            "Price": convertPrice(str(data['Price'])),
            "Rating": float(data['Rating']),
            "Category": data['Category']
        }
    })

@app.route("/home/<int:Place_Id>", methods=['GET'])
def getDetail(Place_Id) :
    data = df[df['Place_Id'] == Place_Id].iloc[0]
    return jsonify({
        "status" : "success",
        "data": {
            "id": int(data["Place_Id"]),
            "Place": data['Place_Name'],
            "City": data['City'],
            "Address": data['Alamat Detail'],
            "Price": convertPrice(str(data['Price'])),
            "Rating": float(data['Rating']),
            "Category": data['Category']
        }
    })

if __name__ == '__main__':
    app.run(debug=True)