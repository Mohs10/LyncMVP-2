import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown, faSearch } from '@fortawesome/free-solid-svg-icons';
import profilePic from '../assets/Profile-pic.png';
import './Profile.css';

const Topbar = ({ title, onSearchChange, isSidebarCollapsed }) => {
  const handleSearchChange = (event) => {
    onSearchChange(event.target.value);
  };

  return (
    <nav className={`navbar navbar-expand-lg navbar-light bg-light fixed-top ${isSidebarCollapsed ? 'expanded' : ''}`}>
      <div className="container-fluid">
        <a className="navbar-brand" href="#">
          {title}
        </a>

        <div className="ml-auto d-flex align-items-center">
          <form className="d-flex align-items-center me-3">
            <div className="input-group">
              <input
                type="text"
                className="form-control search-bar"
                placeholder="Search"
                onChange={handleSearchChange}
              />
            </div>
          </form>

          <div className="d-flex align-items-center">
            <img
              src={profilePic}
              alt="Profile"
              className="rounded-circle me-2"
              style={{ width: '40px', height: '40px' }}
            />
            <span className="d-none d-sm-block">Username</span>
            <FontAwesomeIcon icon={faChevronDown} className="ms-2" />
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Topbar;
