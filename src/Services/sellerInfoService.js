import axios from 'axios';

const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/sellerSellingProduct';

// Function to get seller information based on productId, productFormId, and productVarietyId
const getSellerInfo = async (productId, productFormId, productVarietyId) => {
  const token = localStorage.getItem('authToken'); // Retrieve the token from localStorage

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    // Make a GET request to the API with the Authorization header containing the token
    const response = await axios.get(
      `${API_URL}/${productId}/${productFormId}/${productVarietyId}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    if (!response.data) {
      throw new Error('No data returned from the API');
    }

    return response.data; // Return the seller data for the product
  } catch (error) {
    handleError(error, 'Error fetching seller information');
  }
};

// Function to handle errors in API calls
const handleError = (error, message) => {
  if (error.response) {
    console.error(`${message}:`, error.response.data);
    throw new Error(`Error: ${error.response.status} - ${error.response.data.message || error.response.data}`);
  } else if (error.request) {
    console.error('No response received:', error.request);
    throw new Error('Network error, please check your connection.');
  } else {
    console.error('Unexpected error:', error.message);
    throw new Error(`Unexpected error: ${error.message}`);
  }
};

export default {
  getSellerInfo,
};
