// import React, { useState, useEffect } from 'react';
// import { useParams } from 'react-router-dom';
// import Topbar from '../Dashboard/Topbar';
// import productImage from '../../assets/Product.png';
// import sellerImage from '../../assets/Profile-pic.png';
// import QueryModal from '../../components/Query/SendQueryPopup';
// import sellerInfoService from '../../Services/sellerInfoService';
// import sendInquiryToSeller from '../../Services/sendInquiryService';
// import SellerBuyerService from '../../Services/SellerBuyerService';
// import InquiriesService from '../../Services/InquiriesService'; // Import for fetching inquiry details
// import { getAllProducts } from '../../Services/ProductService'; // Import for fetching product details
// import Back from '../Dashboard/Back';
// const SendQuery = () => {
//   const { queryId } = useParams(); // Get the queryId from the URL
//   const [productDetails, setProductDetails] = useState(null); // Product details state
//   const [selectedSellers, setSelectedSellers] = useState([]);
//   const [price, setPrice] = useState('');
//   const [leadTime, setLeadTime] = useState('');
//   const [showModal, setShowModal] = useState(false);
//   const [selectedAddress, setSelectedAddress] = useState(null);
//   const [instructions, setInstructions] = useState('');
//   const [sellers, setSellers] = useState([]);
//   const [isSubmitting, setIsSubmitting] = useState(false);
//   const [message, setMessage] = useState('');

//   useEffect(() => {
//     const fetchDetails = async () => {
//       try {
//         // Fetch inquiry details by queryId
//         const inquiryDetails = await InquiriesService.getInquiryById(queryId);

//         // Get product ID, form ID, and variety ID from the inquiry
//         const { productId, productFormId, productVarietyId } = inquiryDetails;

//         // Fetch product details from the ProductService
//         const allProducts = await getAllProducts();
//         const product = allProducts.find((p) => p.productId === productId);

//         setProductDetails({
//           ...product,
//           ...inquiryDetails, // Combine product and inquiry-specific details
//         });

//         // Fetch seller data for the product
//         const sellerData = await sellerInfoService.getSellerInfo(productId, productFormId, productVarietyId);

//         // Fetch user details for sellers
//         const allUsers = await SellerBuyerService.fetchAllUsers();
//         const enrichedSellers = sellerData.map((seller) => {
//           const user = allUsers.find((user) => user.userId === seller.sellerId);
//           return {
//             ...seller,
//             sellerName: user?.fullName || 'Unknown',
//             email: user?.email || 'Unknown',
//             phoneNumber: user?.phoneNumber || 'Unknown',
//             country: user?.country || 'Unknown',
//             state: user?.state || 'Unknown',
//             city: user?.city || 'Unknown',
//             pinCode: user?.pinCode || 'Unknown',
//           };
//         });

//         setSellers(enrichedSellers); // Set sellers with enriched details
//       } catch (error) {
//         console.error('Error fetching data:', error.message);
//         alert('Failed to fetch product or seller details. Please try again.');
//       }
//     };

//     fetchDetails();
//   }, [queryId]);

//   const handleSelectSeller = (index) => {
//     setSelectedSellers((prev) =>
//       prev.includes(index) ? prev.filter((i) => i !== index) : [...prev, index]
//     );
//   };

//   const handleSendQuery = async () => {
//     if (!price || selectedSellers.length === 0) {
//       setMessage('Please complete all fields: price and seller selection.');
//       return;
//     }

//     setIsSubmitting(true);
//     setMessage('');

//     const sellerUIds = selectedSellers.map((index) => sellers[index]?.sellerId);
//     const payload = {
//       sellerUIds,
//       adminInitialPrice: parseFloat(price),
//       ...(selectedAddress && { adminAddressId: selectedAddress }),
//       description: instructions || 'Inquiry details from admin.',
//     };

//     try {
//       await sendInquiryToSeller(queryId, payload);
//       setMessage('Inquiry sent successfully!');
//       setSelectedSellers([]);
//       setPrice('');
//       setLeadTime('');
//       setInstructions('');
//       setSelectedAddress(null);
//     } catch (error) {
//       console.error('Error sending inquiry:', error.message);
//       setMessage('Failed to send the inquiry. Please try again.');
//     } finally {
//       setIsSubmitting(false);
//     }
//   };

