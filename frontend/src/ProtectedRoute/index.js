import React from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode'; 

function ProtectedRoute({ children, requiredRole }) {
  const token = localStorage.getItem('token');

  if (!token) {
    return <Navigate to="/Login" replace />;
  }

  try {
    const decoded = jwtDecode(token);
    const roles = decoded.roles || [];

    if (requiredRole && !roles.includes(requiredRole)) {
      return <Navigate to="/" replace />;
    }

    return children;
  } catch (error) {
    console.error("Invalid token:", error);
    return <Navigate to="/Login" replace />;
  }
}

export default ProtectedRoute;
