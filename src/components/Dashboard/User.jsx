import React, { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircle, faEye, faEdit, faTrash } from '@fortawesome/free-solid-svg-icons';
import { useNavigate, useLocation } from 'react-router-dom';
import Topbar from './Topbar';
import SellerBuyerService from '../../Services/SellerBuyerService';
import './Product_list.css';
import Pagination from '../Pagination/Pagination'; // Make sure the path is correct

import DefaultImage from '../../assets/Ellipse 3.png'; // Adjust path as needed

const UsersPerPage = 8;

const User = () => {
  const [filter, setFilter] = useState("both");
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [noResults, setNoResults] = useState(false);
  const token = localStorage.getItem('token');
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const page = location.state?.currentPage || 1;
    setCurrentPage(page);
  }, [location.state]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const userData = await SellerBuyerService.fetchAllUsers(token);
        setUsers(userData.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [token]);

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setCurrentPage(1);
  };

  const viewUserHandler = (userId) => {
    navigate(`/view-user/${userId}`);
  };

  const handleEditUser = (userId) => {
    navigate(`/edit-user/${userId}`, { state: { currentPage } });
  };

  const handleDeleteUser = (userId) => {
    console.log("Delete user:", userId);
  };

  const calculateDaysAgo = (date) => {
    const today = new Date();
    const registeredDate = new Date(date);
    const diffTime = Math.abs(today - registeredDate);
    return Math.floor(diffTime / (1000 * 60 * 60 * 24));
  };

  const filteredUsers = users.filter(user => {
    if (filter === "both") {
      return user.buyer || user.seller;
    }
    if (filter === "buyer") {
      return user.buyer && !user.seller;
    }
    if (filter === "seller") {
      return user.seller && !user.buyer;
    }
    return true;
  }).filter(user => {
    const searchTermLower = searchTerm.toLowerCase();
    return user.fullName.toLowerCase().includes(searchTermLower) || user.email.toLowerCase().includes(searchTermLower);
  });

  const indexOfLastUser = currentPage * UsersPerPage;
  const indexOfFirstUser = indexOfLastUser - UsersPerPage;
  const currentUsers = filteredUsers.slice(indexOfFirstUser, indexOfLastUser);
  const totalPages = Math.ceil(filteredUsers.length / UsersPerPage);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleSearch = (term) => {
    setSearchTerm(term);
    setNoResults(filteredUsers.length === 0 && term.trim() !== '');
    setCurrentPage(1);
  };

  useEffect(() => {
    setNoResults(filteredUsers.length === 0 && searchTerm.trim() !== '');
  }, [filteredUsers, searchTerm]);

  if (loading) {
    return <div>Loading users...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="d-flex flex-column">
 <Topbar title="User List" showSearchBar={true} handleProductSearch={() => {}} handleUserSearch={handleSearch} />

      {noResults && (
        <div style={{
          left: '0',
          fontSize: '1.2rem',
          color: 'red',
          textAlign: 'center',
          marginTop: '15px',
        }}>
          No results found
        </div>
      )}

      <div className="container-fluid my-4" >
        <div className="btn-group-container">
          <div className="btn-group mb-3" role="group" aria-label="Filter buttons">
            <button className={`btn btn-outline-warning ${filter === "seller" ? 'active' : ''}`} onClick={() => handleFilterChange("seller")}>
              Sellers
            </button>
            <button className={`btn btn-outline-warning ${filter === "buyer" ? 'active' : ''}`} onClick={() => handleFilterChange("buyer")}>
              Buyers
            </button>
            <button className={`btn btn-outline-warning ${filter === "both" ? 'active' : ''}`} onClick={() => handleFilterChange("both")}>
              Both
            </button>
          </div>
        </div>

        <div className="table-responsive">
          <table className="table table-hover custom-table" style={{
            borderRadius: '15px',
            border: '1px solid #E9E9E9',
            overflow: 'hidden',
            backgroundColor: '#F6F6F6',
          }}>
            <thead className="table-header">
              <tr>
                <th>User Name</th>
                <th>Email</th>
                <th>Registered</th>
                <th>State</th>
                <th>Company</th>
                <th>Orders</th>
                <th>Waive Sample Fee</th>
              </tr>
            </thead>
            <tbody>
              {currentUsers.map(user => (
                <tr key={user.userId}>
                  <td style={{ borderBottom: '1px solid #E9E9E9' }}>
                    <div className="align-items-center d-flex custom-table">
                    <img
  src={user.profilePictureUrl || DefaultImage}
  alt={user.fullName}
  className="product-image rounded-circle me-2"
  style={{ width: '25px', height: '25px', cursor: 'pointer' }}
  onClick={() => handleEditUser(user.userId)}
/>

                      <div style={{ fontSize: '12px' }}>
                        <span>{user.fullName}</span>
                        <br />
                        <FontAwesomeIcon
                          icon={faCircle}
                          size="xs"
                          className={`me-1 ${user.seller ? 'text-success' : 'text-primary'}`}
                        />
                        <span style={{ color: '#A8A4A4', fontSize: '12px' }}>
                          {user.seller ? 'Seller' : 'Buyer'}
                        </span>
                      </div>
                    </div>
                  </td>
                  <td style={{ fontSize: '14px' }}>{user.email}</td>
                  <td style={{ fontSize: '14px' }}>
                    {new Date(user.createdAt).toLocaleDateString('en-GB', {
                      day: 'numeric',
                      month: 'short',
                      year: 'numeric',
                    })}
                    <br />
                    <small style={{ color: '#A8A4A4', fontSize: '12px' }}>
                      {calculateDaysAgo(user.createdAt)} days ago
                    </small>
                  </td>
                  <td style={{ fontSize: '14px' }}>{user.state}</td>
                  <td style={{ fontSize: '14px' }}>{user.companyName || 'N/A'}</td>
                  <td style={{ fontSize: '14px' }}>
                    <span className={`badge ${user.ordersPending > 0 ? 'badge-danger' : 'badge-success'}`}>
                      {user.ordersPending > 0 ? `${user.ordersPending} Pending` : 'No Pending'}
                    </span>
                  </td>
                  <td>
                    <div className="toggle-container" style={{
                      backgroundColor: '#e9e9e9',
                      width: '160px',
                      borderRadius: '15px',
                      display: 'flex',
                      alignItems: 'center',
                      padding: '5px',
                      position: 'relative',
                    }}>
                      <button className="btn btn-success" style={{
                        backgroundColor: 'limegreen',
                        color: 'black',
                        borderRadius: '15px',
                        border: 'none',
                        padding: '5px 15px',
                        marginRight: '5px',
                        fontSize: '12px',
                        zIndex: 1,
                      }}>
                        Enable
                      </button>
                      <button className="btn btn-danger" style={{
                        backgroundColor: '#e7c1c1',
                        color: 'black',
                        borderRadius: '15px',
                        fontSize: '12px',
                        border: 'none',
                        padding: '5px 15px',
                        zIndex: 1,
                      }}>
                        Disable
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </div>
    </div>
  );
};

export default User;
