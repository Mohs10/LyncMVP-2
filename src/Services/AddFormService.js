
import axios from 'axios';

// API endpoints
const ADD_FORM_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/forms/add';
const GET_FORMS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/forms/all';

// Function to add a form
export const addForm = async (formName) => {
  const token = localStorage.getItem('authToken');

  console.log(formName);

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    // Add the form via the API
    const response = await axios.post(ADD_FORM_URL, { formName }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Form added successfully:', response.data);

    // Fetch all forms after adding the new one
    const allForms = await getForms();

    // Search for the form by name in the fetched forms
    const foundForm = allForms.find(form => form.formName === formName);

    if (foundForm) {
      console.log('Form found:', foundForm);
      return foundForm; // Return the found form
    } else {
      throw new Error('Form not found after adding.');
    }
  } catch (error) {
    handleError(error, 'Error adding form');
  }
};

// Function to get all forms
export const getForms = async () => {
  const token = localStorage.getItem('authToken');

  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(GET_FORMS_URL, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Forms fetched successfully:', response.data);
    return response.data; // Return the list of forms
  } catch (error) {
    handleError(error, 'Error fetching forms');
  }
};

// Function to handle errors
const handleError = (error, message) => {
  if (error.response) {
    console.error(`${message}:`, error.response.data);
    throw new Error(`Error: ${error.response.status} - ${error.response.data}`);
  } else if (error.request) {
    console.error('No response received:', error.request);
    throw new Error('Network error, please check your connection.');
  } else {
    throw new Error(`Error: ${error.message}`);
  }
};
