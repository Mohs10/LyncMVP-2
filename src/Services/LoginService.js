import axios from 'axios';

const LOGIN_REST_API_BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/generateToken';

const login = async (credentials) => {
  try {
    const response = await axios.post(LOGIN_REST_API_BASE_URL, credentials, {
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return response.data; // Assuming the token is directly returned in response.data
  } catch (error) {
    throw error; // Forward the error to the calling function
  }
};

export default login;
