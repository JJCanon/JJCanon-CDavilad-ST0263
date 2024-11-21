import React from 'react';
import { Container, Jumbotron, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

function HomePage() {
    return (
        <Container className="text-center my-5">
            <h1>Bienvenido a nuestra Biblioteca</h1>
            <p>Explora nuestra colección de libros</p>
            <Link to="/books">
                <Button variant="primary" size="lg">Ver Libros</Button>
            </Link>
        </Container>
    );
}

export default HomePage;