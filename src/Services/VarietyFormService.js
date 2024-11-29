import axios from 'axios';

const VARIETIES_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/varieties/all';
const FORMS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/forms/all';

// Fetch all varieties
const getVarieties = async () => {
  try {
    const response = await axios.get(VARIETIES_URL);
    console.log('Varieties fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error fetching varieties:', error.message);
    throw new Error('Could not fetch varieties.');
  }
};

// Fetch all forms
const getForms = async () => {
  try {
    const response = await axios.get(FORMS_URL);
    console.log('Forms fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error fetching forms:', error.message);
    throw new Error('Could not fetch forms.');
  }
};

export { getVarieties, getForms };
