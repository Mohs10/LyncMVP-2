import axios from "axios";

const API_URL = "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/generateToken";

class AuthService {
  // Login method
  login(username, password) {
    return axios
      .post(API_URL, {
        username,
        password,
      })
      .then(response => {
        // Assuming the token is directly in response.data
        const token = response.data.token || response.data;

        if (token) {
          localStorage.setItem("authToken", token); // Store the token in localStorage
        }

        return token; // Return the token
      });
  }

  // Logout method
  logout() {
    localStorage.removeItem("authToken"); // Remove the token on logout
  }

  // Get the current user from localStorage
  getCurrentUser() {
    const token = localStorage.getItem('authToken');
    console.log(token); // Log the token
    return token ? { token } : null; // Return token if available
  }
}

export default new AuthService();
