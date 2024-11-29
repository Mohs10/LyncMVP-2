// import React from 'react';
// import Topbar from "../Dashboard/Topbar";

// import productImage from "../../assets/Product.png"; // Assuming there's a product image


// const NotifyBuyer = ({ product, sampleDetails, deliveryDetails }) => {
//   const handleNotifyBuyer = () => {
//     alert('Buyer has been notified!');
//   };

//   return (
//     <div className="d-flex flex-column">
//       {/* Top Bar */}
//       <Topbar title="Sample Request" userName="Neha Sharma" showSearchBar={false} />

//       {/* Main Content */}
//       <div className="container mt-4">
//         <div className="row">
//           {/* Left Panel - Product Information */}
//           <div className="col-md-6">
//             <div className="card p-3 shadow-sm mb-4">
//               <h5 className="fw-bold mb-3">Product Information</h5>
//               <div className="row">
//                 <div className="col-3">
//                   <img
//                     src={productImage}
//                     alt="Product"
//                     style={{
//                       width: "100px",
//                       height: "100px",
//                       borderRadius: "8px",
//                       objectFit: "cover",
//                     }}
//                   />
//                 </div>
//                 <div className="col-9">
//                   <p style={{ margin: "0", fontSize: "18px" }}>{product.name}</p>
//                   <p className="product-description">Variety: {product.variety}</p>
//                   <p className="product-description">Quantity: {product.quantity}</p>
//                   <p className="product-description">Price Range: {product.priceRange}</p>
//                   <p className="product-description">Certification: {product.certification}</p>
//                   <p className="product-description">Location: {product.location}</p>
//                 </div>
//               </div>
//             </div>
//           </div>

//           {/* Right Panel - Sample and Delivery Details */}
//           <div className="col-md-6">
//             {/* Sample Details Card */}
//             <div className="card p-3 shadow-sm mb-4">
//               <h5 className="fw-bold mb-3">Sample Details</h5>
//               <p>Item Total - {sampleDetails.itemTotal}</p>
//               <p>{sampleDetails.waiver ? "Sample Fee Waiver Enabled" : "Sample Fee Waiver Not Enabled"}</p>
//               <p>Delivery Charges: {sampleDetails.deliveryCharges}</p>
//               <h6>Total Payment: {sampleDetails.totalPayment}</h6>
//             </div>

//             {/* Delivery Details Card */}
//             <div className="card shadow-sm">
//               <h5 className="fw-bold mb-3">Delivery Details</h5>
//               <p>Address: {deliveryDetails.address}</p>
//             </div>
            
//           </div>
//         </div>

//         {/* Notify Buyer Button */}
//         <div className="mt-4">
//           <button className="btn btn-primary" onClick={handleNotifyBuyer}>
//             Notify Buyer
//           </button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default NotifyBuyer;
