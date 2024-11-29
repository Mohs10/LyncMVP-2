import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const SellerNegotiation = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // Retrieve negotiation details passed via navigation
  const { priceDetails } = location.state || {};

  return (
    <div className="container mt-5">
      <h3 className="mb-4">Seller Negotiations</h3>
      <button className="btn btn-outline-dark mb-3" onClick={() => navigate(-1)}>
        Back
      </button>
      {priceDetails ? (
        priceDetails.length > 0 ? (
          priceDetails.map((negotiation, index) => (
            <div key={index} className="card p-3 shadow-sm mb-3">
              <h6>Seller ID: {negotiation.sellerUId}</h6>
              <p>Status: {negotiation.status}</p>
              <p>Admin Initial Price: ${negotiation.adminInitialPrice}</p>
              <p>Seller Negotiated Price: ${negotiation.sellerNegotiatePrice || 'N/A'}</p>
              <p>Admin Final Price: ${negotiation.adminFinalPrice}</p>
              <p>Date: {negotiation.afpDate}</p>
            </div>
          ))
        ) : (
          <p className="text-muted">No negotiations available for this query.</p>
        )
      ) : (
        <p className="text-muted">No negotiation details provided.</p>
      )}
    </div>
  );
};

export default SellerNegotiation;
