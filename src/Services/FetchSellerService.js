import axios from 'axios';

// API URL
const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/getInquiryById/';

// Function to get headers with Bearer token
const getHeaders = () => {
    const token = localStorage.getItem('authToken');
    console.log('Token:', token);  // Log the token to verify
    if (!token) {
      throw new Error('No token found, please login');
    }
    
}

// Function to fetch seller details
export const fetchSellerDetails = async (queryId) => {
  try {
    const response = await axios.get(`${API_URL}${queryId}`, {
      headers: getHeaders()  // Using the custom getHeaders function
    });
    return response.data;  // Return the data to be used in your component
  } catch (error) {
    console.error("Error fetching seller details", error);
    throw error;
  }
};
