import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const PriceNegotiation = () => {
  const { state } = useLocation();
  const { queryId, productId } = state || {};
  const [negotiationAmount, setNegotiationAmount] = useState(0);
  const navigate = useNavigate();

  if (!queryId || !productId) {
    return <div>Error: Missing queryId or productId!</div>;
  }

  const handleNegotiationSubmit = () => {
    console.log('Submitting negotiation for queryId:', queryId, 'with amount:', negotiationAmount);
    // Call API for negotiation submission (you should implement this)
    navigate('/queries'); // Redirect after submission
  };

  return (
    <div className="container mt-5">
      <h3>Price Negotiation</h3>
      <form onSubmit={handleNegotiationSubmit}>
        <div className="mb-3">
          <label htmlFor="negotiationAmount" className="form-label">
            Enter your Negotiated Price
          </label>
          <input
            type="number"
            id="negotiationAmount"
            className="form-control"
            value={negotiationAmount}
            onChange={(e) => setNegotiationAmount(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary">Submit Negotiation</button>
      </form>
    </div>
  );
};

export default PriceNegotiation;
