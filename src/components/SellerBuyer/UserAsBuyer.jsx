import React, { useState } from 'react';
import Product from './Product';
import Query from './Query';
import Order from './Order';
import Transaction from './Transaction';

import riceImage from '../../assets/Product2.png'; // Adjust paths as needed
import turmericImage from '../../assets/Product2.png';
import pepperImage from '../../assets/Product2.png';
import chilliImage from '../../assets/Product2.png';

const UserDashboard = () => {
  const [isBuyer, setIsBuyer] = useState(true);
  const [activeTab, setActiveTab] = useState('Product');

  // Define buyerProducts and sellerProducts arrays
  const buyerProducts = [
    { name: 'Rice', img: riceImage, stock: 100 },
    { name: 'Turmeric', img: turmericImage, stock: 50 },
    { name: 'Pepper', img: pepperImage, stock: 20 },
   
    // Add more products as needed
  ];

  const sellerProducts = [
    { name: 'Wheat', img: riceImage, stock: 300 },
    { name: 'Spices', img: turmericImage, stock: 100 },
    { name: 'Herbs', img: pepperImage, stock: 50 },
    // Add more products as needed
  ];

  const handleToggle = (role) => {
    setIsBuyer(role === 'buyer');
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Product':
        return <Product products={isBuyer ? buyerProducts : sellerProducts} />;
      case 'Query':
        return <Query queries={isBuyer ? buyerQueries : sellerQueries} />;
      case 'Order':
        return <Order />;
      case 'Transaction':
        return <Transaction />;
      default:
        return <div>Select a menu to view content</div>;
    }
  };

  // Define buyerQueries and sellerQueries arrays (from previous steps)
  const buyerQueries = [
    { id: '344433', name: 'Aanjali Mehta', date: '26th July 2024', location: 'Maharashtra, Pune', status: 'Accepted', profilePic: 'path/to/profile1.png' },
    // Add more sample queries for Buyer if needed
  ];

  const sellerQueries = [
    { id: '122334', name: 'Rajesh Kumar', date: '15th August 2024', location: 'Delhi, India', status: 'Pending', profilePic: 'path/to/profile2.png' },
    { id: '122334', name: 'Rajesh Kumar', date: '15th August 2024', location: 'Delhi, India', status: 'Pending', profilePic: 'path/to/profile2.png' },
  ];

  return (
    <div className="container-fluid mt-4">
    <div className="row">
      <div className="col-12 text-center mb-4">
        <div className="btn-slider-container">
        <button
              className={`btn-toggle ${isBuyer ? 'active' : ''}`}
              onClick={() => handleToggle('buyer')}
            >
            User as Buyer
          </button>
          <button
              className={`btn-toggle ${!isBuyer ? 'active' : ''}`}
              onClick={() => handleToggle('seller')}
            >
              User as Seller
            </button>
        </div>
      </div>
      </div>
      <div className="menu-container">
        {['Product', 'Query', 'Order', 'Transaction'].map((tab) => (
          <button
            key={tab}
            className={`menu-tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </button>
        ))}
      </div>
      <div className="tab-content">{renderTabContent()}</div>
    </div>
   
  );
};

export default UserDashboard;