//   return (
//     <div className="d-flex flex-column">
//       <Topbar title="Send Query" userName="Admin" showSearchBar={false} />
    
//       <div className="container mt-4">
//       <Back />
//         <div className="row">
//           <div className="col-md-5">
//             <h6>Product Information</h6>
//             {productDetails ? (
//               <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
//                 <div className="row">
//                   <div className="col-3">
//                     <img
//                       src={productImage}
//                       alt="Product"
//                       style={{ width: '82px', height: '80px', borderRadius: '8px', objectFit: 'cover' }}
//                     />
//                   </div>
//                   <div className="col-9 ps-3">
//                     <p style={{ margin: '0', fontSize: '18px' }}>{productDetails.productName}</p>
//                     <p className="product-description">Variety: {productDetails.varietyName || 'N/A'}</p>
//                     <p className="product-description">
//                       Quantity: {productDetails.quantity} {productDetails.unit}
//                     </p>
//                     <p className="product-description">
//                       Price Range: {productDetails.askMinPrice} - {productDetails.askMaxPrice} {productDetails.priceUnit}
//                     </p>
//                     <p className="product-description">Certification: {productDetails.certification || 'N/A'}</p>
//                     <p className="product-description">
//                       Location: {productDetails.city}, {productDetails.state}, {productDetails.country}
//                     </p>
//                   </div>
//                 </div>
//               </div>
//             ) : (
//               <p>Loading product details...</p>
//             )}
//           </div>

//           <div className="col-md-7">
//             <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
//               <h4>Sellers</h4>
//               {sellers.length > 0 ? (
//                 sellers.map((seller, index) => (
//                   <div key={index} className="card p-3 shadow-sm mb-3 seller-card position-relative">
//                     <div className="d-flex">
//                       <div className="col-2">
//                         <img src={sellerImage} alt="Seller" className="seller-image" />
//                       </div>
//                       <div className="col-10" style={{ marginTop: '10px' }}>
//                         <div className="d-flex justify-content-between">
//                           <div>
//                             <h5 className="seller-id">Seller ID: {seller.sellerId}</h5>
//                             <p className="seller-name">{seller.sellerName}</p>
//                             <p className="seller-email">{seller.email}</p>
//                             <p className="seller-phone-number">{seller.phoneNumber}</p>
//                             <p className="seller-location">
//                               {seller.city}, {seller.state}, {seller.country}, {seller.pinCode}
//                             </p>
//                           </div>
//                           <div className="text-end" style={{ marginTop: '10px' }}>
//                             <p className="seller-info">
//                               Available Quantity: {seller.availableAmount} {seller.unit}
//                             </p>
//                             <p className="seller-info">
//                               Price: {seller.minPrice} - {seller.maxPrice}
//                             </p>
//                           </div>
//                         </div>
//                       </div>
//                     </div>
//                     <div className="form-check position-absolute" style={{ top: '10px', right: '10px' }}>
//                       <input
//                         type="checkbox"
//                         className="form-check-input"
//                         id={`seller-${index}`}
//                         checked={selectedSellers.includes(index)}
//                         onChange={() => handleSelectSeller(index)}
//                       />
//                       <label className="form-check-label" htmlFor={`seller-${index}`}>
//                         Select
//                       </label>
//                     </div>
//                   </div>
//                 ))
//               ) : (
//                 <p>No sellers found for this product.</p>
//               )}
//               {selectedSellers.length > 0 && (
//                 <div className="custom-card mt-3">
//                   <div className="row align-items-center">
//                     <div className="col-6">
//                       <input
//                         type="text"
//                         className="form-control mb-3"
//                         placeholder="Add Price"
//                         value={price}
//                         onChange={(e) => setPrice(e.target.value)}
//                       />
//                       <input
//                         type="text"
//                         className="form-control"
//                         placeholder="Average Lead Time"
//                         value={leadTime}
//                         onChange={(e) => setLeadTime(e.target.value)}
//                       />
//                     </div>
//                     <div className="col-6 text-end">
//                       <button
//                         className="btn btn-dark w-75"
//                         onClick={handleSendQuery}
//                         disabled={isSubmitting}
//                       >
//                         {isSubmitting ? 'Sending...' : 'Select address'}
//                       </button>
//                     </div>
//                   </div>
                  
