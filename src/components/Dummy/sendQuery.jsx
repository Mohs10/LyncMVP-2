import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Topbar from '../Dashboard/Topbar';
import productImage from '../../assets/Product.png';
import sellerImage from '../../assets/Profile-pic.png';
import QueryModal from '../../components/Query/SendQueryPopup';
import inquiriesService from '../../Services/InquiriesService'; // Fetch product details
import sellerInfoService from '../../Services/sellerInfoService'; // Fetch seller info
import { sendInquiryToSeller } from '../../Services/sendInquiryService';

const SendQuery = () => {
  const { queryId } = useParams();
  const [product, setProduct] = useState(null); // Holds product details
  const [sellers, setSellers] = useState([]);
  const [selectedSellers, setSelectedSellers] = useState([]);
  const [price, setPrice] = useState('');
  const [leadTime, setLeadTime] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [selectedAddresses, setSelectedAddresses] = useState([]);
  const [newAddress, setNewAddress] = useState('');
  const [instructions, setInstructions] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchProductAndSellers = async () => {
      try {
        if (!queryId) throw new Error('Query ID is required.');

        // Fetch product details using the queryId
        const productData = await inquiriesService.getProductDetails(queryId);
        setProduct(productData);

        // Fetch sellers based on the product details
        const sellerData = await sellerInfoService.getSellerInfo(
          productData.productId,
          productData.productFormId,
          productData.productVarietyId
        );
        setSellers(sellerData);
      } catch (error) {
        console.error('Error:', error.message);
        setMessage(error.message);
      }
    };

    fetchProductAndSellers();
  }, [queryId]);

  const handleSelectSeller = (index) => {
    setSelectedSellers((prev) =>
      prev.includes(index) ? prev.filter((i) => i !== index) : [...prev, index]
    );
  };

  const handleSendQuery = async () => {
    if (!price || selectedSellers.length === 0) {
      setMessage('Please add an initial price and select at least one seller.');
      return;
    }

    
    setIsSubmitting(true);
    setMessage('');

    const sellerUIds = selectedSellers.map((index) => sellers[index]?.sellerId);
    const payload = {
      sellerUIds,
      adminInitialPrice: parseFloat(price),
      adminAddressId: selectedAddresses.length > 0 ? selectedAddresses[0] : '',
      description: instructions || 'Inquiry details from admin.',
    };

    try {
      const response = await sendInquiryToSeller(queryId, payload);
      setMessage('Inquiry sent successfully!');
      console.log('API Response:', response);

      setSelectedSellers([]);
      setPrice('');
      setLeadTime('');
      setInstructions('');
    } catch (error) {
      console.error('Error sending inquiry:', error);
      setMessage('Failed to send the inquiry. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCloseModal = () => setShowModal(false);

  const handleAddNewAddress = () => {
    if (newAddress) {
      setSelectedAddresses((prev) => [...prev, newAddress]);
      setNewAddress('');
    }
  };

  return (
    <div className="d-flex flex-column">
      <Topbar title="Send Query" userName="Admin" showSearchBar={false} />

      <div className="container mt-4">
        <div className="row">
          <div className="col-md-5">
            <h4>Product Information</h4>
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
                  {product ? (
                    <>
                      <p style={{ margin: '0', fontSize: '18px' }}>{product.productName}</p>
                      <p className="product-description">Variety: {product.varietyName}</p>
                      <p className="product-description">Quantity: {product.quantity} {product.quantityUnit}</p>
                      <p className="product-description">Price Range: {product.askMinPrice} - {product.askMaxPrice} {product.priceUnit}</p>
                      <p className="product-description">Certification: {product.npop ? 'NPOP' : ''} {product.eu ? 'EU' : ''}</p>
                      <p className="product-description">Location: {product.city}, {product.state}, {product.country}</p>
                    </>
                  ) : (
                    <p>Loading product details...</p>
                  )}
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-7">
            <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
              <h4>Sellers</h4>
              {sellers.length > 0 ? (
                sellers.map((seller, index) => (
                  <div key={index} className="card p-3 shadow-sm mb-3">
                    <div className="d-flex">
                      <div className="col-2">
                        <img src={sellerImage} alt="Seller" className="seller-image" />
                      </div>
                      <div className="col-10">
                        <h5>{seller.sellerId}</h5>
                        <p>{seller.warehouseCountry}</p>
                        <p>Available: {seller.availableAmount} {seller.unit}</p>
                        <p>Price: {seller.minPrice} - {seller.maxPrice}</p>
                      </div>
                    </div>
                    <div>
                      <input
                        type="checkbox"
                        checked={selectedSellers.includes(index)}
                        onChange={() => handleSelectSeller(index)}
                      />
                    </div>
                  </div>
                ))
              ) : (
                <p>No sellers found for this product.</p>
              )}
            </div>
          </div>
        </div>
      </div>

      {showModal && (
        <QueryModal
          isVisible={showModal}
          onClose={handleCloseModal}
          selectedAddresses={selectedAddresses}
          newAddress={newAddress}
          setNewAddress={setNewAddress}
        />
      )}
    </div>
  );
};

export default SendQuery;
