import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useLocation, useNavigate } from 'react-router-dom'; // Import useNavigate for redirection
import AuthService from '../../Services/AuthService'; // Adjust the import path as necessary

import { 
  faDashboard, 
  faUsers, 
  faListAlt,
  faBox, 
  faQuestionCircle, 
  faShoppingCart, 
  faCreditCard, 
  faBell, 
  faSignOutAlt, 
  faChevronLeft,
  faPlus
} from '@fortawesome/free-solid-svg-icons';

import logo from '../../assets/logo.png'; // Assuming your logo is saved in the assets folder

const Sidebar = () => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation(); // Hook to get the current route
  const navigate = useNavigate(); // Hook for navigation
  const [activeItem, setActiveItem] = useState(location.pathname); // Initialize active state based on the current route

  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  const handleSetActive = (path) => {
    setActiveItem(path); // Set the clicked item as active
  };

  const handleLogout = () => {
    AuthService.logout(); // Call the logout method
    navigate('/'); // Redirect to the login page
  };

  return (
   <div className={`d-flex flex-column bg-dark text-light p-3 sidebar ${collapsed ? 'collapsed' : ''}`}>
      {/* Logo Section */}
      <div className="d-flex align-items-right justify-content-between mb-4 p-2">
        
        <div className="rounded-circle d-inline-block logo-icon">
          <img src={logo} alt="Logo" className="img-fluid rounded-circle logo-img" />
        </div>
        <button className="btn text-light ms-2" onClick={toggleSidebar}>
          <FontAwesomeIcon icon={collapsed ? faChevronLeft : faChevronLeft} />
        </button>
      </div>

      {/* Navigation Items */}
      <ul className="nav flex-column mb-auto">
        <li className="nav-item mb-4">
          <Link 
            to="/dashboard" 
            className={`nav-link d-flex align-items-center ${activeItem === '/dashboard' ? 'active' : ''}`}
            onClick={() => handleSetActive('/dashboard')}
          >
            <FontAwesomeIcon icon={faDashboard} className="me-2 fa-icon" />
            {!collapsed && "Dashboard"}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link 
            to="/users" 
            className={`nav-link d-flex align-items-center ${activeItem === '/users' ? 'active' : ''}`}
            onClick={() => handleSetActive('/users')}
          >
            <FontAwesomeIcon icon={faUsers} className="me-2 fa-icon" />
            {!collapsed && "Users"}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link 
            to="/product-list" 
            className={`nav-link d-flex align-items-center ${activeItem === '/product-list' ? 'active' : ''}`}
            onClick={() => handleSetActive('/product-list')}
          >
            <FontAwesomeIcon icon={faListAlt} className="me-2 fa-icon" />
            {!collapsed && "Product"}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link 
            to="/query" 
            className={`nav-link d-flex align-items-center ${activeItem === '/query' ? 'active' : ''}`}
            onClick={() => handleSetActive('/query')}
          >
            <FontAwesomeIcon icon={faQuestionCircle} className="me-2 fa-icon" />
            {!collapsed && "Query"}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link 
            to="/orders" 
            className={`nav-link d-flex align-items-center ${activeItem === '/orders' ? 'active' : ''}`}
            onClick={() => handleSetActive('/orders')}
          >
            <FontAwesomeIcon icon={faShoppingCart} className="me-2 fa-icon" />
            {!collapsed && "Orders"}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link 
            to="/transactions" 
            className={`nav-link d-flex align-items-center ${activeItem === '/transactions' ? 'active' : ''}`}
            onClick={() => handleSetActive('/transactions')}
          >
            <FontAwesomeIcon icon={faCreditCard} className="me-2 fa-icon" />
            {!collapsed && "Transactions"}
          </Link>
        </li>
        <li className="nav-item mb-4">
          <Link 
            to="/notifications" 
            className={`nav-link d-flex align-items-center ${activeItem === '/notifications' ? 'active' : ''}`}
            onClick={() => handleSetActive('/notifications')}
          >
            <FontAwesomeIcon icon={faBell} className="me-2 fa-icon" />
            {!collapsed && "Notifications"}
          </Link>
          
        </li>
       
        <li className="nav-item mb-4">
  <Link 
    to="/testimonial" 
    className={`nav-link d-flex align-items-center ${activeItem === '/testimonials' ? 'active' : ''}`}
    onClick={() => handleSetActive('/testimonial')}
  >
    <FontAwesomeIcon icon={faListAlt} className="me-2 fa-icon" />
    {!collapsed && "Testimonial"}
  </Link>
</li>


      </ul>

      <div className="mt-auto mb-4">
        <button onClick={handleLogout} className="nav-link text-secondary d-flex align-items-center">
          <FontAwesomeIcon icon={faSignOutAlt} className="me-2" />
          Log Out
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
