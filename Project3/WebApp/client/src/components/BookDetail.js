import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getBookById } from '../services/bookService';
import { Container, Card, Button } from 'react-bootstrap';

function BookDetail() {
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { id } = useParams();

    useEffect(() => {
        const fetchBook = async () => {
            try {
                const bookData = await getBookById(id);
                setBook(bookData);
                setLoading(false);
            } catch (err) {
                setError(err);
                setLoading(false);
            }
        };
        fetchBook();
    }, [id]);

    if (loading) return <div>Cargando detalles del libro...</div>;
    if (error) return <div>Error al cargar los detalles del libro</div>;
    if (!book) return <div>Libro no encontrado</div>;

    return (
        <Container>
            <Card className="my-4">
                <Card.Body>
                    <Card.Title>{book.title}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">{book.author}</Card.Subtitle>
                    <Card.Text>{book.description}</Card.Text>
                    <p>Año de publicación: {book.publishYear}</p>
                    <p>Género: {book.genre}</p>
                    <Link to="/books">
                        <Button variant="secondary">Volver a la lista</Button>
                    </Link>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default BookDetail;