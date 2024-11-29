import axios from 'axios';

const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/products/';
const token = localStorage.getItem('authToken');

if (!token) throw new Error('No authentication token found.');

const viewProductById = async (productId) => {
  try {
    // Make a GET request to fetch the product by ID
    const response = await axios.get(`${API_URL}${productId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // The product data returned from the API
    console.log('Product details fetched:', response.data);

    return response.data;
  } catch (error) {
    console.error('Error fetching product details:', error.response ? error.response.data : error.message);
    throw error;
  }
};

export { viewProductById };
