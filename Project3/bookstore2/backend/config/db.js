// config/db.js
import mongoose from 'mongoose';
import colors from 'colors';

const connectDB = async () => {
  try {
    const conn = await mongoose.connect(process.env.MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
      retryWrites: true,
      w: 'majority',
      ssl: true // Asegura que se use SSL
    });

    console.log(`MongoDB connected: ${conn.connection.host}`.yellow);

    // Verificar la conexión a la base de datos específica
    const db = mongoose.connection.db;
    console.log(`Connected to database: ${db.databaseName}`.yellow);

    // Listar las colecciones
    const collections = await db.listCollections().toArray();
    console.log('Available collections:'.yellow);
    collections.forEach(collection => {
      console.log(`- ${collection.name}`.yellow);
    });

  } catch (error) {
    console.error(`Error: ${error.message}`.red.underline.bold);
    process.exit(1);
  }
};

export default connectDB;