//                 </div>
//               )}
//             </div>
//             {message && <p className="text-center mt-2">{message}</p>}
//           </div>
         
//         </div>
//       </div>
//       {showModal && (
//         <QueryModal
//           isOpen={showModal}
//           onClose={() => setShowModal(false)}
//           onConfirm={handleSendQuery}
//         />
//       )}
//     </div>
//   );
// };

// export default SendQuery;
// import React, { useState, useEffect } from 'react';
// import { useParams } from 'react-router-dom';
// import Topbar from '../Dashboard/Topbar';
// import productImage from '../../assets/Product.png';
// import sellerImage from '../../assets/Profile-pic.png';
// import QueryModal from '../../components/Query/SendQueryPopup';
// import sellerInfoService from '../../Services/sellerInfoService';
// import { sendInquiryToSeller } from '../../Services/sendInquiryService';


// import SellerBuyerService from '../../Services/SellerBuyerService';
// import InquiriesService from '../../Services/InquiriesService';
// import { getAllProducts } from '../../Services/ProductService';
// import Back from '../Dashboard/Back';

// const SendQuery = () => {
//   const { queryId } = useParams();
//   const [productDetails, setProductDetails] = useState(null);
//   const [selectedSellers, setSelectedSellers] = useState([]);
//   const [price, setPrice] = useState('');
//   const [leadTime, setLeadTime] = useState('');
//   const [showModal, setShowModal] = useState(false);
//   const [selectedAddress, setSelectedAddress] = useState(null);
//   const [instructions, setInstructions] = useState('');
//   const [sellers, setSellers] = useState([]);
//   const [isSubmitting, setIsSubmitting] = useState(false);
//   const [message, setMessage] = useState('');
//   const [avgLeadTime, setAvgLeadTime] = useState("");
//   useEffect(() => {
//     const fetchDetails = async () => {
//       try {
//         const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//         const { productId, productFormId, productVarietyId } = inquiryDetails;

//         const allProducts = await getAllProducts();
//         const product = allProducts.find((p) => p.productId === productId);

//         setProductDetails({ ...product, ...inquiryDetails });

//         const sellerData = await sellerInfoService.getSellerInfo(productId, productFormId, productVarietyId);
//         const allUsers = await SellerBuyerService.fetchAllUsers();

//         const enrichedSellers = sellerData.map((seller) => {
//           const user = allUsers.find((user) => user.userId === seller.sellerId);
//           return {
//             ...seller,
//             sellerName: user?.fullName || 'Unknown',
//             email: user?.email || 'Unknown',
//             phoneNumber: user?.phoneNumber || 'Unknown',
//             country: user?.country || 'Unknown',
//             state: user?.state || 'Unknown',
//             city: user?.city || 'Unknown',
//             pinCode: user?.pinCode || 'Unknown',
//           };
//         });

//         setSellers(enrichedSellers);
//       } catch (error) {
//         console.error('Error fetching data:', error.message);
//         alert('Failed to fetch product or seller details. Please try again.');
//       }
//     };

//     fetchDetails();
//   }, [queryId]);

//   const handleSelectSeller = (index) => {
//     setSelectedSellers((prev) =>
//       prev.includes(index) ? prev.filter((i) => i !== index) : [...prev, index]
//     );
//   };

//   const handleOpenModal = () => {
//     if (!price || !leadTime) {
//       setMessage('Please fill in both the price and lead time fields.');
//       return;
//     }
//     setShowModal(true);
//   };

//   const handleCloseModal = () => {
//     setShowModal(false);
//   };

//   const handleSendQuery = async () => {
//     if (!selectedAddress) {
//       setMessage('Please select an address.');
//       return;
//     }
  
//     setIsSubmitting(true);
//     setMessage('');
  
//     const payload = {
//       sellerUIds: selectedSellers.map((seller) => seller.id), // Adjust this as per your seller format
//         adminInitialPrice: parseFloat(adminInitialPrice),
//         avgLeadTime: parseFloat(avgLeadTime),
//         adminAddressId: selectedAddress,
//         description: instructions,
//     };
  

