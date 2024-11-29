import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import InquiriesService from '../../Services/InquiriesService'; // Service to fetch inquiry details
import negotiationService from '../../Services/NegotiationService'; // Service to handle negotiations
import Topbar from '../Dashboard/Topbar'; // Assuming Topbar is a reusable component
import defaultImage from '../../assets/Product2.png';
import Back from '../Dashboard/Back';
const PriceNegotiation = () => {
  const { state } = useLocation();
  const { queryId, productId } = state || {};
  const [inquiryDetails, setInquiryDetails] = useState(null);
  const [negotiationAmount, setNegotiationAmount] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (queryId) {
      const fetchInquiryDetails = async () => {
        try {
          const response = await InquiriesService.getInquiryById(queryId);
          setInquiryDetails(response);
        } catch (error) {
          console.error('Error fetching inquiry details:', error);
          setErrorMessage('Failed to load inquiry details.');
        }
      };
      fetchInquiryDetails();
    }
  }, [queryId]);

  if (!queryId || !productId) {
    return <div>Error: Missing queryId or productId!</div>;
  }

  const handleNegotiationSubmit = async (e) => {
    e.preventDefault();
  
    if (negotiationAmount <= 0) {
      setErrorMessage('Negotiated price must be greater than zero');
      return;
    }
  
    const priceData = { amount: negotiationAmount }; // Corrected payload structure
  
    try {
      console.log('Submitting negotiation for queryId:', queryId);
      const response = await negotiationService.sendNegotiationToBuyer(queryId, priceData);
  
      if (response.status === 200) {
        setSuccessMessage('Negotiation submitted successfully!');
        setNegotiationAmount(0);
      } else {
        setErrorMessage('Failed to submit negotiation. Please try again.');
      }
    } catch (error) {
      console.error('Error during negotiation submission:', error.message);
      setErrorMessage('An error occurred during submission. Please try again.');
    }
  };
  
  if (!inquiryDetails) {
    return <div className="container mt-4">Loading inquiry details...</div>;
  }

  return (
    <div className="d-flex flex-column">
    <Topbar title="Query Details" userName="Neha Sharma" showSearchBar={false} />
    <div className="container mt-5">
    
<Back />
      {/* Success and Error Messages */}
    
      {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}

      {/* Inquiry Details */}
      <div className="row mb-4">
        <div className="col-md-6">
          <div className="card p-4">
          <div className="product-image-upload text-center mb-3">
                <img
                  src={defaultImage}
                  className="uploaded-image"
                  style={{ width: '100%', height: '300px', objectFit: 'cover' }}
                  alt="Product"
                />
              </div>
            {[ 
              { label: 'Buyer ID', value: inquiryDetails.buyerUId },
              { label: 'Query ID', value: inquiryDetails.qid },
              
              { label: 'Quantity', value: `${inquiryDetails.quantity} ${inquiryDetails.quantityUnit}` },
              { label: 'Price Range', value: `${inquiryDetails.askMinPrice} - ${inquiryDetails.askMaxPrice} ${inquiryDetails.priceUnit}` },
              
            ].map((item, idx) => (
              <div className="form-group row mb-2" key={idx}>
                <label className="col-12 col-form-label">{item.label}:</label>
                <div className="col-12">
                  <input type="text" className="form-control" value={item.value} readOnly />
                </div>
              </div>
            ))}
            <p className="my-2">Required Certifications:</p>
                {['npop', 'nop', 'eu', 'gsdc', 'ipm', 'other'].map((cert, idx) => (
                  <div className="form-check mb-3" key={idx}>
                    <input
                      className="form-check-input"
                      type="checkbox"
                      checked={inquiryDetails[cert] || false}
                      readOnly
                    />
                    <label className="form-check-label">{cert.toUpperCase()}</label>
                  </div>
                ))}
          </div>
        </div>

        <div className="col-md-6">
          <div className="card p-4">
            <h5 className="card-title">Product Details</h5>
            {[ 
              { label: 'Product Name', value: inquiryDetails.productName },
              { label: 'Variety', value: inquiryDetails.varietyName },
              { label: 'Packaging Material', value: inquiryDetails.packagingMaterial },
              { label: 'Price Terms', value: inquiryDetails.priceTerms },
              { label: 'Target Lead Time', value: inquiryDetails.targetLeadTime },
              { label: 'Delivery Address', value: inquiryDetails.deliveryAddress },
              
            ].map((item, idx) => (
              <div className="form-group row mb-2" key={idx}>
                <label className="col-12 col-form-label">{item.label}:</label>
                <div className="col-12">
                  <input type="text" className="form-control" value={item.value} readOnly />
                </div>
              </div>
            ))}
            <div className="form-group row mb-3">
  <div className="col-md-6">
    <label className="col-form-label"><strong>Country</strong></label>
    <input
      type="text"
      className="form-control"
      value={inquiryDetails.country || ''}
      readOnly
    />
  </div>
  <div className="col-md-6">
    <label className="col-form-label"><strong>State</strong></label>
    <input
      type="text"
      className="form-control"
      value={inquiryDetails.state || ''}
      readOnly
    />
  </div>
</div>

<div className="form-group row mb-3">
  <div className="col-md-6">
    <label className="col-form-label"><strong>City</strong></label>
    <input
      type="text"
      className="form-control"
      value={inquiryDetails.city || ''}
      readOnly
    />
  </div>
  <div className="col-md-6">
    <label className="col-form-label"><strong>Pin-code</strong></label>
    <input
      type="text"
      className="form-control"
      value={inquiryDetails.pincode || ''}
      readOnly
    />
  </div>
</div>
<div className="form-group row mb-3">
    <label className="col-12 col-form-label">Delivery Date</label>
    <div className="col-12">
      <input
        type="text"
        className="form-control"
        value={inquiryDetails.specifyDeliveryDate || ''}
        readOnly
      />
    </div>
  </div>
  <div
  className="bg-warning mb-2 w-100 text-center p-3 rounded"
>
Update Final Price and Send to Buyer
</div>
            
  <div className="form-group row mb-3">
    <label className="col-12 col-form-label">Final Price to Buyer</label>
    <div className="col-12">
      <input
        type="text"
        className="form-control"
        value={inquiryDetails.buyerNegotiatePrice || ''}
        readOnly
      />
    </div>
  </div>
             <form onSubmit={handleNegotiationSubmit}>
        <div className="mb-4">
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
        <button type="submit" className="btn btn-dark">Submit Negotiation</button>
      </form>
          </div>
          {successMessage && <div className="alert alert-success">{successMessage}</div>}
        </div>
        
      </div>

      {/* Negotiation Form */}
      </div>
    </div>
  );
};

export default PriceNegotiation;
