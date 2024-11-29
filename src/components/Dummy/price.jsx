import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import sellerImage from '../../assets/Profile-pic.png';
import productImage from '../../assets/Product.png';
import Topbar from '../Dashboard/Topbar';
import { sendFinalPrice, selectSeller } from '../../Services/PriceService'; // Correct import

const PriceDetails = () => {
  const location = useLocation();
  const priceDetails = location.state?.priceDetails || [];
  const [selectedSeller, setSelectedSeller] = useState(null);
  const [proposalPrice, setProposalPrice] = useState('');

  // Handle seller selection - Allow only one open proposal card at a time
  const handleSelectSeller = async (sellerId) => {
    try {
      // Call the API to select the seller
      const result = await selectSeller(sellerId);
      alert(`Seller ${sellerId} selected successfully!`);
      // Open the proposal card for the selected seller
      setSelectedSeller((prevSeller) => (prevSeller === sellerId ? null : sellerId));
      setProposalPrice(''); // Reset proposal price when changing selection
    } catch (error) {
      alert('Failed to select seller. Please try again.');
    }
  };
// 
  // Handle proposal submission
  const handleProposalSubmit = async (sellerId) => {
    if (!proposalPrice || isNaN(proposalPrice)) {
      alert('Please enter a valid proposal price.');
      return;
    }
  
    try {
      const result = await sendFinalPrice(sellerId, parseFloat(proposalPrice));  // Make sure to parse as a number
      alert(`Proposal submitted successfully for Seller ${sellerId}: ${result.message || 'Success'}`);
      setSelectedSeller(null); // Close the proposal card after submission
    } catch (error) {
      alert('Failed to submit proposal. Please try again.');
    }
  };
  

  return (
    <div className="d-flex flex-column">
      {/* Top Bar */}
      <Topbar title="Query" userName="Neha Sharma" showSearchBar={false} />
      <div className="container mt-4">
        <div className="row">
          {/* Product Details */}
          <div className="col-md-5">
            <h4>Products</h4>
            <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
              <div className="row">
                <div className="col-3">
                  <img
                    src={productImage}
                    alt="Product"
                    style={{ width: '82px', height: '80px', borderRadius: '8px', objectFit: 'cover' }}
                  />
                </div>
                <div className="col-9 ps-3">
                  <p style={{ margin: '0', fontSize: '18px' }}>Sonamasuri Rice</p>
                  <p className="product-description">Variety: Polished</p>
                  <p className="product-description">Quantity: 50kg</p>
                  <p className="product-description">Price Range: 500-800</p>
                  <p className="product-description">Certification: FSSAI</p>
                  <p className="product-description">Location: Palodara, Kanol, Vadodara, Gujarat 395009, India</p>
                </div>
              </div>
            </div>
          </div>

          {/* Seller Details */}
          <div className="col-md-7">
            <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
              <h4>Sellers</h4>
              {priceDetails.length > 0 ? (
                priceDetails.map((negotiation, index) => (
                  <div
                    key={index}
                    className={`card p-3 shadow-sm mb-4 seller-card position-relative ${selectedSeller === negotiation.sellerUId ? 'active' : ''}`}
                    style={{ borderRadius: '8px' }}
                  >
                    <div className="d-flex">
                      <div className="col-2">
                        <img
                          src={sellerImage}
                          alt="Seller"
                          className="seller-image"
                          style={{ width: '50px', height: '50px', borderRadius: '50%' }}
                        />
                      </div>
                      <div className="col-10" style={{ marginTop: '10px' }}>
                        <div className="d-flex justify-content-between">
                          <div>
                            <h6>{negotiation.sellerUId}</h6>
                            <p className="seller-address">{negotiation.sellerAddress || 'N/A'}</p>
                            <p className="seller-address">
                              Delivery Address: {negotiation.adminDeliveryAddress || 'N/A'}
                            </p>
                            <div className="alert">Final Price: {negotiation.adminFinalPrice || 'N/A'}</div>
                          </div>
                          <div className="text-end" style={{ marginTop: '10px' }}>
                            <p>Initial Price: {negotiation.adminInitialPrice}</p>
                            <p className="seller-info">Date: {negotiation.afpDate}</p>
                            <p className="seller-info">
                              Quantity: {negotiation.availableAmount}
                              {negotiation.unit}
                            </p>
                          </div>
                        </div>
                        <button
                        className={`btn btn-outline-primary btn-sm ${selectedSeller === negotiation.sellerUId ? 'btn-secondary' : ''}`}
                        onClick={() => handleSelectSeller(negotiation.sellerUId)}
                      >
                        {selectedSeller === negotiation.sellerUId ? 'Cancel' : 'Send Proposal'}
                      </button>
                      </div>
                    </div>

                    {/* Button to Show Proposal Card */}
                    <div className="d-flex justify-content-between mt-3">
                      
                      <button
                        className="btn btn-success btn-sm"
                        onClick={() => alert(`Seller ${negotiation.sellerUId} approved!`)}
                      >
                        Approve
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => alert(`Proposal for Seller ${negotiation.sellerUId} sent again.`)}
                      >
                        Reject
                      </button>
                    </div>

                    {/* Proposal Card */}
                    {selectedSeller === negotiation.sellerUId && (
                      <div className="mt-3 p-3 border rounded shadow-sm">
                        <h6>Submit Proposal</h6>
                        <div className="d-flex align-items-center">
                        <input
  type="number"
  className="form-control me-2"
  placeholder="Proposal Final Price"
  value={proposalPrice}
  onChange={(e) => setProposalPrice(e.target.value)} // Directly bind the value
/>

                          <button
                            className="btn btn-dark"
                            onClick={() => handleProposalSubmit(negotiation.sellerUId)}
                          >
                            Submit
                          </button>
                        </div>
                      </div>
                    )}
                  </div>
                ))
              ) : (
                <p className="text-muted">No negotiations available for this query.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PriceDetails;
