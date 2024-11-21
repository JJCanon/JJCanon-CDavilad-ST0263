import express from 'express';
import colors from 'colors';
import dotenv from 'dotenv';
import connectDB from './config/db.js';
import bookRoutes from './routes/bookRoutes.js';

dotenv.config();

const app = express();
app.use(express.json());

// Conectar a MongoDB
connectDB();

const PORT = process.env.PORT || 5001;

app.use('/api/books', bookRoutes);

app.get('/', (req, res) => {
  res.send('API is running...');
});

app.listen(PORT, () => {
  console.log(`Server running in ${process.env.NODE_ENV} and listening on PORT ${PORT}`.blue);
});