import axios from 'axios';
// API endpoints
const ADD_VARIETY_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/varieties/add';
const GET_VARIETIES_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/varieties/all';

// Function to add a variety
export const addVariety = async (varietyName) => {
  const token = localStorage.getItem('authToken');

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    // Add the variety via the API
    const response = await axios.post(ADD_VARIETY_URL, { varietyName }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Variety added successfully:', response.data);

    // Fetch all varieties after adding the new one
    const allVarieties = await getVarieties();

    // Search for the variety by name in the fetched varieties
    const foundVariety = allVarieties.find(variety => variety.varietyName === varietyName);

    if (foundVariety) {
      console.log('Variety found:', foundVariety);
      return foundVariety; // Return the found variety
    } else {
      throw new Error('Variety not found after adding.');
    }
  } catch (error) {
    handleError(error, 'Error adding variety');
  }
};

// Function to get all varieties
export const getVarieties = async () => {
  const token = localStorage.getItem('authToken');

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(GET_VARIETIES_URL, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Varieties fetched successfully:', response.data);
    return response.data; // Return the list of varieties
  } catch (error) {
    handleError(error, 'Error fetching varieties');
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
