// import axios from 'axios';

// // Base API URL
// const BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin';

// // Function to send an inquiry to sellers using the Query ID
// export const sendInquiryToSeller = async (queryId, { sellerUIds, adminInitialPrice, adminAddressId, description }) => {
//   const token = localStorage.getItem('authToken'); // Retrieve auth token from local storage

//   if (!token) {
//     throw new Error('No token found, please login');
//   }

//   const apiEndpoint = `${BASE_URL}/sendInquiryToSeller/${queryId}`; // Dynamic endpoint with Query ID

//   try {
//     const response = await axios.post(
//       apiEndpoint,
//       { sellerUIds, adminInitialPrice, adminAddressId, description },
//       {
//         headers: { Authorization: `Bearer ${token}` }, // Add authorization header
//       }
//     );
//     console.log('Inquiry sent successfully:', response.data);
//     return response.data; // Return the response data
//   } catch (error) {
//     handleError(error, 'Error sending inquiry');
//   }
// };

// // Function to handle errors
// const handleError = (error, message) => {
//   if (error.response) {
//     console.error(`${message}:`, error.response.data);
//     throw new Error(`Error: ${error.response.status} - ${error.response.data}`);
//   } else if (error.request) {
//     console.error('No response received:', error.request);
//     throw new Error('Network error, please check your connection.');
//   } else {
//     throw new Error(`Error: ${error.message}`);
//   }
// };
// export default sendInquiryToSeller;


// Function to send an inquiry to sellers using the Query ID
// export const sendInquiryToSeller = async (queryId, { sellerUIds, adminInitialPrice, adminAddressId, description }) => {
//   if (!queryId) {
//     throw new Error('Query ID is required');
//   }

//   const token = localStorage.getItem('authToken'); // Retrieve auth token from local storage
//   if (!token) {
//     throw new Error('No token found, please login');
//   }

//   const apiEndpoint = `${BASE_URL}/sendInquiryToSeller/${queryId}`;
//   console.log('API Endpoint:', apiEndpoint);

//   const payload = {
//     sellerUIds,
//     adminInitialPrice,
//     adminAddressId,
//     description,
//   };

//   console.log('Request Payload:', payload);

//   try {
//     const response = await axios.post(apiEndpoint, payload, {
//       headers: {
//         Authorization: `Bearer ${token}`, // Add token to request headers
//         'Content-Type': 'application/json', // Ensure content type is set
//       },
//     });

//     console.log('Inquiry sent successfully:', response.data);
//     return response.data; // Return the API response
//   } catch (error) {
//     handleError(error, 'Error sending inquiry');
//   }
// };

// // Function to handle errors
// const handleError = (error, message) => {
//   if (error.response) {
//     console.error(`${message}:`, error.response.data);
//     throw new Error(`${error.response.data.message || 'Something went wrong'}`);
//   } else if (error.request) {
//     console.error('No response received:', error.request);
//     throw new Error('Network error, please check your connection.');
//   } else {
//     console.error('Error:', error.message);
//     throw new Error(error.message);
//   }
// };

// export default sendInquiryToSeller;
import axios from 'axios';

// Base API URL
const BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin';

export const sendInquiryToSeller = async (
  queryId,
  { sellerUIds, adminInitialPrice, avgLeadTime, adminAddressId, description }
) => {
  if (!queryId) {
    throw new Error('Query ID is required');
  }

  const token = localStorage.getItem('authToken');
  if (!token) {
    throw new Error('No token found, please login');
  }

  const apiEndpoint = `${BASE_URL}/sendInquiryToSeller/${queryId}`;
  const payload = {
    sellerUIds,
    adminInitialPrice,
    avgLeadTime,
    adminAddressId,
    description,
  };

  console.log('Sending payload to API:', payload); // Debug payload
  console.log('API endpoint:', apiEndpoint); // Debug endpoint

  try {
    const response = await axios.post(apiEndpoint, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if(response.status == 200){
      console.log('Inquiry sent successfully. Response:', response.data); // Debug response
    } else {
      console.log(response.status);
    }
    
    return response.data;
  } catch (error) {
    if (error.response) {
      console.error('API Error:', error.response.data); // Log server error response
      throw new Error(error.response.data.message || 'Failed to send inquiry.');
    } else if (error.request) {
      console.error('Network Error:', error.request); // Log network error
      throw new Error('Network error, please try again later.');
    } else {
      console.error('Unexpected Error:', error.message); // Log unexpected errors
      throw new Error('Unexpected error occurred.');
    }
  }
};
