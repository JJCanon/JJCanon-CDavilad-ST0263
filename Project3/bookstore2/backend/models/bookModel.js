import mongoose from 'mongoose';

// Actualizamos el nombre de la colección a tb_books
const bookSchema = mongoose.Schema({
    id: {
        type: String,
        required: true,
        unique: true
    },
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
    collection: 'tb_books' // Especificamos el nombre exacto de la colección
});

const Book = mongoose.model('Book', bookSchema, 'tb_books');
export default Book;