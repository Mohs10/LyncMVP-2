import axios from 'axios';

const BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/';

const AdminQuoteService = {
  sendQuoteToBuyer: async (queryId, price, comment) => {
    try {
      // Get the token from localStorage (or wherever you store it)
      const token = localStorage.getItem('authToken');
      console.log("Token:", token);  // Check if the token is being retrieved correctly
      
      if (!token) {
        console.error('No token found, please login');
        throw new Error('Authorization token is missing.');
      }

      // Prepare the request payload
      const requestData = {
        adminInitialPrice: price,
        comment: comment,
      };

      // Log request data for debugging
      console.log('Sending quote with data:', requestData);

      // If the token exists, include it in the headers and use BASE_URL
      const response = await axios.post(
        `${BASE_URL}auth/admin/adminQuoteToBuyer/${queryId}`,
        requestData,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Bearer token
          }
        }
      );

      // Log the response from the API for debugging
      console.log('Response from API:', response.data);

      return response.data;
    } catch (error) {
      // Log the full error for more insights
      console.error('Error sending quote:', error.response || error.message);
      throw error; // You can handle the error further in the calling component
    }
  },
};

export default AdminQuoteService;
