
import axios from 'axios';

const getProductDetails = async (inquiryId) => {
  const token = localStorage.getItem('authToken'); // Retrieve the token from localStorage

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    // Make a GET request to the API with the Authorization header containing the token
    const response = await axios.get(
      `http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/getInquiryById/${inquiryId}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    return response.data; // Return the product details
  } catch (error) {
    console.error('Error fetching product details:', error);
    throw new Error('Failed to fetch product details');
  }
};

export default {
  getProductDetails,
  // other service methods...
};
