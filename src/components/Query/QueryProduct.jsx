// import React from "react";
// import { useLocation, useNavigate } from "react-router-dom";
// import Topbar from "../Dashboard/Topbar"; // Adjust the path if necessary

// const QueryProduct = () => {
//   const location = useLocation();
//   const navigate = useNavigate();
//   const inquiryDetails = location.state?.inquiryDetails;

//   if (!inquiryDetails) {
//     return (
//       <div className="container mt-4">
//         <h3 className="text-danger text-center">No Details Available</h3>
//         <button
//           className="btn btn-primary d-block mx-auto mt-4"
//           onClick={() => navigate(-1)}
//         >
//           Go Back
//         </button>
//       </div>
//     );
//   }

//   return (
//     <div className="d-flex flex-column">
//       {/* Top Bar */}
//       <Topbar
//         title="Product Inquiry Details"
//         userName="Neha Sharma"
//         showSearchBar={false}
//       />

//       <form className="container-fluid mt-4">
//         <div className="row">
//           {/* Left Section: Inquiry Summary */}
//           <div className="col-md-6">
//             <div className="card p-4 shadow-sm mt-3">
//               <h4 className="text-center mb-4 heading-with-bar">
//                 Inquiry Summary
//               </h4>
//               {[
//                 { label: "Inquiry ID", value: inquiryDetails.qid },
//                 { label: "Buyer", value: inquiryDetails.buyerUId },
//                 {
//                   label: "Raised On",
//                   value: `${inquiryDetails.raiseDate} at ${inquiryDetails.raiseTime}`,
//                 },
//                 { label: "Order Status", value: inquiryDetails.orderStatus },
//                 {
//                   label: "Location",
//                   value: `${inquiryDetails.city}, ${inquiryDetails.state}, ${inquiryDetails.country} - ${inquiryDetails.pincode}`,
//                 },
//                 {
//                   label: "Delivery Address",
//                   value: inquiryDetails.deliveryAddress,
//                 },
//                 {
//                   label: "Delivery Date",
//                   value: inquiryDetails.specifyDeliveryDate,
//                 },
//               ].map((item, idx) => (
//                 <div className="form-group row mb-3" key={idx}>
//                   <label className="col-4 col-form-label">
//                     <strong>{item.label}:</strong>
//                   </label>
//                   <div className="col-8">
//                     <input
//                       type="text"
//                       className="form-control"
//                       value={item.value}
//                       readOnly
//                     />
//                   </div>
//                 </div>
//               ))}
//             </div>
//           </div>

//           {/* Right Section: Product Details */}
//           <div className="col-md-6">
//             <div className="card shadow-sm">
//               <div className="card-body">
//                 <h5 className="text-center mb-4 heading-with-bar">
//                   Product Details
//                 </h5>
//                 {[
//                   { label: "Product Name", value: inquiryDetails.productName },
//                   { label: "Variety", value: inquiryDetails.varietyName },
//                   { label: "Form", value: inquiryDetails.formName },
//                   {
//                     label: "Quantity",
//                     value: `${inquiryDetails.quantity} ${inquiryDetails.quantityUnit}`,
//                   },
//                   {
//                     label: "Price Range",
//                     value: `${inquiryDetails.askMinPrice} - ${inquiryDetails.askMaxPrice} ${inquiryDetails.priceUnit}`,
//                   },
//                   { label: "Price Terms", value: inquiryDetails.priceTerms },
//                   {
//                     label: "Packaging Material",
//                     value: inquiryDetails.packagingMaterial,
//                   },
//                   { label: "Payment Terms", value: inquiryDetails.paymentTerms },
//                   { label: "Target Lead Time", value: inquiryDetails.targetLeadTime },
//                 ].map((item, idx) => (
//                   <div className="form-group row mb-3" key={idx}>
//                     <label className="col-4 col-form-label">
//                       <strong>{item.label}:</strong>
//                     </label>
//                     <div className="col-8">
//                       <input
//                         type="text"
//                         className="form-control"
//                         value={item.value}
//                         readOnly
//                       />
//                     </div>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           </div>
//         </div>

