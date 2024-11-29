// File: src/Services/TrashService.js

import axios from 'axios';

// API endpoint
const INACTIVE_PRODUCTS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/inactiveProducts';

// Function to fetch all inactive products
export const getInactiveProducts = async () => {
  try {
    const response = await axios.get(INACTIVE_PRODUCTS_URL);
    console.log('Inactive products fetched successfully:', response.data);
    return response.data; // Return the inactive products data
  } catch (error) {
    handleError(error, 'Error fetching inactive products');
  }
};

// Function to handle errors
const handleError = (error, message) => {
  if (error.response) {
    console.error(`${message}:`, error.response.data);
    throw new Error(`Error: ${error.response.status} - ${error.response.data}`);
  } else if (error.request) {
    console.error('No response received:', error.request);
    throw new Error('Network error, please check your connection.');
  } else {
    throw new Error(`Error: ${error.message}`);
  }
};
