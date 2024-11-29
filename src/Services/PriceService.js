import axios from "axios";

// API URL (replace this with your actual API endpoint for selecting a seller)
const API_BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth';

export const sendFinalPrice = async (sellerId, proposalPrice) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/admin/sendFinalPrice/1`,
      {amount: proposalPrice },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
          'Content-Type': 'application/json',
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error submitting proposal:', error);
    throw new Error('Failed to submit proposal');
  }
};

// Service to select seller
export const selectSeller = async (sellerId) => {
    try {
        const url = `${API_BASE_URL}/admin/adminSelectsSeller/1`;

      console.log(`Request URL: ${url}`);  // Verify the constructed URL
  console.log('Auth Token:', localStorage.getItem('authToken'));

      const response = await axios.post(url, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
          'Content-Type': 'application/json',
        },
      });
  
      return response.data;
    } catch (error) {
      console.error('Error selecting seller:', error);
      throw new Error('Failed to select seller');
    }
  };
  