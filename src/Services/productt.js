import axios from 'axios';

const BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin';

const ProductService = {
  getProductDetails: async (productId, productFormId, productVarietyId) => {
    const token = localStorage.getItem('authToken'); // Retrieve token from localStorage
    if (!token) {
      throw new Error('No token found. Please log in.');
    }

    try {
      const response = await axios.get(
        `${BASE_URL}/sellerSellingProduct/${productId}/${productFormId}/${productVarietyId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      handleError(error, 'Error fetching product details');
    }
  },
};

// Function to handle errors
const handleError = (error, message) => {
  if (error.response) {
    console.error(`${message}:`, error.response.data);
    throw new Error(`Error: ${error.response.status} - ${error.response.data.message}`);
  } else if (error.request) {
    console.error('No response received:', error.request);
    throw new Error('Network error, please check your connection.');
  } else {
    console.error('Error:', error.message);
    throw new Error(error.message);
  }
};

export default ProductService;
