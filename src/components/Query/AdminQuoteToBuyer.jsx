// import React, { useState, useEffect } from 'react';
// import { useParams, useNavigate } from 'react-router-dom';
// import AdminQuoteService from '../../Services/AdminQuoteService';  // Ensure this path is correct
// import InquiriesService from '../../Services/InquiriesService';
// import Topbar from '../Dashboard/Topbar';

// const AdminQuoteToBuyer = () => {
//   const { queryId } = useParams();
//   const navigate = useNavigate();
//   const [price, setPrice] = useState('');
//   const [comment, setComment] = useState('');
//   const [isSubmitting, setIsSubmitting] = useState(false);
//   const [message, setMessage] = useState('');
//   const [inquiryDetails, setInquiryDetails] = useState(null);

//   useEffect(() => {
//     const fetchInquiryDetails = async () => {
//       try {
//         const response = await InquiriesService.getInquiryById(queryId);
//         setInquiryDetails(response);
//       } catch (error) {
//         console.error('Error fetching inquiry details:', error);
//         setMessage('Failed to load inquiry details.');
//       }
//     };
//     fetchInquiryDetails();
//   }, [queryId]);

//   const handleSubmitQuote = async () => {
//     if (!price || !comment) {
//       setMessage('Please fill in all fields.');
//       return;
//     }

//     setIsSubmitting(true);
//     setMessage('');  // Clear any previous message

//     try {
//       console.log("Sending quote with price:", price, "and comment:", comment);
//       // Use AdminQuoteService to send the quote
//       const response = await AdminQuoteService.sendQuoteToBuyer(queryId, price, comment);
//       // console.log("Response from API:", response);
//       setMessage('Quote sent successfully!');
//       setPrice('');  // Clear the form fields after submission
//       setComment('');
//     } catch (error) {
//       console.error('Error sending quote:', error);
//       setMessage('Failed to send the quote. Please try again.');
//     } finally {
//       setIsSubmitting(false);
//     }
//   };

//   if (!inquiryDetails) {
//     return (
//       <div className="container mt-4">
//         <h3 className="text-danger text-center">Inquiry Details Not Available</h3>
//         <button className="btn btn-secondary mt-4" onClick={() => navigate(-1)}>
//           Go Back
//         </button>
//       </div>
//     );
//   }

//   return (
//     <div className="d-flex flex-column">
//       <Topbar title="Add Product" userName="Neha Sharma" showSearchBar={false} />
//       <div className="container mt-4">
//         <form className="container-fluid mt-4" onSubmit={(e) => e.preventDefault()}>
//           <div className="margin-bottom-custom">
//             <button className="btn btn-dark" onClick={() => navigate(-1)}>
//               Back
//             </button>
//           </div>

//           <div className="row mb-4">
//             {/* Inquiry and Product Details */}
//             <div className="col-md-6">
//               <div className="card p-4">
//                 <h4 className="text-center mb-4 heading-with-bar">Inquiry Details</h4>
//                 {[ 
//                   { label: "Inquiry ID", value: inquiryDetails.qid },
//                   { label: "Buyer", value: inquiryDetails.buyerUId },
//                   { label: "Raised On", value: `${inquiryDetails.raiseDate} at ${inquiryDetails.raiseTime}` },
//                   { label: "Order Status", value: inquiryDetails.orderStatus },
//                   { label: "Location", value: `${inquiryDetails.city}, ${inquiryDetails.state}, ${inquiryDetails.country} - ${inquiryDetails.pincode}` },
//                   { label: "Delivery Address", value: inquiryDetails.deliveryAddress },
//                   { label: "Delivery Date", value: inquiryDetails.specifyDeliveryDate },
//                 ].map((item, idx) => (
//                   <div className="form-group row mb-3" key={idx}>
//                     <label className="col-4 col-form-label"><strong>{item.label}:</strong></label>
//                     <div className="col-8">
//                       <input type="text" className="form-control" value={item.value} readOnly />
//                     </div>
//                   </div>
//                 ))}
//               </div>
//             </div>
//             {/* Product Details */}
//             <div className="col-md-6">
//               <div className="card p-4">
//                 <h4 className="text-center mb-4 heading-with-bar">Product Details</h4>
//                 {[ 
//                   { label: "Product Name", value: inquiryDetails.productName },
//                   { label: "Variety", value: inquiryDetails.varietyName },
//                   { label: "Form", value: inquiryDetails.formName },
//                   { label: "Quantity", value: `${inquiryDetails.quantity} ${inquiryDetails.quantityUnit}` },
//                   { label: "Price Range", value: `${inquiryDetails.askMinPrice} - ${inquiryDetails.askMaxPrice} ${inquiryDetails.priceUnit}` },
//                   { label: "Price Terms", value: inquiryDetails.priceTerms },
//                   { label: "Packaging Material", value: inquiryDetails.packagingMaterial },
//                   { label: "Payment Terms", value: inquiryDetails.paymentTerms },
//                   { label: "Target Lead Time", value: inquiryDetails.targetLeadTime },
//                 ].map((item, idx) => (
//                   <div className="form-group row mb-3" key={idx}>
//                     <label className="col-4 col-form-label"><strong>{item.label}:</strong></label>
//                     <div className="col-8">
//                       <input type="text" className="form-control" value={item.value} readOnly />
//                     </div>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           </div>

