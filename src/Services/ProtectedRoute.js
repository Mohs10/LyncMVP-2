import React from "react";
import { Navigate } from "react-router-dom";



const ProtectedRoute = ({
  isAuthenticated,
  authenticationPath,
  element,
}) => {
  if (isAuthenticated) {
    return element; 
  } else {
    return <Navigate to={authenticationPath} />;
  }
};

export default ProtectedRoute;