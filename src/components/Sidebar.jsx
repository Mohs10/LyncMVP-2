import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faUsers, faBox, faShoppingBag, faShoppingCart, faWallet, faSignOutAlt, faBell, faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import AuthService from '../Services/AuthService'; // Import AuthService
import logo from '../assets/logo.png'; // Your logo path
import './Profile.css'; // Import the CSS file for styles

const Sidebar = () => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const [activeItem, setActiveItem] = useState(location.pathname);

  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  const handleSetActive = (path) => {
    setActiveItem(path);
  };

  const handleLogout = () => {
    AuthService.logout(); // Clear the token
    navigate('/emaillogin'); // Redirect to the login page
  };

  return (
    <div className={`sidebar ${collapsed ? 'collapsed' : ''}`}>
      {/* Logo Section */}
      <div className="logo-section">
        <div className="logo-container">
          <img src={logo} alt="Logo" className="logo-img" />
        </div>
        <button
          className="btn toggle-btn"
          onClick={toggleSidebar}
          style={{ color: 'white' }}
        >
          <FontAwesomeIcon icon={collapsed ? faChevronRight : faChevronLeft} />
        </button>
      </div>

      {/* Navigation Links */}
      <ul className="nav flex-column mb-auto">
        <li className="nav-item mb-4">
          <Link
            to="/home"
            className={`nav-link ${activeItem === '/home' ? 'active' : ''}`}
            onClick={() => handleSetActive('/home')}
          >
            <FontAwesomeIcon icon={faHome} className="fa-icon" />
            {!collapsed && 'Home'}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link
            to="/profile"
            className={`nav-link ${activeItem === '/profile' ? 'active' : ''}`}
            onClick={() => handleSetActive('/profile')}
          >
            <FontAwesomeIcon icon={faUsers} className="fa-icon" />
            {!collapsed && 'Profile'}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link
            to="/marketplace"
            className={`nav-link ${activeItem === '/marketplace' ? 'active' : ''}`}
            onClick={() => handleSetActive('/marketplace')}
          >
            <FontAwesomeIcon icon={faBox} className="fa-icon" />
            {!collapsed && 'Marketplace'}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link
            to=""
            className={`nav-link ${activeItem === '' ? 'active' : ''}`}
            // onClick={() => handleSetActive('')}
          >
            <FontAwesomeIcon icon={faShoppingBag} className="fa-icon" />
            {!collapsed && 'Queries'}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link
            to=""
            className={`nav-link ${activeItem === '/orders' ? 'active' : ''}`}
            // onClick={() => handleSetActive('/orders')}
          >
            <FontAwesomeIcon icon={faShoppingCart} className="fa-icon" />
            {!collapsed && 'Orders'}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link
            to=""
            className={`nav-link ${activeItem === '' ? 'active' : ''}`}
            // onClick={() => handleSetActive('')}
          >
            <FontAwesomeIcon icon={faWallet} className="fa-icon" />
            {!collapsed && 'Transactions'}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link
            to=""
            className={`nav-link ${activeItem === '' ? 'active' : ''}`}
            // onClick={() => handleSetActive('')}
          >
            <FontAwesomeIcon icon={faBell} className="fa-icon" />
            {!collapsed && 'Notifications'}
          </Link>
        </li>
      </ul>

      {/* Logout Section */}
      <div className="logout-section">
        <button className="nav-link logout-btn" onClick={handleLogout}>
          <FontAwesomeIcon icon={faSignOutAlt} className="fa-icon" />
          {!collapsed && 'Log Out'}
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