//     try {
//       await sendInquiryToSeller(queryId, payload);
//       setMessage('Inquiry sent successfully!');
//       setSelectedSellers([]);
//       setPrice('');
//       setLeadTime('');
//       setInstructions('');
//       setSelectedAddress(null);
//     } catch (error) {
//       console.error('Error sending inquiry:', error.message);
//       setMessage(error.message);
//     } finally {
//       setIsSubmitting(false);
//     }
//   };
  
//   return (
//     <div className="d-flex flex-column">
//       <Topbar title="Send Query" userName="Admin" showSearchBar={false} />
//       <div className="container mt-4">
//         <Back />
//         <div className="row">
//           <div className="col-md-5">
//             <h6>Product Information</h6>
//             {productDetails ? (
//               <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
//                 <div className="row">
//                   <div className="col-3">
//                     <img
//                       src={productImage}
//                       alt="Product"
//                       style={{ width: '82px', height: '80px', borderRadius: '8px', objectFit: 'cover' }}
//                     />
//                   </div>
//                   <div className="col-9 ps-3">
//                     <p style={{ margin: '0', fontSize: '18px' }}>{productDetails.productName}</p>
//                     <p className="product-description">Variety: {productDetails.varietyName || 'N/A'}</p>
//                     <p className="product-description">
//                       Quantity: {productDetails.quantity} {productDetails.unit}
//                     </p>
//                     <p className="product-description">
//                       Price Range: {productDetails.askMinPrice} - {productDetails.askMaxPrice} {productDetails.priceUnit}
//                     </p>
//                     <p className="product-description">Certification: {productDetails.certification || 'N/A'}</p>
//                     <p className="product-description">
//                       Location: {productDetails.city}, {productDetails.state}, {productDetails.country}
//                     </p>
//                   </div>
//                 </div>
//               </div>
//             ) : (
//               <p>Loading product details...</p>
//             )}
//           </div>

//           <div className="col-md-7">
//             <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
//               <h4>Sellers</h4>
//               {sellers.length > 0 ? (
//                 sellers.map((seller, index) => (
//                   <div key={index} className="card p-3 shadow-sm mb-3 seller-card position-relative">
//                     <div className="d-flex">
//                       <div className="col-2">
//                         <img src={sellerImage} alt="Seller" className="seller-image" />
//                       </div>
//                       <div className="col-10" style={{ marginTop: '10px' }}>
//                         <div className="d-flex justify-content-between">
//                           <div>
//                             <h5 className="seller-id">Seller ID: {seller.sellerId}</h5>
//                             <p className="seller-name">{seller.sellerName}</p>
//                             <p className="seller-email">{seller.email}</p>
//                             <p className="seller-phone-number">{seller.phoneNumber}</p>
//                             <p className="seller-location">
//                               {seller.city}, {seller.state}, {seller.country}, {seller.pinCode}
//                             </p>
//                           </div>
//                           <div className="text-end" style={{ marginTop: '10px' }}>
//                             <p className="seller-info">
//                               Available Quantity: {seller.availableAmount} {seller.unit}
//                             </p>
//                             <p className="seller-info">
//                               Price: {seller.minPrice} - {seller.maxPrice}
//                             </p>
//                           </div>
//                         </div>
//                       </div>
//                     </div>
//                     <div className="form-check position-absolute" style={{ top: '10px', right: '10px' }}>
//                       <input
//                         type="checkbox"
//                         className="form-check-input"
//                         id={`seller-${index}`}
//                         checked={selectedSellers.includes(index)}
//                         onChange={() => handleSelectSeller(index)}
//                       />
//                       <label className="form-check-label" htmlFor={`seller-${index}`}>
//                         Select
//                       </label>
//                     </div>
//                   </div>
//                 ))
//               ) : (
//                 <p>No sellers found for this product.</p>
//               )}
//               {selectedSellers.length > 0 && (
//                 <div className="custom-card mt-3">
//                   <div className="row align-items-center">
//                     <div className="col-6">
//                       <input
//                         type="text"
//                         className="form-control mb-3"
//                         placeholder="Add Price"
//                         value={price}
//                         onChange={(e) => setPrice(e.target.value)}
//                       />
//                       <input
//                         type="text"
//                         className="form-control"
//                         placeholder="Average Lead Time"
//                         value={leadTime}
//                         onChange={(e) => setLeadTime(e.target.value)}
//                       />
//                     </div>
//                     <div className="col-6 text-end">
//                       <button
//                         className="btn btn-dark w-75"
//                         onClick={!selectedAddress ? handleOpenModal : handleSendQuery}
//                         disabled={isSubmitting}
//                       >
//                         {selectedAddress ? (isSubmitting ? 'Sending...' : 'Send') : 'Select Address'}
//                       </button>
//                     </div>
//                   </div>
//                 </div>
//               )}
//             </div>
//             {message && <p className="text-center mt-2">{message}</p>}
//           </div>
//         </div>
//       </div>
//       {queryId && (
//   <QueryModal
//     selectedSellers={selectedSellers}
//     selectedAddresses={selectedAddresses}
//     instructions={instructions}
//     handleCloseModal={handleCloseModal}
//     handleAddressChange={handleAddressChange}
//     handleAddNewAddress={handleAddNewAddress}
//     setInstructions={setInstructions}
//     setMessage={setMessage}
//     queryId={queryId} // Ensure qid is available
//   />
// )}

