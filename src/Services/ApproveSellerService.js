// ApproveSellerService.js
import axios from "axios";

const API_BASE_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com";

export const approveSeller = async (snId) => {
  const token = localStorage.getItem("authToken"); // Replace with your token management logic

  if (!token) {
    throw new Error("Authentication token not found.");
  }

  try {
    const response = await axios.post(
      `${API_BASE_URL}/auth/admin/adminSelectsSeller/${snId}`,
      {}, // Body can be empty or include additional data if required
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );

    return response.data; // Handle API response as required
  } catch (error) {
    console.error("Error approving seller:", error);
    throw new Error(
      error.response?.data?.message || "Already Approved Seller"
    );
  }
};

export default { approveSeller };
