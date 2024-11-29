import axios from 'axios';

// Base URL for the inactivate product API
const INACTIVATE_PRODUCT_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/inactivateProduct/';

// Function to get the token from localStorage or any other method you are using
const getToken = () => {
  return localStorage.getItem('token'); // Adjust this method if you're storing the token elsewhere
};

// Function to handle errors globally
const handleError = (error, customMessage) => {
  if (error.response) {
    // Server responded with a status other than 2xx
    console.error(`${customMessage}: ${error.response.status} - ${error.response.data.message}`);
  } else if (error.request) {
    // No response received from the server
    console.error(`${customMessage}: No response received`);
  } else {
    // Other errors (e.g., network issues)
    console.error(`${customMessage}: ${error.message}`);
  }
  throw new Error(customMessage);
};

// Service function to inactivate a product by its ID
export const inactivateProduct = async (productId) => {
  const token = getToken(); // Get the token from local storage
  if (!token) {
    throw new Error('No token found. Please login to continue.');
  }

  try {
    const response = await axios.put(`${INACTIVATE_PRODUCT_URL}${productId}`, null, {
      headers: {
        Authorization: `Bearer ${token}`, // Set the token in the Authorization header
      },
    });
    return response.data; // Return the data from the response
  } catch (error) {
    return handleError(error, 'Error inactivating product'); // Handle the error
  }
};
