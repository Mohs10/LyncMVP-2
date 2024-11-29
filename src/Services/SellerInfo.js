// sellerInfoService.js

import axios from 'axios';

const getSellerInfo = async (sellerUId) => {
  const token = localStorage.getItem('authToken'); // Retrieve the token from localStorage

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    // Make a GET request to fetch the seller's information by sellerUId
    const response = await axios.get(
      `http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/getInquiryById/${sellerUId}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    return response.data; // Return the seller data
  } catch (error) {
    console.error('Error fetching seller information:', error);
    throw new Error('Failed to fetch seller information');
  }
};

export default {
  getSellerInfo,
};
