import React from 'react';
import { Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom'; // Import useNavigate from react-router-dom
export const Login = () => {
  const navigate = useNavigate(); // Initialize useNavigate

  const handleLoginClick = () => { 
    navigate('/logintype'); // Navigate to the Logintype component
  };
 

  return ( 
    <div className="d-flex flex-column vh-100">
      <div
        className="d-flex flex-column justify-content-center align-items-center flex-grow-1 text-center pt-5"
        style={{
          maxWidth: '380px', // Adjust width as needed
          maxHeight: '500px',
          margin: 'auto', // Center horizontally
          padding: '20px', // Add padding inside the box
          backgroundColor: 'rgba(255, 255, 255, 0.8)', // Semi-transparent white background
          borderRadius: '8px', // Rounded corners for the box
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)', // Optional: Add shadow for better visibility
          position: 'relative', // Ensure that the box stays within the parent div
          height: 'auto',
          width: '90%',
          backdropFilter: 'blur(1px)',
          zIndex: 1 // Ensure the box is above the background image
        }}
      >
        <div
          className="rounded-circle bg-warning d-flex justify-content-center align-items-center"
          style={{ width: '50px', height: '50px', boxShadow: '0 4px 4px rgba(0, 0, 0, 0.3)' }}
        >
          <span className="text-white h4">L</span> {/* Letter 'L' inside the circle */}
        </div>
        <br />

        {/* Welcome Heading */}
        <h3 className="mt-3">Welcome To Lyncc!</h3>
        <p className="text-muted">Our gateway to organic, natural & <br />sustainable produce.</p>

        {/* Login Button */}
        <Button
          variant="dark"
          className="mt-3 w-100"
          style={{ maxWidth: '300px' }}
          onClick={handleLoginClick} // Add onClick handler for navigation
        >
          Login
        </Button>
      </div>

      {/* Bottom Image that covers the whole bottom area */}
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
          zIndex: -1 
        }}
      ></div>
    </div>
  );
};
export default Login;