//           {/* Quote Proposal Form */}
//           <div className="card p-4">
//             <h4 className="text-center heading-with-bar">Submit Proposal</h4>
//             <div className="mb-3">
//               <label htmlFor="price" className="form-label">Proposal Price</label>
//               <input
//                 type="number"
//                 className="form-control"
//                 id="price"
//                 placeholder="Enter the proposal price"
//                 value={price}
//                 onChange={(e) => setPrice(e.target.value)}
//               />
//             </div>

//             <div className="mb-3">
//               <label htmlFor="comment" className="form-label">Comment</label>
//               <textarea
//                 className="form-control"
//                 id="comment"
//                 placeholder="Enter a comment"
//                 value={comment}
//                 onChange={(e) => setComment(e.target.value)}
//               />
//             </div>

//             <button
//               className="btn btn-dark"
//               onClick={handleSubmitQuote}
//               disabled={isSubmitting}
//             >
//               {isSubmitting ? 'Submitting...' : 'Submit Quote'}
//             </button>
//             {message && <p className="mt-3">{message}</p>}
//           </div>
//         </form>
//       </div>
//     </div>
//   );
// };

// export default AdminQuoteToBuyer;
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AdminQuoteService from '../../Services/AdminQuoteService';
import InquiriesService from '../../Services/InquiriesService';
import Topbar from '../Dashboard/Topbar';
import defaultImage from '../../assets/Product2.png';
import Back from '../Dashboard/Back';

