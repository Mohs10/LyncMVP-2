import axios from 'axios';

// API URLs
const PRODUCT_API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/products/all';
const CATEGORY_API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/categories/all';

// Fetch all products
const getAllProducts = async () => {
  try {
    const response = await axios.get(PRODUCT_API_URL);
    console.log('Products fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error.message);
    throw new Error('Failed to fetch products. Please check the API endpoint.');
  }
};

// Fetch all categories
const getAllCategories = async () => {
  try {
    const response = await axios.get(CATEGORY_API_URL);
    console.log('Categories fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error fetching categories:', error.message);
    throw new Error('Failed to fetch categories. Please check the API endpoint.');
  }
};

export { getAllProducts, getAllCategories };
