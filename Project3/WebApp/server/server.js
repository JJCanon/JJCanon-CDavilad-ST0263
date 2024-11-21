const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const connectDB = require('./config/database');
const bookRoutes = require('./routes/bookRoutes');

// Cargar variables de entorno
dotenv.config();

// Conectar a la base de datos
connectDB();

// Crear aplicación Express
const app = express();

// Middlewares
app.use(cors());
app.use(express.json());

// Rutas
app.use('/api/books', bookRoutes);

// Iniciar servidor
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`Servidor corriendo en puerto ${PORT}`);
});