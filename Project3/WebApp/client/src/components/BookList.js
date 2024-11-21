import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getBooks } from '../services/bookService';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';

function BookList() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchBooks = async () => {
            try {
                const booksData = await getBooks();
                setBooks(booksData);
                setLoading(false);
            } catch (err) {
                setError(err);
                setLoading(false);
            }
        };
        fetchBooks();
    }, []);

    if (loading) return <div>Cargando libros...</div>;
    if (error) return <div>Error al cargar los libros</div>;

    return (
        <Container>
            <h1 className="my-4">Libros</h1>
            <Row>
                {books.map(book => (
                    <Col key={book._id} md={4} className="mb-4">
                        <Card>
                            <Card.Body>
                                <Card.Title>{book.title}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">{book.author}</Card.Subtitle>
                                <Card.Text>{book.description?.substring(0, 100)}...</Card.Text>
                                <Link to={`/book/${book._id}`}>
                                    <Button variant="primary">Ver Detalles</Button>
                                </Link>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Container>
    );
}

export default BookList;