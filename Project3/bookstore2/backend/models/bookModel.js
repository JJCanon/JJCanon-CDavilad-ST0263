import mongoose from 'mongoose';

const bookSchema = mongoose.Schema({
    author: {
        type: String,
        required: true
    },
    countInStock: {
        type: Number,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    image: {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    price: {
        type: String,
        required: true
    }
}, {
    timestamps: true,
    collection: 'books' // Nombre exacto de la colección
});

const Book = mongoose.model('Book', bookSchema, 'books');
export default Book;