//         {/* Additional Information */}
//         <div className="row mt-4">
//         <div className="col-md-6">
//           <div className="card mt-4 shadow-sm">
//             <div className="card-body">
//               <h5 className="text-center mb-4 heading-with-bar">
//                 Additional Information
//               </h5>
//               <div className="form-group mb-3">
//                 <label>
//                   <strong>Certifications:</strong>
//                 </label>
//                 <div>
//                   <ul>
//                     {inquiryDetails.npop && <li>NPOP</li>}
//                     {inquiryDetails.nop && <li>NOP</li>}
//                     {inquiryDetails.eu && <li>EU</li>}
//                     {inquiryDetails.gsdc && <li>GSDC</li>}
//                     {inquiryDetails.ipm && <li>IPM</li>}
//                     {inquiryDetails.other && (
//                       <li>{`Other (${inquiryDetails.otherCertification})`}</li>
//                     )}
//                   </ul>
//                 </div>
//               </div>
//             </div>
//           </div>
//         </div>

//         {/* Specifications */}
      
//           <div className="col-md-6">
//             <div className="card shadow-sm">
//               <div className="card-body">
//                 <h5 className="text-center mb-4 heading-with-bar">
//                   Specifications
//                 </h5>
//                 {[
//                   {
//                     label: "Chalky Grains",
//                     value: inquiryDetails.chalkyGrains
//                       ? `${inquiryDetails.chalkyGrains}%`
//                       : "Not Available",
//                   },
//                   {
//                     label: "Grain Size",
//                     value: inquiryDetails.grainSize || "Not Available",
//                   },
//                   {
//                     label: "Kett Value",
//                     value: inquiryDetails.kettValue || "Not Available",
//                   },
//                   {
//                     label: "Moisture Content",
//                     value: inquiryDetails.moistureContent
//                       ? `${inquiryDetails.moistureContent}%`
//                       : "Not Available",
//                   },
//                   {
//                     label: "Broken Grain",
//                     value: inquiryDetails.brokenGrain || "Not Available",
//                   },
//                   {
//                     label: "Admixing",
//                     value: inquiryDetails.admixing || "Not Available",
//                   },
//                   {
//                     label: "Dehulling",
//                     value: inquiryDetails.dd || "Not Available",
//                   },
//                 ].map((item, idx) => (
//                   <div className="form-group row mb-3" key={idx}>
//                     <label className="col-4 col-form-label">
//                       <strong>{item.label}:</strong>
//                     </label>
//                     <div className="col-8">
//                       <input
//                         type="text"
//                         className="form-control"
//                         value={item.value}
//                         readOnly
//                       />
//                     </div>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           </div>
//         </div>

//         {/* Description and Comments */}
//         {/* <div className="form-group row mb-3">
//           <label className="col-4 col-form-label">
//             <strong>Description:</strong>
//           </label>
//           <div className="col-8">
//             <textarea
//               className="form-control"
//               rows="3"
//               value={inquiryDetails.description || "Not Provided"}
//               readOnly
//             />
//           </div>
//         </div>
//         <div className="form-group row mb-3">
//           <label className="col-4 col-form-label">
//             <strong>Admin Comments:</strong>
//           </label>
//           <div className="col-8">
//             <textarea
//               className="form-control"
//               rows="3"
//               value={inquiryDetails.comment || "No Comments Provided"}
//               readOnly
//             />
//           </div>
//         </div> */}
//       </form>
//     </div>
//   );
// };

// export default QueryProduct;
import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import ProductService from '../../Services/productt';
import InquiriesService from '../../Services/Inqury';

const QueryProductDetails = () => {
  const location = useLocation();
  const { inquiryDetails } = location.state;

  const [productDetails, setProductDetails] = useState(null);
  const [sellerDetails, setSellerDetails] = useState([]);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const productResponse = await ProductService.getProductDetails(
          inquiryDetails.productId,
          inquiryDetails.productFormId,
          inquiryDetails.productVarietyId
        );
        setProductDetails(productResponse);

        const sellerResponse = await InquiriesService.getSellerDetails(
          inquiryDetails.inquiryId
        );
        setSellerDetails(sellerResponse);
      } catch (error) {
        console.error('Error fetching details:', error.message);
      }
    };

    fetchDetails();
  }, [inquiryDetails]);

  return (
    <div className="container mt-4">
      <h3>Product Details</h3>
      {productDetails ? (
        <div className="card p-3 shadow-sm mb-4">
          <h5>{productDetails.productName} - {productDetails.productVarietyName}</h5>
          <p>{productDetails.description}</p>
          <img
            src={productDetails.productImageUrl1}
            alt="Product"
            className="img-fluid"
          />
        </div>
      ) : (
        <p>Loading product details...</p>
      )}

      <h3>Available Sellers</h3>
      {sellerDetails.length > 0 ? (
        sellerDetails.map((seller) => (
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