const AdminQuoteToBuyer = () => {
  const { queryId } = useParams();
  const navigate = useNavigate();
  const [price, setPrice] = useState('');
  const [comment, setComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');
  const [inquiryDetails, setInquiryDetails] = useState(null);
  const [sellerNegotiatePrice, setSellerNegotiatePrice] = useState(null);
  const sellerNegotiatedPriceField = {
    label: 'Seller Negotiated Price',
    value: sellerNegotiatePrice ? `${sellerNegotiatePrice} ${inquiryDetails.priceUnit}` : 'N/A',
  };
  

  // useEffect(() => {
  //   const fetchInquiryDetails = async () => {
  //     try {
  //       const response = await InquiriesService.getInquiryById(queryId);
  //       setInquiryDetails(response);
  //     } catch (error) {
  //       console.error('Error fetching inquiry details:', error);
  //       setMessage('Failed to load inquiry details.');
  //     }
  //   };
  //   fetchInquiryDetails();
  // }, [queryId]);
  useEffect(() => {
    const fetchInquiryDetails = async () => {
      try {
        const response = await InquiriesService.getInquiryById(queryId);
        setInquiryDetails(response);

        // Extract sellerNegotiatePrice
        if (response.sellerNegotiations?.length > 0) {
          setSellerNegotiatePrice(response.sellerNegotiations[0].sellerNegotiatePrice);
        }
      } catch (error) {
        console.error('Error fetching inquiry details:', error);
        setMessage('Failed to load inquiry details.');
      }
    };
    fetchInquiryDetails();
  }, [queryId]);

  const handleSubmitQuote = async () => {
    if (!price || !comment) {
      setMessage('Please fill in all fields.');
      return;
    }

    setIsSubmitting(true);
    setMessage('');

    try {
      const response = await AdminQuoteService.sendQuoteToBuyer(queryId, price, comment);
      setMessage('Quote sent successfully!');
      setPrice('');
      setComment('');
    } catch (error) {
      console.error('Error sending quote:', error);
      setMessage('Failed to send the quote. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!inquiryDetails) {
    return (
      <div className="container mt-4">
        <h3 className="text-danger text-center">Inquiry Details Not Available</h3>
        <button className="btn btn-secondary mt-4" onClick={() => navigate(-1)}>
          Go Back
        </button>
      </div>
    );
  }

  return (
    <div className="d-flex flex-column">
      <Topbar title="Quote Details" userName="Neha Sharma" showSearchBar={false} />
      <div className="container-fluid mt-4">
        <Back />
        <div className="row">
          {/* Left Column */}
          <div className="col-md-6">
            <div className="card p-4 mb-4">
              <div className="product-image-upload text-center mb-3">
                <img
                  src={defaultImage}
                  className="uploaded-image"
                  style={{ width: '100%', height: '300px', objectFit: 'cover' }}
                  alt="Product"
                />
              </div>
              <div className="card-body">
                {[
                  { label: 'Buyer ID', value: inquiryDetails.buyerUId },
                  { label: 'Query ID', value: inquiryDetails.qid },
                  { label: 'Quantity', value: `${inquiryDetails.quantity} ${inquiryDetails.quantityUnit}` },
                  { label: 'Price Range', value: `${inquiryDetails.askMinPrice} - ${inquiryDetails.askMaxPrice} ${inquiryDetails.priceUnit}` },
                  { label: 'Target Lead Time', value: inquiryDetails.targetLeadTime },
                  // { label: 'Seller Negotiated Price', value: sellerNegotiatePrice ? `${sellerNegotiatePrice} ${inquiryDetails.priceUnit}` : 'N/A' },
                ].map((item, idx) => (
                  <div className="form-group row mb-3" key={idx}>
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
          </div>

          {/* Right Column */}
          <div className="col-md-6">
            <div className="card p-4 mb-4">
            <div className="card-body">
  {[
    { label: 'Product Name', value: inquiryDetails.productName },
    { label: 'Variety', value: inquiryDetails.varietyName },
    { label: 'Form', value: inquiryDetails.formName },
    { label: 'Packaging Material', value: inquiryDetails.packagingMaterial },
    { label: 'Price Terms', value: inquiryDetails.priceTerms },
  ].map((item, idx) => (
    <div className="form-group row mb-3" key={idx}>
      <label className="col-12 col-form-label">{item.label}:</label>
      <div className="col-12">
        <input type="text" className="form-control" value={item.value} readOnly />
      </div>
    </div>
  ))}

  {/* Add Address Fields */}
  <div className="form-group row mb-3">
    <div className="col-md-6 mb-3">
      <label className="col-form-label">Country</label>
      <input
        type="text"
        className="form-control"
        value={inquiryDetails.country || ''}
        readOnly
      />
    </div>
    <div className="col-md-6 mb-3">
      <label className="col-form-label">State</label>
      <input
        type="text"
        className="form-control"
        value={inquiryDetails.state || ''}
        readOnly
      />
    </div>
  </div>
  <div className="form-group row mb-3">
    <div className="col-md-6 mb-3">
      <label className="col-form-label">City</label>
      <input
        type="text"
        className="form-control"
        value={inquiryDetails.city || ''}
        readOnly
      />
    </div>
    <div className="col-md-6 mb-3">
      <label className="col-form-label">Pin-code</label>
      <input
        type="text"
        className="form-control"
        value={inquiryDetails.pincode || ''}
        readOnly
      />
    </div>
  </div>

  {/* Delivery Date */}
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
  {/* <div className="alert w-100 my-4">
                Final Price: {inquiryDetails.sellerNegotiatePrice || "N/A"}
              </div> */}
              
             
           
        
<div
  className="bg-warning mb-2 w-100 text-center p-3 rounded"
>
  Update Quote and Send to Buyer
</div>
<div className="form-group row mb-3">
    <label className="col-12 col-form-label">{sellerNegotiatedPriceField.label}:</label>
    <div className="col-12">
      <input type="text" className="form-control" value={sellerNegotiatedPriceField.value} readOnly />
    </div>
  </div>
          <form onSubmit={(e) => e.preventDefault()}>
            <div className="mb-3">
              <label htmlFor="price" className="form-label">Price</label>
              <input
                type="number"
                className="form-control"
                id="price"
                placeholder="Enter the proposal price"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="comment" className="form-label">Comment</label>
              <textarea
                className="form-control"
                id="comment"
                placeholder="Enter a comment"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
              />
            </div>
            <button
              className="btn btn-dark"
              onClick={handleSubmitQuote}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Submitting...' : 'Submit Quote'}
            </button>
            {message && <p className="mt-3">{message}</p>}
          </form>
        </div>
      </div>
          </div>
        </div>
        </div>
        {/* Quote Proposal */}
        </div>
   
  );
};

export default AdminQuoteToBuyer;