//     </div>
//   );
// };

// export default SendQuery;

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Topbar from '../Dashboard/Topbar';
import productImage from '../../assets/Product.png';
import sellerImage from '../../assets/Profile-pic.png';
import QueryModal from '../../components/Query/SendQueryPopup';
import sellerInfoService from '../../Services/sellerInfoService';
import { sendInquiryToSeller } from '../../Services/sendInquiryService';

import SellerBuyerService from '../../Services/SellerBuyerService';
import InquiriesService from '../../Services/InquiriesService';
import { getAllProducts } from '../../Services/ProductService';
import Back from '../Dashboard/Back';

const SendQuery = () => {
  const { queryId } = useParams();
  const [productDetails, setProductDetails] = useState(null);
  const [selectedSellers, setSelectedSellers] = useState([]);
  const [price, setPrice] = useState('');
  const [leadTime, setLeadTime] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [instructions, setInstructions] = useState('');
  const [sellers, setSellers] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');


  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const inquiryDetails = await InquiriesService.getInquiryById(queryId);
        const { productId, productFormId, productVarietyId } = inquiryDetails;

        const allProducts = await getAllProducts();
        const product = allProducts.find((p) => p.productId === productId);

        setProductDetails({ ...product, ...inquiryDetails });

        const sellerData = await sellerInfoService.getSellerInfo(productId, productFormId, productVarietyId);
        const allUsers = await SellerBuyerService.fetchAllUsers();

        const enrichedSellers = sellerData.map((seller) => {
          const user = allUsers.find((user) => user.userId === seller.sellerId);
          return {
            ...seller,
            sellerName: user?.fullName || 'Unknown',
            email: user?.email || 'Unknown',
            phoneNumber: user?.phoneNumber || 'Unknown',
            country: user?.country || 'Unknown',
            state: user?.state || 'Unknown',
            city: user?.city || 'Unknown',
            pinCode: user?.pinCode || 'Unknown',
          };
        });

        setSellers(enrichedSellers);
      } catch (error) {
        console.error('Error fetching data:', error.message);
        alert('Failed to fetch product or seller details. Please try again.');
      }
    };

    fetchDetails();
  }, [queryId]);

  const handleSelectSeller = (index) => {
    setSelectedSellers((prev) =>
      prev.includes(index) ? prev.filter((i) => i !== index) : [...prev, index]
    );
  };

  const handleOpenModal = () => {
    if (!price || !leadTime) {
      setMessage('Please fill in both the price and lead time fields.');
      return;
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleSendQuery = async () => {
    setIsSubmitting(true);
    setMessage('');
  
    const sellerUIds = selectedSellers.map((index) => sellers[index]?.sellerId);
    console.log('Seller IDs being sent:', sellerUIds); // Debugging seller IDs
  
    const payload = {
      sellerUIds,
      adminInitialPrice: parseFloat(price),
      avgLeadTime: parseFloat(leadTime),
      description: instructions,
      adminAddressId: "1",
    };
  
    try {
      await sendInquiryToSeller(queryId, payload);
      setMessage('Inquiry sent successfully!');
      setSelectedSellers([]);
      setPrice('');
      setLeadTime('');
      setInstructions('');
      setSuccessMessage('Query sent successfully!');
    } catch (error) {
      console.error('Error sending query:', error);
      // Handle error (optional: set an error message state)
    } finally {
      setIsSubmitting(false); // Re-enable button
    }
  };
    
  return (
    <div className="d-flex flex-column">
      <Topbar title="Send Query" userName="Admin" showSearchBar={false} />
      <div className="container mt-4">
        <Back />
        <div className="row">
          <div className="col-md-5">
            <h6>Product Information</h6>
            {productDetails ? (
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
                    <p style={{ margin: '0', fontSize: '18px' }}>{productDetails.productName}</p>
                    <p className="product-description">Variety: {productDetails.varietyName || 'N/A'}</p>
                    <p className="product-description">
                      Quantity: {productDetails.quantity} {productDetails.unit}
                    </p>
                    <p className="product-description">
                      Price Range: {productDetails.askMinPrice} - {productDetails.askMaxPrice} {productDetails.priceUnit}
                    </p>
                    {/* <p className="product-description">Certification: {productDetails.certification || 'N/A'}</p> */}
                    <p className="product-description">
                      Location: {productDetails.city}, {productDetails.state}, {productDetails.country}
                    </p>
                  </div>
                </div>
              </div>
            ) : (
              <p>Loading product details...</p>
            )}
          </div>

          <div className="col-md-7">
            <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: '8px' }}>
              <h4>Sellers</h4>
              {sellers.length > 0 ? (
                sellers.map((seller, index) => (
                  <div key={index} className="card p-3 shadow-sm mb-3 seller-card position-relative">
                    <div className="d-flex">
                      <div className="col-2">
                        <img src={sellerImage} alt="Seller" className="seller-image" />
                      </div>
                      <div className="col-10" style={{ marginTop: '10px' }}>
                        <div className="d-flex justify-content-between">
                          <div>
                            <h5 className="seller-id">Seller ID: {seller.sellerId}</h5>
                            <p className="seller-name">{seller.sellerName}</p>
                            <p className="seller-email">{seller.email}</p>
                            <p className="seller-phone-number">{seller.phoneNumber}</p>
                            <p className="seller-location">
                              {seller.city}, {seller.state}, {seller.country}, {seller.pinCode}
                            </p>
                          </div>
                          <div className="text-end" style={{ marginTop: '10px' }}>
                            <p className="seller-info">
                              Available Quantity: {seller.availableAmount} {seller.unit}
                            </p>
                            <p className="seller-info">
                              Price: {seller.minPrice} - {seller.maxPrice}
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="form-check position-absolute" style={{ top: '10px', right: '10px' }}>
                      <input
                        type="checkbox"
                        className="form-check-input"
                        id={`seller-${index}`}
                        checked={selectedSellers.includes(index)}
                        onChange={() => handleSelectSeller(index)}
                      />
                      <label className="form-check-label" htmlFor={`seller-${index}`}>
                        Select
                      </label>
                    </div>
                  </div>
                ))
              ) : (
                <p>No sellers found for this product.</p>
              )}
              {selectedSellers.length > 0 && (
                <div className="custom-card mt-3">
                  <div className="row align-items-center">
                    <div className="col-6">
                      <input
                        type="text"
                        className="form-control mb-3"
                        placeholder="Add Price"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                      />
                      <input
                        type="text"
                        className="form-control"
                        placeholder="Average Lead Time"
                        value={leadTime}
                        onChange={(e) => setLeadTime(e.target.value)}
                      />
                    </div>
                    <div className="col-6 text-end">
                      <button
                        className="btn btn-dark w-75"
                        onClick={handleSendQuery}
                        disabled={isSubmitting}
                      >
                        {isSubmitting ? 'Sending...' : 'Send'}
                      </button>
                    </div>
                  </div>
                 
                </div>
              )}
               {successMessage && (
      <div className="alert alert-success mt-3" role="alert">
        {successMessage}
      </div>
    )}
            </div>
            
          </div>
        </div>
      </div>
     
    </div>
  );
};

export default SendQuery;
