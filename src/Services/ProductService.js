import axios from 'axios';

// API endpoint to get all products
const PRODUCTS_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/products/all';

// API endpoint to inactivate a product
const INACTIVATE_PRODUCT_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/inactivateProduct/';

// API endpoint to activate a product
const ACTIVATE_PRODUCT_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/activateProduct/';

// Function to get all products
const getAllProducts = async () => {
  try {
    const response = await axios.get(PRODUCTS_URL);
    console.log('Products fetched successfully:', response.data);
    return response.data; // Return the product data (an array of products)
  } catch (error) {
    handleError(error, 'Error fetching products');
  }
};

// Function to find a product by ID from the fetched product list
const findProductById = async (productId) => {
  console.log(`Finding product with ID ${productId}...`);
  try {
    const products = await getAllProducts(); // Fetch all products first
    console.log('Fetched products:', products);

    // Convert productId to a number
    const numericProductId = Number(productId);

    // Find the product by ID (assuming productId in products is a number)
    const product = products.find(p => p.productId === numericProductId); // Use numeric comparison

    if (product) {
      console.log(`Product with ID ${numericProductId} found:`, product);
      return product;
    } else {
      console.error(`Product with ID ${numericProductId} not found.`);
      return null;
    }
  } catch (error) {
    console.error('Error finding product by ID:', error.message);
    throw new Error('Could not find product.');
  }
};

// Function to inactivate a product by ID
const inactivateProduct = async (productId) => {
  console.log(`Inactivating product with ID ${productId}...`);
  
  const token = getToken(); // Retrieve the token
  if (!token) {
    throw new Error('No token found. Please login to continue.');
  }

  try {
    const response = await axios.patch(`${INACTIVATE_PRODUCT_URL}${productId}`, {}, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    console.log(`Product with ID ${productId} inactivated successfully:`, response.data);
    return response.data; // Return the response data if needed
  } catch (error) {
    handleError(error, 'Error inactivating product');
  }
};
const addNewProduct = async (productData) => {
  const ADD_PRODUCT_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/products/add';

  const token = getToken(); // Retrieve the token
  if (!token) {
    throw new Error('No token found. Please login to continue.');
  }

  try {
    const response = await axios.post(ADD_PRODUCT_URL, productData, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    console.log('Product added successfully:', response.data);
    return response.data; // Return the response data if needed
  } catch (error) {
    handleError(error, 'Error adding product');
  }
};

// Function to activate (restore) a product by ID
const activateProduct = async (productId) => {
  console.log(`Activating product with ID ${productId}...`);

  const token = getToken(); // Retrieve the token
  if (!token) {
    throw new Error('No token found. Please login to continue.');
  }

  try {
    const response = await axios.patch(`${ACTIVATE_PRODUCT_URL}${productId}`, {}, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    console.log(`Product with ID ${productId} activated successfully:`, response.data);
    return response.data; // Return the response data if needed
  } catch (error) {
    handleError(error, 'Error activating product');
  }
};

// Helper function to retrieve the token from local storage
const getToken = () => {
  try {
    const token = localStorage.getItem('authToken');
    console.log('Token:', token); // Debugging: log the token
    return token;
  } catch (error) {
    console.error('Failed to retrieve token:', error);
    return null; // Return null if there's an error
  }
};

// Improved error handling function
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

export { getAllProducts, findProductById, inactivateProduct, activateProduct }