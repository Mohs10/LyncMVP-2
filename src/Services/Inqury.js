const BASE_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com';

const InquiriesService = {
  getSellerDetails: async (productId, productFormId, productVarietyId) => {
    if (!productId || !productFormId || !productVarietyId) {
      throw new Error('Missing required parameters for getSellerDetails');
    }
    const token = localStorage.getItem('authToken');
    const response = await axios.get(
      `${BASE_URL}/auth/admin/sellerSellingProduct/${productId}/${productFormId}/${productVarietyId}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    return response.data;
  },
};

export default InquiriesService;
