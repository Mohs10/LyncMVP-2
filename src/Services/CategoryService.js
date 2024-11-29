// src/Services/CategoryService.js

import axios from 'axios';

// API endpoints
const CATEGORIES_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/categories/all';
const ADD_CATEGORY_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/categories/add';

// Function to fetch all categories
export const getAllCategories = async () => {
  try {
    const response = await axios.get(CATEGORIES_URL);
    console.log('Categories fetched successfully:', response.data);
    return response.data; // Return the categories data
  } catch (error) {
    handleError(error, 'Error fetching categories');
  }
};

// Function to add a new category
export const addCategory = async (categoryName) => {
    console.log(categoryName);

  const token = localStorage.getItem('authToken');
  
  if (!token) {
    throw new Error('No token found, please login');
  }

  try {
    const response = await axios.post(ADD_CATEGORY_URL, { categoryName: categoryName }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Category added successfully:', response.data);
    return response.data; // Return the response data
  } catch (error) {
    handleError(error, 'Error adding category');
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
