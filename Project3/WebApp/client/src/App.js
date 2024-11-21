import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container, Navbar, Nav } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

import HomePage from './pages/HomePage';
import BookList from './components/BookList';
import BookDetail from './components/BookDetail';
import AboutPage from './pages/AboutPage';

function App() {
  return (
    <Router>
      <Navbar bg="dark" variant="dark">
        <Container>
          <Navbar.Brand href="/">Biblioteca</Navbar.Brand>
          <Nav>
            <Nav.Link href="/">Inicio</Nav.Link>
            <Nav.Link href="/books">Libros</Nav.Link>
            <Nav.Link href="/about">Sobre</Nav.Link>
          </Nav>
        </Container>
      </Navbar>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/books" element={<BookList />} />
        <Route path="/book/:id" element={<BookDetail />} />
        <Route path="/about" element={<AboutPage />} />
      </Routes>
    </Router>
  );
}

export default App;