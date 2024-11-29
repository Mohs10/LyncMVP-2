import axios from "axios";

const API_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/allUsers";

const SellerBuyerService = {
  // Fetch all users
  fetchAllUsers: async () => {
    const token = localStorage.getItem("authToken"); // Get the token

    try {
      const response = await axios.get(API_URL, {
        headers: {
          Authorization: `Bearer ${token}`, // Include the token in the header
        },
      });
      return response.data; // Return the fetched data
    } catch (error) {
      throw new Error(error.response ? error.response.data.message : error.message);
    }
  },

  // Find user details by ID
  findUserById: async (userId) => {
    const token = localStorage.getItem("authToken"); // Get the token
    if (!userId) throw new Error("User ID is required.");

    try {
      const allUsers = await SellerBuyerService.fetchAllUsers(); // Fetch all users
      const user = allUsers.find((user) => user.userId === userId); // Find the user by ID
      if (!user) {
        throw new Error(`User with ID ${userId} not found.`);
      }
      return user; // Return the found user
    } catch (error) {
      throw new Error(error.response ? error.response.data.message : error.message);
    }
  },
};

export default SellerBuyerService;
