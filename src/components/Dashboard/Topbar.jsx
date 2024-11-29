import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBell, faChevronDown, faSearch, faSignOutAlt } from '@fortawesome/free-solid-svg-icons';
import getAdminDetails from '../../Services/AdminService'; // Assuming the service is here
import profilePic from '../../assets/Profile-pic.png'; // Replace this with your profile pic path
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import AuthService from '../../Services/AuthService'; // Adjust the import path as necessary
import { useNavigate } from 'react-router-dom'; // Import useNavigate for redirection


const Topbar = ({ title, showSearchBar = true, handleProductSearch, handleUserSearch, handleInactiveProductSearch }) => {
  const [adminName, setAdminName] = useState('Fetching...'); // Initial state as "Fetching..."
  const navigate = useNavigate(); // Hook for navigation

  useEffect(() => {
    // Fetch admin details when the component mounts
    const fetchAdmin = async () => {
      try {
        const adminDetails = await getAdminDetails();
        setAdminName(adminDetails.name);
      } catch (error) {
        console.error('Error fetching admin details:', error);
      }
    };
    fetchAdmin();
  }, []);

  const handleSearchChange = (e) => {
    const searchValue = e.target.value;
    if (title === "Product List") handleProductSearch(searchValue);
    else if (title === "User List") handleUserSearch(searchValue);
    else if (title === "Inactive Product") handleInactiveProductSearch(searchValue);
  };
  const handleLogout = () => {
    AuthService.logout(); // Call the logout method
    navigate('/'); // Redirect to the login page
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light" style={{ boxShadow: '0 -1px 9.8px rgba(170, 170, 170, 0.25)' }}>
      <div className="container-fluid">
        {/* Dynamic Title */}
        <a className="navbar-brand" href="#" style={{ fontSize: '20px', color: 'black', fontWeight: '600' }}>
          {title}
        </a>

        {/* Center Search Bar - Conditionally Render */}
        {showSearchBar && (
          <div className="d-none d-md-flex align-items-center w-50">
            <div className="search-wrapper" style={{ position: 'relative' }}>
              <input
                className="custom-form"
                placeholder={
                  title === "Product List"
                    ? "Search products..."
                    : title === "Inactive Product"
                    ? "Search inactive products..."
                    : "Search users..."
                }
                onChange={handleSearchChange}
              />
              <FontAwesomeIcon icon={faSearch} className="search-icon" aria-hidden="true" style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)' }} />
            </div>
          </div>
        )}

        {/* Right Side Icons and Profile */}
        <div className="ml-auto d-flex align-items-center">
          {/* Notification Icon */}
          <div className="nav-item me-3 d-none d-md-block">
            <FontAwesomeIcon icon={faBell} />
          </div>

          {/* Profile Picture and Name */}
          <div className="d-flex align-items-center">
  <img
    src={profilePic}
    alt="Profile"
    className="rounded-circle me-2"
    style={{ width: '40px', height: '40px' }}
  />
  <div className="dropdown">
    <button
      className="btn btn-link dropdown-toggle text-dark"
      id="profileDropdown"
      data-bs-toggle="dropdown"
      aria-expanded="false"
    >
      {adminName}
    </button>
    <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="profileDropdown">
      <li>
        <button
          onClick={handleLogout}
          className="dropdown-item text-secondary"
        >
          <FontAwesomeIcon icon={faSignOutAlt} className="me-2" />
          Log Out
        </button>
      </li>
    </ul>
  </div>
</div>
</div></div>
      {/* Search Bar for smaller screens */}
      {showSearchBar && (
        <div className="d-md-none mt-3 px-3">
          <input
            type="text"
            className="form-control"
            placeholder={
              title === "Product List"
                ? "Search products..."
                : title === "Inactive Product"
                ? "Search inactive products..."
                : "Search users..."
            }
            onChange={handleSearchChange}
            style={{ border: '2px solid #ffeb3b' }}
          />
        </div>
      )}
    </nav>
  );
};

export default Topbar;
