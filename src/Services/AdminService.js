import axios from 'axios';

// API endpoint to get admin details
const ADMIN_DETAILS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/details';

const getAdminDetails = async () => {
  console.log('Fetching admin details...');
  const token = localStorage.getItem('authToken');

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    // Make GET request with Authorization header
    const response = await axios.get(ADMIN_DETAILS_URL, {
      headers: {
        Authorization: `Bearer ${token}`, // Include the token in the Authorization header
      },
      timeout: 5000, // Set timeout of 5 seconds
    });

    const adminDetails = response.data;
    console.log('Admin details fetched successfully:', adminDetails);

    // Store admin details in localStorage
    localStorage.setItem('adminDetails', JSON.stringify(adminDetails));

    return adminDetails; // Return the admin details

  } catch (error) {
    if (error.response) {
      // Handle server-side errors
      console.error('Error response data:', error.response.data);
      if (error.response.status === 401) {
        throw new Error('Unauthorized access, please login again.');
      } else if (error.response.status === 403) {
        throw new Error('Access forbidden.');
      } else if (error.response.status === 404) {
        throw new Error('Admin details not found.');
      } else {
        throw new Error(`Server error: ${error.response.status}`);
      }
    } else if (error.request) {
      // Handle client-side errors
      console.error('No response received:', error.request);
      throw new Error('Network error, please check your connection.');
    } else {
      // Handle other errors
      throw new Error(`Error: ${error.message}`);
    }
  }
};

export default getAdminDetails;
