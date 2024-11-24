import Book from '../models/bookModel.js';

const getBooks = async (req, res) => {
  try {
    const books = await Book.find({});
    console.log('Found books:', books); // Debug log
    res.json(books);
  } catch (error) {
    console.error('Error in getBooks:', error); // Debug log
    res.status(500).json({ message: error.message });
  }
};

const getBooksById = async (req, res) => {
  try {
    console.log('Looking for book with ID:', req.params.id); // Debug log

    const book = await Book.findById(req.params.id);
    console.log('Found book:', book); // Debug log

    if (book) {
      res.json(book);
    } else {
      res.status(404).json({ message: 'Book not found' });
    }
  } catch (error) {
    console.error('Error in getBooksById:', error); // Debug log

    // Si el error es debido a un formato de ID inválido
    if (error.name === 'CastError') {
      return res.status(400).json({ message: 'Invalid book ID format' });
    }

    res.status(500).json({ message: error.message });
  }
};

export { getBooksById, getBooks };