import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // For navigation
import AuthService from '../Services/AuthService'; // Import the AuthService
import logo from '../assets/logo.png'; // Assuming your logo is saved in the assets folder
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'; // Import FontAwesome for icons
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons'; // Import eye and eye-slash icons

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false); // New loading state to prevent double submission
  const [showPassword, setShowPassword] = useState(false); // State to handle password visibility

  const navigate = useNavigate(); // React Router navigation

  const validateEmail = (email) => {
    // Using the RegExp pattern you provided
    const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return re.test(String(email).toLowerCase());
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Clear the error on a new submit attempt
    setError('');
    
    console.log('log1: Form submitted');

    // Simple validation for email and password
    if (username === '' || password === '') {
      setError('Please fill in all fields');
      console.log('log2: Fields are empty');
      return;
    }

    // Email validation using the provided RegExp
    if (!validateEmail(username)) {
      setError('Please enter a valid email address');
      console.log('log3: Invalid email format');
      return;
    }

    try {
      setLoading(true); // Set loading to true before making the request

      const token = await AuthService.login(username, password);

      // Navigate to the Dashboard after successful login
      navigate('/dashboard');
      console.log('log4: Navigation to dashboard');
    } catch (error) {
      console.error('log5: Login error', error);

      // Check if the error has a response (network or server issue)
      if (error.response && error.response.data) {
        setError('Invalid username or password'); // Server returned an error
      } else {
        setError('Invalid username or password.'); // Network error or unreachable server
      }
    } finally {
      setLoading(false); // Always stop loading after the request completes
    }
  };

  return (
    <div className="containers">
      <div className="wrapper">
        <div className="logo">
          <img src={logo} alt="Logo" />
        </div>
        <div className="title"></div>
        <form onSubmit={handleSubmit}>
          <div className="heading">User Login</div>

          {error && <p className="error">{error}</p>}

          <div className="row">
            <i className="fas fa-user"></i>
            <input
              type="text"
              placeholder="EMAIL"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={loading} // Disable input when loading
            />
          </div>

          <div className="row password-row">
            <i className="fas fa-lock"></i>
            <input
              type={showPassword ? 'text' : 'password'} // Toggle between text and password types
              placeholder="PASSWORD"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading} // Disable input when loading
            />
            <span
              className="toggle-password"
              onClick={() => setShowPassword(!showPassword)} // Toggle password visibility
            >
              <FontAwesomeIcon icon={showPassword ? faEyeSlash : faEye} />
            </span>
          </div>

          <div className="row button">
            <input
              type="submit"
              value={loading ? 'Logging in...' : 'Login'} // Show loading state on the button
              disabled={loading} // Disable the button during loading
            />
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
