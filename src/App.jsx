import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';

const Login = lazy(() => import('./components/Login'));
const Logintype = lazy(() => import('./components/Logintype'));
const EmailLogin = lazy(() => import('./components/EmailLogin'));
const Profile = lazy(() => import('./components/Profile'));
const Home = lazy(() => import('./components/Home'));
const Marketplace = lazy(() => import('./components/Marketplace'));

const App = () => {
  const token = localStorage.getItem('token'); // Ensure this matches your AuthService

  return (
    <Router>
      <Suspense fallback={<div>Loading...</div>}>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Login />} />
          <Route path="/logintype" element={<Logintype />} />
          <Route path="/emaillogin" element={<EmailLogin />} />

          {/* Private Routes */}
          {/* <Route
            path="/home"
            element={token ? <Home /> : <Navigate to="/" replace />}
          /> */}
          <Route
              path="/home"
              element={
                
                  <Home />
               
              }
            />
            <Route
              path="/profile"
              element={
                
                  <Profile />
               
              }
            />
          <Route
              path="/marketplace"
              element={
                
                  <Marketplace />
               
              }
            />
        </Routes>
      </Suspense>
    </Router>
  );
};

export default App;






