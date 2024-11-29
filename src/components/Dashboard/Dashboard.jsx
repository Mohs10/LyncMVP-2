import React, { useEffect } from 'react';
import Topbar from './Topbar';
import getAdminDetails from '../../Services/AdminService'; // Import the service

const Dashboard = () => {

  useEffect(() => {
    const fetchAdminDetails = async () => {
      try {
        const details = await getAdminDetails(); // Fetch admin details
        console.log('Admin Details:', details);
        const storedAdminDetails = localStorage.getItem('adminDetails');

 // Log the details to the console
      } catch (error) {
        console.error('Error fetching admin details:', error); // Log any error to the console
      }
    };

    fetchAdminDetails(); // Call the function on component mount
  }, []);

  return (
    <div className="d-flex flex-column">
      {/* Topbar */}
      <Topbar title="Dashboard" userName="Neha Sharma" showSearchBar={true} />
      
      {/* Main Content Area */}
      <div className="container-fluid my-4" id="main">
        <div className="row">
          <h1>Welcome to the Dashboard</h1>
          <p>Your dashboard content goes here.</p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
