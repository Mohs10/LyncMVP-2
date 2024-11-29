import axios from "axios";

const API_URL = 'http://lyncorganikness.ap-south-1.elasticbeanstalk.com/api/generateToken';

class AuthService {
  // Login method
  async login(username, password) {
    const response = await axios
      .post(API_URL, {
        username,
        password,
      });
    // Assuming the token is directly in response.data
    const token = response.data.token || response.data;
    if (token) {
      localStorage.setItem("authToken", token); // Store the token in localStorage
    }
    return token;
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
