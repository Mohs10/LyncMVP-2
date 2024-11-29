import axios from 'axios';

// API endpoints for adding, getting, updating, and deleting testimonials
const ADD_TESTIMONIAL_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/testimonials/addTestimonial';
const GET_ALL_TESTIMONIALS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/testimonials/getAll';
const UPDATE_TESTIMONIAL_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/testimonials/updateTestimonial';
const DELETE_TESTIMONIAL_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/testimonials/deleteTestimonial';

// Function to add a new testimonial
export const addTestimonial = async (testimonialData) => {
  console.log(testimonialData);

  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.post(ADD_TESTIMONIAL_URL, testimonialData, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Testimonial added successfully:', response.data);
    return response.data; // Return the response data
  } catch (error) {
    handleError(error, 'Error adding testimonial');
  }
};

// Function to get all testimonials
export const getAllTestimonials = async () => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(GET_ALL_TESTIMONIALS_URL, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('All testimonials fetched successfully:', response.data);
    return response.data; // Return the fetched data
  } catch (error) {
    handleError(error, 'Error fetching testimonials');
  }
};

// Function to update an existing testimonial
export const updateTestimonial = async (testimonialId, testimonialData) => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.put(`${UPDATE_TESTIMONIAL_URL}/${testimonialId}`, testimonialData, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Testimonial updated successfully:', response.data);
    return response.data; // Return the updated testimonial data
  } catch (error) {
    handleError(error, 'Error updating testimonial');
  }
};

// Function to delete a testimonial
export const deleteTestimonial = async (testimonialId) => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.delete(`${DELETE_TESTIMONIAL_URL}/${testimonialId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Testimonial deleted successfully:', response.data);
    return response.data; // Return a success message or data
  } catch (error) {
    handleError(error, 'Error deleting testimonial');
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
