// PrivateRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ children }) => {
  // Replace this with your actual authentication logic
  const isAuthenticated = localStorage.getItem('token') !== null;

  return isAuthenticated ? children : <Navigate to="/" />;
};

export default PrivateRoute;
