import axios from 'axios';

// API endpoints
const ADD_ADDRESS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/addAddress';
const GET_ALL_ADDRESSES_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/getAllAddress';

// Function to add an address
export const addAddress = async (addressData) => {
  const token = localStorage.getItem("authToken");

  if (!token) {
    throw new Error("No token found, please login");
  }

  try {
    const response = await axios.post(ADD_ADDRESS_URL, addressData, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log("Address added successfully:", response.data);
    return response.data; // Return the response data
  } catch (error) {
    handleError(error, "Error adding address");
  }
};


// Function to get all addresses
export const getAllAddresses = async () => {
  const token = localStorage.getItem('authToken'); // Retrieve the token

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(GET_ALL_ADDRESSES_URL, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Fetched addresses successfully:', response.data);
    return response.data; // Return the list of addresses
  } catch (error) {
    handleError(error, 'Error fetching addresses');
  }
};

// Function to handle errors
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
