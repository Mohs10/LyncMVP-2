import axios from 'axios';

// Base API URL for products
const PRODUCT_API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/products/all';

const getAllProducts = async () => {
  try {
    const response = await axios.get(PRODUCT_API_URL);
    console.log('Products fetched successfully:', response.data);
    return response.data; // Return the fetched products
  } catch (error) {
    console.error('Error fetching products:', error.message);
    throw new Error('Failed to fetch products. Please check the API endpoint.');
  }
};

export { getAllProducts };
