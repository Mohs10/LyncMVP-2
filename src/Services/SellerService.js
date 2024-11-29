import axios from 'axios';

const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/';

// Function to get inquiry details by ID with Bearer token
const getInquiryById = async (queryId) => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(`${API_URL}auth/admin/getInquiryById/${queryId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data; // Return the fetched inquiry data
  } catch (error) {
    throw new Error('Error fetching inquiry details');
  }
};

const InquiriesService = {
  getInquiryById,
  // other methods...
};

export default InquiriesService;
