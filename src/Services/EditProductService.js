import axios from 'axios';

const EDIT_PRODUCT_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/editProduct';
const UPLOAD_IMAGE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/uploadProductImage';

// Function to update a product by ID and upload image
export const editProductById = async (productId, productData, imageFile) => {
    console.log('Updating product with ID:', productId);
    console.log(' product data:', productData);

    // Construct the productPayload
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

    console.log('productPayload:', productPayload);


    const token = localStorage.getItem('authToken');
        if (!token) {
        throw new Error('No token found. Please login to continue.');
    }

    try {
        // Update product data first with the structured payload
        const response = await axios.post(`${EDIT_PRODUCT_URL}/${productId}`, productPayload, {
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
        });

        console.log('Product updated successfully:', response.data);
        console.log('imageFile:', imageFile);

        // If there is an image file, upload it
        if (imageFile && imageFile instanceof File && imageFile.size > 0) {
            console.log('Uploading image...');
            console.log('Uploading:', imageFile);
        
            await uploadProductImage(productId, imageFile);
        } else {
            console.log('Image file is either empty or not provided. Skipping image upload.');
        }
        
        
        

        return response.data;
    } catch (error) {
        return handleError(error, 'Error updating product');
    }
};

// Function to upload product image
const uploadProductImage = async (productId, imageFile) => {
    console.log('Uploading image for product with ID:', productId);

    const token = getToken();
    if (!token) {
        throw new Error('No token found. Please login to continue.');
    }

    const formData = new FormData();
    console.log('In image Uploading:', imageFile);

    formData.append('productImage', imageFile);

    try {
        const response = await axios.post(`${UPLOAD_IMAGE_URL}/${productId}`, formData, {
            headers: {
                Authorization: `Bearer ${token}`,
                // 'Content-Type': 'multipart/form-data',
            },
        });

        console.log('Image uploaded successfully:', response.data);
        return response.data;
    } catch (error) {
        return handleError(error, 'Error uploading image');
    }
};

// Function to retrieve the token from local storage
const getToken = () => {
    try {
        const token = localStorage.getItem('authToken');
        console.log('Token:', token); // Debug token
        return token;
    } catch (error) {
        console.error('Failed to retrieve token:', error);
        return null;
    }
};

// Improved error handling function
const handleError = (error, message) => {
    console.error('Error details:', error); // Additional logging for debugging

    if (error.response) {
        console.error(`${message}:`, error.response.data);
        throw new Error(`Error: ${error.response.status} - ${error.response.data.message || error.response.data}`);
    } else if (error.request) {
        console.error('No response received:', error.request);
        throw new Error('Network error, please check your connection.');
    } else {
        console.error('Error:', error.message);
        throw new Error(`Error: ${error.message}`);
    }
};

export default { editProductById, uploadProductImage };
