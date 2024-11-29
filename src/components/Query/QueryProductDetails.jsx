import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import InquiriesService from '../../Services/Inqury';

const QueryProductDetails = () => {
  const location = useLocation();
  const { inquiryDetails } = location.state;

  const [productData, setProductData] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!inquiryDetails) {
      setError('Inquiry details are missing');
      return;
    }

    const fetchDetails = async () => {
      try {
        // Fetch seller details
        const response = await InquiriesService.getSellerDetails(
          inquiryDetails.productId,
          inquiryDetails.productFormId,
          inquiryDetails.productVarietyId
        );

        setProductData(response);
      } catch (error) {
        setError(`Error fetching details: ${error.message}`);
        console.error('Error fetching details:', error);
      }
    };

    fetchDetails();
  }, [inquiryDetails]);

  if (error) {
    return <div className="container mt-4"><p className="text-danger">{error}</p></div>;
  }

  return (
    <div className="container mt-4">
      <h3>Product Details</h3>
      {productData.length > 0 ? (
        productData.map((product) => (
          <div key={product.spId} className="card p-3 shadow-sm mb-4">
            <h5>{product.productName} - {product.productVarietyName}</h5>
            <p>{product.description}</p>
            <img
              src={product.productImageUrl1}
              alt="Product"
              className="img-fluid mb-3"
            />

            <h6>Specifications:</h6>
            <ul>
              {product.specifications.map((spec) => (
                <li key={spec.specificationName}>
                  {spec.specificationName}: {spec.specificationValue} {spec.specificationValueUnits}
                </li>
              ))}
            </ul>

            <h6>Warehouse Details:</h6>
            <p>{product.warehouseCity}, {product.warehouseState}, {product.warehouseCountry}</p>
          </div>
        ))
      ) : (
        <p>Loading product details...</p>
      )}

      <h3>Available Sellers</h3>
      {productData.length > 0 ? (
        productData.map((seller) => (
          <div key={seller.sellerId} className="card p-3 shadow-sm mb-3">
            <h5>{seller.sellerId} - {seller.productFormName}</h5>
            <p>Price Range: {seller.minPrice} - {seller.maxPrice}</p>
            <p>Available Amount: {seller.availableAmount} {seller.unit}</p>
            <button className="btn btn-primary">
              Send Proposal
            </button>
          </div>
        ))
      ) : (
        <p>Loading seller details...</p>
      )}
    </div>
  );
};

export default QueryProductDetails;
