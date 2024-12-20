import axios from 'axios';

const API_URL = `${process.env.REACT_APP_API_URL}/books`;

export const getBooks = async () => {
    try {
        const response = await axios.get(API_URL);
        return response.data;
    } catch (error) {
        console.error('Error fetching books', error);
        throw error;
    }
};

export const getBookById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching book details', error);
        throw error;
    }
};