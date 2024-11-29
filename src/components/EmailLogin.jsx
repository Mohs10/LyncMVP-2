import React, { useState } from 'react';
import { Button } from 'react-bootstrap';
import { Form, InputGroup } from 'react-bootstrap';
import { BsEnvelopeFill } from 'react-icons/bs';
// import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
 // Import eye and eye-slash icons
import AuthService from '../Services/AuthService'; // Import the AuthService
import { useNavigate } from 'react-router-dom';
import './Profile.css';

export const EmailLogin = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false); 
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  // Function to validate email
  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  // Function to handle login
  const handleLogin = async (e) => {
    e.preventDefault();

    // Clear the error on a new submit attempt
    setError('');
    console.log('log1: Form submitted');

    // Simple validation for email and password
    if (email === '' || password === '') {
      setError('Email & password cannot be empty');
      console.log('log2: Fields are empty');
      return;
    }

    // Email validation
    if (!validateEmail(email)) {
      setError('Please enter a valid email address');
      console.log('log3: Invalid email format');
      return;
    }

    try {
      setLoading(true); // Start loading before making the request
      console.log('log4: Attempting login');

      // Use AuthService for login
      const data = await AuthService.login(email, password);

      // Save token to local storage
      localStorage.setItem('token', data.token);

      // Navigate to the dashboard or home page
      navigate('/home');
      console.log('log5: Navigation to home');
    } catch (error) {
      console.error('log6: Login error', error);

      // Check if the error has a response
      if (error.response && error.response.data) {
        setError(error.response.data.message || 'Invalid username or password');
      } else {
        setError('Invalid username or password.');
      }
    } finally {
      setLoading(false); // Always stop loading
    }
  };

  return (
    <div className="d-flex flex-column vh-100 position-relative">
      <div
        className="d-flex flex-column justify-content-center align-items-center flex-grow-1 text-center pt-5"
        style={{
          maxWidth: '380px',
          maxHeight: '500px',
          margin: 'auto',
          padding: '20px',
          backgroundColor: 'rgba(255, 255, 255, 0.8)',
          borderRadius: '8px',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
          position: 'relative',
          height: 'auto',
          width: '90%',
          backdropFilter: 'blur(1px)',
          zIndex: 1,
        }}
      >
        {/* Circular logo */}
        <div
          className="rounded-circle bg-warning d-flex justify-content-center align-items-center"
          style={{ width: '50px', height: '50px', boxShadow: '0 4px 4px rgba(0, 0, 0, 0.3)' }}
        >
          <span className="text-white h4">L</span> {/* Letter 'L' inside the circle */}
        </div>
        <br />

        {/* Login Heading */}
        <p className="mt-6" style={{ fontSize: '30px', fontWeight: '500' }}>
          Login
        </p>

        {/* Email Field */}
        <Form.Group className="mb-3 w-100" style={{ maxWidth: '300px' }} controlId="formEmail">
          <InputGroup
            className="input-group-lg"
            style={{
              width: '100%',
              maxWidth: '300px',
              boxShadow: '0 2px 2px rgba(0, 0, 0, 0.3)',
              borderRadius: '8px',
            }}
          >
            <Form.Control
              type="email"
              placeholder="Enter your Email"
              aria-label="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="custom-input"
              style={{
                backgroundColor: '#f5f6f7',
                borderRight: 'none',
                borderRadius: '8px 0 0 8px',
                fontSize: '14px',
                paddingRight: '40px',
                height: '40px',
              }}
            />
            <InputGroup.Text
              style={{
                backgroundColor: '#f5f6f7',
                borderLeft: 'none',
                borderRadius: '0 8px 8px 0',
                height: '40px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                padding: '0 15px',
              }}
            >
              <BsEnvelopeFill style={{ color: '#ccc' }} />
            </InputGroup.Text>
          </InputGroup>
        </Form.Group>

        {/* Password Field */}
        <Form.Group className="mb-3 w-100" style={{ maxWidth: '300px' }} controlId="formPassword">
          <InputGroup
            className="input-group-lg"
            style={{
              width: '100%',
              maxWidth: '300px',
              boxShadow: '0 2px 2px rgba(0, 0, 0, 0.3)',
              borderRadius: '8px',
            }}
          >
            <Form.Control
             type={showPassword ? 'text' : 'password'}
              placeholder="Enter your password"
              aria-label="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
              className="custom-input"
              style={{
                backgroundColor: '#f5f6f7',
                borderRight: 'none',
                borderRadius: '8px 0 0 8px',
                fontSize: '14px',
                paddingRight: '40px',
                height: '40px',
              }}
            />
            <InputGroup.Text
              style={{
                backgroundColor: '#f5f6f7',
                borderLeft: 'none',
                borderRadius: '0 8px 8px 0',
                height: '40px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                padding: '0 15px',
              }}
            >
              <span
              className="toggle-password"
              onClick={() => setShowPassword(!showPassword)} // Toggle password visibility
            >
              <FontAwesomeIcon icon={showPassword ? faEyeSlash  : faEye}
              style={{ color: showPassword ? "black" : "#ccc" }}
              >/</FontAwesomeIcon>
            </span>

            </InputGroup.Text>
          </InputGroup>
        </Form.Group>

        {/* Login Button */}
        <Button
          variant="dark"
          className="mt-3 w-100"
          style={{ maxWidth: '300px' }}
          onClick={handleLogin}
          disabled={loading} // Disable button when loading
        >
          {loading ? 'Logging in...' : 'Login'}
        </Button>

        {/* Error Message */}
        {error && (
          <p style={{ color: 'red', marginTop: '10px', fontSize: '14px' }}>
            {error}
          </p>
        )}
      </div>

      {/* Background Image */}
      <div
        style={{
          position: 'absolute',
          bottom: '0',
          left: '0',
          width: '100%',
          height: '100vh',
          backgroundImage: 'url(/assets/webb.jpg)',
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          opacity: 0.3,
          zIndex: -1,
        }}
      ></div>
    </div>
  );
};

export default EmailLogin;








