// import axios from 'axios';

// const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin';

// const sendNegotiationToBuyer = async (queryId, amount) => {
//   try {
//     const token = localStorage.getItem('authToken'); // Ensure the key matches

//     if (!token) {
//       throw new Error('Token not found');
//     }

//     console.log('Sending negotiation with data:', amount);

//     const response = await axios.post(
//       `${API_URL}/adminSentFinalPriceToBuyer/${queryId}`,
//       amount,
//       {
//         headers: {
//           Authorization: `Bearer ${token}`,
//           'Content-Type': 'application/json',
//         },
//       }
//     );

//     console.log('API Response:', response); // Debug API Response
//     return response;
//   } catch (error) {
//     console.error('Error sending negotiation to buyer:', error.message);
//     throw new Error('Failed to send negotiation: ' + error.message);
//   }
// };

// export default {
//   sendNegotiationToBuyer,
// };
import axios from 'axios';

const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin';

const sendNegotiationToBuyer = async (queryId, data) => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('Auth token missing');
    }

    console.log('Sending POST request to:', `${API_URL}/adminSentFinalPriceToBuyer/${queryId}`);
    console.log('Payload:', data);

    const response = await axios.post(
      `${API_URL}/adminSentFinalPriceToBuyer/${queryId}`,
      data,
      {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      }
    );
    return response;
  } catch (error) {
    console.error('Error in API call:', error.message);
    throw error;
  }
};

export default {
  sendNegotiationToBuyer,
};
