// CategoryService.js

import axios from 'axios';

// API endpoints
const ADD_CATEGORY_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/categories/add';
const GET_CATEGORIES_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/categories/all';

// Function to add a category
export const addCategory = async (categoryName) => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.post(ADD_CATEGORY_URL, { categoryName }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Category added successfully:', response.data);
    return response.data; // Return the response data
  } catch (error) {
    handleError(error, 'Error adding category');
  }
};

// Function to get all categories
export const getCategories = async () => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(GET_CATEGORIES_URL, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Categories fetched successfully:', response.data);
    return response.data; // Return the list of categories
  } catch (error) {
    handleError(error, 'Error fetching categories');
  }
};

// Function to get a category by ID
export const getCategoryById = async (categoryId) => {
  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.get(`${GET_CATEGORIES_URL}/${categoryId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Category fetched successfully:', response.data);
    return response.data; // Return the category object
  } catch (error) {
    handleError(error, 'Error fetching category');
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
