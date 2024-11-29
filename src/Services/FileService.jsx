import axios from 'axios';

const FileService = {
  /**
   * Upload a file.
   * @param {string} type - The type of file (e.g., 'certificate', 'cancelledCheque').
   * @param {string} userId - The ID of the user.
   * @param {File} file - The file to upload.
   * @param {Object} formData - The form data to include in the request.
   */
  uploadFile: async (type, userId, file, formData) => {
    try {
      const token = localStorage.getItem('authToken'); // Get the auth token
      
      // Create FormData for the request
      const uploadFormData = new FormData();
      uploadFormData.append('file', file);
      uploadFormData.append('sellerBuyerDTO', JSON.stringify(formData));

      const response = await axios.put(
        `http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/editUser/${userId}`,
        uploadFormData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        }
      );

      console.log(`${type} uploaded successfully.`);
      return response.data;
    } catch (err) {
      console.error(`Error uploading ${type}:`, err);
      throw err;
    }
  },

  /**
   * Download a file.
   * @param {string} fileUrl - The URL of the file to download.
   * @param {string} fileName - The name of the downloaded file.
   */
  downloadFile: async (fileUrl, fileName) => {
    try {
      const token = localStorage.getItem('authToken'); // Get the auth token

      const response = await axios.get(fileUrl, {
        responseType: 'blob', // Ensure we get the file as a Blob
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      // Trigger file download
      const fileURL = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = fileURL;
      link.setAttribute('download', fileName); // Specify file name
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      console.log(`${fileName} downloaded successfully.`);
    } catch (err) {
      console.error(`Error downloading ${fileName}:`, err);
      throw err;
    }
  },
};

export default FileService;
