import axios from "axios";

const API_BASE_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com";

// export const submitProposalPrice = async (snId, amount) => {
//   const token = localStorage.getItem("authToken");

//   if (!token) {
//     throw new Error("Authentication token not found.");
//   }

//   try {
//     const response = await axios.post(
//       `${API_BASE_URL}/auth/admin/sendFinalPrice/${snId}`,
//       { finalPrice: amount }, // Pass finalPrice in the request body
//       {
//         headers: {
//           Authorization: `Bearer ${token}`,
//           "Content-Type": "application/json",
//         },
//       }
//     );

//     return response.data; // Handle the response as needed
//   } catch (error) {
//     console.error("Error submitting final price:", error);
//     throw new Error(
//       error.response?.data?.message || "Failed to submit final price. Try again."
//     );
//   }
// };
export const submitProposalPrice = async (snId, amount) => {
  const token = localStorage.getItem("authToken");

  if (!token) {
    throw new Error("Authentication token not found.");
  }

  try {
    const response = await axios.post(
      `${API_BASE_URL}/auth/admin/sendFinalPrice/${snId}`,
      { amount }, // Use 'amount' as the key to match the backend requirement
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );

    return response.data; // Handle the response as needed
  } catch (error) {
    console.error("Error submitting final price:", error);
    throw new Error(
      error.response?.data?.message || "Failed to submit final price. Try again."
    );
  }
};

