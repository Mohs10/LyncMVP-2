import axios from 'axios';

const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/products/add';
const UPLOAD_IMAGE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/uploadProductImage';
// const UPLOAD_IMAGE_URL = 'http://localhost:8089/api/uploadProductImage';

// Helper function to convert Blob to File with dynamic MIME type
function blobToFile(blob, fileName, extension = '.jpeg') {
  const mimeType = blob.type || 'image/jpeg'; // Use the blob's MIME type if available
  return new File([blob], `${fileName}${extension}`, { type: mimeType });
}

// Helper function to validate product data
function validateProductData(data) {
  if (!data.productName || typeof data.productName !== 'string') {
    throw new Error('Invalid product name');
  }
  if (
    !data.hsnCode || 
    typeof data.hsnCode !== 'string' || 
    !/^\d{4}(\d{2})?(\d{2})?$/.test(data.hsnCode)
  ) {
    throw new Error('Invalid HSN code. It must be 4, 6, or 8 digits.');
  }
  
  if (!data.categoryId || isNaN(data.categoryId)) {
    throw new Error('Invalid category ID');
  }
  if (data.varietyIds && !Array.isArray(data.varietyIds)) {
    throw new Error('Variety IDs must be an array');
  }
  if (data.formIds && !Array.isArray(data.formIds)) {
    throw new Error('Form IDs must be an array');
  }
  // Additional validation can be added as necessary
}

const addProduct = async (productData, productImage) => {
  console.log('Product data received:', productData);

  const token = localStorage.getItem('authToken');
  if (!token) {
    throw new Error('No authentication token found.');
  }

  try {
    // Validate product data
    validateProductData(productData);

    // Construct the product payload
    const productPayload = {
      productName: productData.productName,
      hsnCode: productData.hsnCode,
      categoryId: Number(productData.categoryId),
      varietyIds: productData.varietyIds?.map(item => Number(item.varietyId)) || [],
      formIds: productData.formIds?.map(item => Number(item.formId)) || [],
      productDescription: productData.productDescription || '',
      certifications: productData.certifications?.map(cert => ({
        certificationName: cert.certificationName,
        isCertified: cert.isCertified,
      })) || [],
      specifications: productData.specifications?.map(spec => ({
        specificationName: spec.specificationName,
        specificationValue: spec.specificationValue,
        specificationValueUnits: spec.specificationValueUnits,
      })) || [],
    };

    console.log('Product payload:', productPayload);

    // Add the product
    const response = await axios.post(API_URL, productPayload, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    const productId = response.data.productId;
    console.log('Product added successfully with ID:', productId);

    // Handle product image upload if available
    if (productImage) {
      console.log('Preparing to upload product image...');
      const imageFile = blobToFile(productImage, productData.productName);
      console.log('Converted image file:',  productImage);

      const formData = new FormData();
      formData.append('productImage', productImage);//

      try {
        const uploadResponse = await axios.post(`${UPLOAD_IMAGE_URL}/${productId}`, formData, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        console.log('Image uploaded successfully:', uploadResponse.data);
      } catch (uploadError) {
        console.error(
          'Error during image upload:',
          uploadError.response ? uploadError.response.data : uploadError.message
        );
        throw new Error('Image upload failed.');
      }
    } else {
      console.warn('No product image provided.');
    }

    return response.data;
  } catch (error) {
    console.error(
      'Error adding product:',
      error.response ? error.response.data : error.message
    );
    throw error;
  }
};

export { addProduct };
