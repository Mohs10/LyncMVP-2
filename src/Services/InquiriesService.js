// import axios from "axios";

// const BASE_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin";

// // Create an Axios instance with default configuration
// const axiosInstance = axios.create({
//   baseURL: BASE_URL,
// });

// // Add a request interceptor to include the token in every request
// axiosInstance.interceptors.request.use(
//   (config) => {
//     const token = localStorage.getItem("authToken");
//     console.log("Token in request: ", token); // Debugging step

//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`;
//     } else {
//       throw new Error("Authentication token is missing or expired.");
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

// const InquiriesService = {
//   // Fetch all inquiries
//   fetchAllInquiries: async () => {
//     try {
//       const response = await axiosInstance.get("/allInquiries");
//       return response.data; // Return the fetched inquiries data
//     } catch (error) {
//       // Handle server and network errors
//       console.error("Error fetching inquiries:", error.response ? error.response : error);
//       throw new Error(
//         error.response ? error.response.data.message : "An error occurred while fetching inquiries."
//       );
//     }
//   },

//   // Fetch inquiry details by ID
//   getInquiryById: async (inquiryId) => {
//     if (!inquiryId) {
//       throw new Error("Inquiry ID is required.");
//     }

//     try {
//       const response = await axiosInstance.get(`/getInquiryById/${inquiryId}`);
//       return response.data; // Return inquiry details
//     } catch (error) {
//       console.error("Error fetching inquiry details:", error.response ? error.response : error);
//       throw new Error(
//         error.response
//           ? error.response.data.message
//           : "An error occurred while fetching inquiry details."
//       );
//     }
//   },
// };

// export default InquiriesService;
// import axios from "axios";

// // Base URL for the API
// const BASE_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin";

// // Create an Axios instance for API requests
// const axiosInstance = axios.create({
//   baseURL: BASE_URL,
// });

// // Add an interceptor to include the authorization token
// axiosInstance.interceptors.request.use(
//   (config) => {
//     const token = localStorage.getItem("authToken"); // Retrieve token from localStorage
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`; // Include token in headers
//     } else {
//       throw new Error("Authentication token is missing or expired.");
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

// // Service methods to interact with API endpoints
// const InquiriesService = {
//   // Fetch all inquiries
//   fetchAllInquiries: async () => {
//     try {
//       const response = await axiosInstance.get("/allInquiries");
//       return response.data;
//     } catch (error) {
//       console.error("Error fetching inquiries:", error.response || error);
//       throw new Error(
//         error.response?.data?.message || "An error occurred while fetching inquiries."
//       );
//     }
//   },

//   // Fetch inquiry details by ID
//   getInquiryById: async (inquiryId) => {
//     if (!inquiryId) {
//       throw new Error("Inquiry ID is required.");
//     }
//     try {
//       const response = await axiosInstance.get(`/getInquiryById/${inquiryId}`);
//       return response.data;
//     } catch (error) {
//       console.error("No Product and seller of this inquiryid", error.response || error);
//       throw new Error(
//         "No Product and seller of this inquiryid" || "An error occurred while fetching inquiry details."
//       );
//     }
//   },
// };

// export default InquiriesService;
import axios from "axios";

// Base URL for the API
const BASE_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin";

// Create an Axios instance for API requests
const axiosInstance = axios.create({
  baseURL: BASE_URL,
});

// Add an interceptor to include the authorization token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken"); // Retrieve token from localStorage
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // Include token in headers
    } else {
      throw new Error("Authentication token is missing or expired.");
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Service methods to interact with API endpoints
const InquiriesService = {
  // Fetch all inquiries
  fetchAllInquiries: async () => {
    try {
      const response = await axiosInstance.get("/allInquiries");
      return response.data;
    } catch (error) {
      console.error("Error fetching inquiries:", error.response || error);
      throw new Error(
        error.response?.data?.message || "An error occurred while fetching inquiries."
      );
    }
  },

  // Fetch inquiry details by ID
  getInquiryById: async (inquiryId) => {
    if (!inquiryId) {
      throw new Error("Inquiry ID is required.");
    }
    try {
      const response = await axiosInstance.get(`/getInquiryById/${inquiryId}`);
      return response.data;
    } catch (error) {
      console.error("No Product and seller of this inquiryid", error.response || error);
      throw new Error(
        "No Product and seller of this inquiryid" || "An error occurred while fetching inquiry details."
      );
    }
  },

  // Send inquiry to seller
  sendInquiryToSeller: async (inquiryId, payload) => {
    if (!inquiryId) {
      throw new Error("Inquiry ID is required.");
    }
    try {
      const response = await axiosInstance.post(`/sendInquiryToSeller/${inquiryId}`, payload);
      return response.data;
    } catch (error) {
      console.error("Error sending inquiry:", error.response || error);
      throw new Error(
        error.response?.data?.message || "An error occurred while sending the inquiry."
      );
    }
  },
};

export default InquiriesService;


