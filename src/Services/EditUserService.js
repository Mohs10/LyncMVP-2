import axios from 'axios';

const BASE_API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/';
const EDIT_USER_API_URL = `${BASE_API_URL}editUser/`;
const UPDATE_PROFILE_PICTURE_API_URL = `${BASE_API_URL}updateProfilePicture/`;
const UPLOAD_CERTIFICATE_API_URL = `${BASE_API_URL}uploadCertificate/`;
const UPLOAD_CANCELLED_CHEQUE_API_URL = `${BASE_API_URL}uploadCancelledCheque/`;

const EditUserService = {
  // Edit user details with optional file uploads
  editUser: async (userId, userData, profilePicture, certificate, cancelledCheque) => {
    console.log('Preparing to send request:', userData);

    const token = localStorage.getItem('authToken');
    if (!token) throw new Error('No authentication token found.');

    try {
      // Prepare file uploads if files are provided
      if (profilePicture) {
        await EditUserService.updateProfilePicture(userId, profilePicture);
      }

      if (certificate) {
        await EditUserService.uploadCertificate(userId, certificate);
      }

      if (cancelledCheque) {
        await EditUserService.uploadCancelledCheque(userId, cancelledCheque);
      }

      // Once the files are uploaded, update the user details
      const response = await axios.put(
        `${EDIT_USER_API_URL}${userId}`,
        userData,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      console.log('Response:', response.data);
      return response.data;
    } catch (error) {
      handleError(error, 'Error editing user');
    }
  },

  // Update profile picture
  updateProfilePicture: async (userId, profilePicture) => {
    if (!profilePicture) throw new Error('Profile picture is required');
    const token = localStorage.getItem('authToken');
    if (!token) throw new Error('No authentication token found.');

    const form = new FormData();
    form.append('profilePicture', profilePicture);

    try {
      const response = await axios.put(
        `${UPDATE_PROFILE_PICTURE_API_URL}${userId}`,
        form,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        }
      );
      console.log('Profile Picture Response:', response.data);
      return response.data;
    } catch (error) {
      handleError(error, 'Error updating profile picture');
    }
  },

  // Upload certificate
  uploadCertificate: async (userId, certificate) => {
    if (!certificate) throw new Error('Certificate is required');
    const token = localStorage.getItem('authToken');
    if (!token) throw new Error('No authentication token found.');

    const form = new FormData();
    form.append('certificate', certificate);

    try {
      const response = await axios.put(
        `${UPLOAD_CERTIFICATE_API_URL}${userId}`,
        form,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        }
      );
      console.log('Certificate Response:', response.data);
      return response.data;
    } catch (error) {
      handleError(error, 'Error uploading certificate');
    }
  },

  // Upload cancelled cheque
  uploadCancelledCheque: async (userId, cancelledCheque) => {
    if (!cancelledCheque) throw new Error('Cancelled cheque is required');
    const token = localStorage.getItem('authToken');
    if (!token) throw new Error('No authentication token found.');

    const form = new FormData();
    form.append('cancelledCheque', cancelledCheque);

    try {
      const response = await axios.put(
        `${UPLOAD_CANCELLED_CHEQUE_API_URL}${userId}`,
        form,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        }
      );
      console.log('Cancelled Cheque Response:', response.data);
      return response.data;
    } catch (error) {
      handleError(error, 'Error uploading cancelled cheque');
    }
  },
};

// Utility function to handle API errors
function handleError(error, message) {
  if (error.response) {
    console.error(`${message}:`, error.response.data);
    throw new Error(`Error: ${error.response.status} - ${error.response.data}`);
  } else if (error.request) {
    console.error('No response received:', error.request);
    throw new Error('Network error, please check your connection.');
  } else {
    console.error(`${message}:`, error.message);
    throw new Error(error.message || 'An unknown error occurred');
  }
}

export default EditUserService;
