import React from 'react';
import { Navigate } from 'react-router-dom';

// PrivateRoute component to protect routes
const PrivateRoute = ({ children }) => {
  // Check if token exists in localStorage (or sessionStorage)
  const token = localStorage.getItem('authToken'); // You can use sessionStorage as well

  return token ? children : <Navigate to="/" />;
};

export default PrivateRoute;
