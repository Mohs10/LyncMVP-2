// import React, { useState, useEffect } from "react";
// import { useParams } from "react-router-dom";
// import sellerImage from "../../assets/Profile-pic.png";
// import productImage from "../../assets/Product.png";
// import Topbar from "../Dashboard/Topbar";
// import { sendFinalPrice, selectSeller } from "../../Services/PriceService";
// import InquiriesService from "../../Services/InquiriesService";
// import { getAllProducts} from "../../Services/ProductService"; // Import mapping APIs
// import { getCategories } from "../../Services/AddCategoryService";
// import { getForms,getVarieties } from "../../Services/VarietyFormService";
// const PriceDetails = () => {
//   const { queryId } = useParams(); // Get queryId from the route
//   const [productDetails, setProductDetails] = useState(null); // State for product details
//   const [priceDetails, setPriceDetails] = useState([]); // Seller negotiations
//   const [categories, setCategories] = useState([]); // Category mapping
//   const [forms, setForms] = useState([]); // Form mapping
//   const [varieties, setVarieties] = useState([]); // Variety mapping
//   const [selectedSeller, setSelectedSeller] = useState(null); // Selected seller state
//   const [proposalPrice, setProposalPrice] = useState(""); // Proposal price

//   // Fetch inquiry, product, and mapping details
//   useEffect(() => {
//     const fetchDetails = async () => {
//       try {
//         // Fetch inquiry details to get productId
//         const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//         setPriceDetails(inquiryDetails.sellerNegotiations || []);

//         // Fetch all products and find the matching product by productId
//         const allProducts = await getAllProducts();
//         const product = allProducts.find(
//           (p) => p.productId === inquiryDetails.productId
//         );

//         if (product) {
//           setProductDetails(product);
//         } else {
//           console.warn(`Product with ID ${inquiryDetails.productId} not found.`);
//         }

//         // Fetch mappings for category, form, and variety
//         const fetchedCategories = await getCategories();
//         const fetchedForms = await getForms();
//         const fetchedVarieties = await getVarieties();

//         setCategories(fetchedCategories);
//         setForms(fetchedForms);
//         setVarieties(fetchedVarieties);
//       } catch (error) {
//         console.error("Error fetching details:", error.message);
//         alert("Failed to fetch details. Please try again later.");
//       }
//     };

//     fetchDetails();
//   }, [queryId]);

//   // Helper functions to get names from IDs
//   const getCategoryName = (categoryId) => {
//     const category = categories.find((c) => c.id === categoryId);
//     return category ? category.categoryName:  "N/A";
//   };

//   const getFormName = (formId) => {
//     const form = forms.find((f) => f.id === formId);
//     return form ? form.formName : "N/A";
//   };

//   const getVarietyName = (varietyId) => {
//     const variety = varieties.find((v) => v.id === varietyId);
//     return variety ? variety.varietyName : "N/A";
//   };

//   // Handle seller selection
//   const handleSelectSeller = async (sellerId) => {
//     try {
//       await selectSeller(sellerId);
//       alert(`Seller ${sellerId} selected successfully!`);
//       setSelectedSeller((prev) => (prev === sellerId ? null : sellerId));
//       setProposalPrice(""); // Reset proposal price on selection change
//     } catch (error) {
//       console.error("Error selecting seller:", error.message);
//       alert("Failed to select seller. Please try again.");
//     }
//   };

//   // Handle proposal submission
//   const handleProposalSubmit = async (sellerId) => {
//     if (!proposalPrice || isNaN(proposalPrice)) {
//       alert("Please enter a valid proposal price.");
//       return;
//     }

//     try {
//       await sendFinalPrice(sellerId, parseFloat(proposalPrice));
//       alert(`Proposal submitted successfully for Seller ${sellerId}`);
//       setSelectedSeller(null); // Close the proposal card
//     } catch (error) {
//       console.error("Error submitting proposal:", error.message);
//       alert("Failed to submit proposal. Please try again.");
//     }
//   };

//   return (
//     <div className="d-flex flex-column">
//       {/* Top Bar */}
//       <Topbar title="Query" userName="Neha Sharma" showSearchBar={false} />
//       <div className="container mt-4">
//         <div className="row">
//           {/* Product Details */}
//           <div className="col-md-5">
//             <h4>Product Details</h4>
//             {productDetails ? (
//               <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: "8px" }}>
//                 <div className="row">
//                   <div className="col-3">
//                     <img
//                       src={productImage}
//                       alt="Product"
//                       style={{ width: "82px", height: "80px", borderRadius: "8px", objectFit: "cover" }}
//                     />
//                   </div>
//                   <div className="col-9 ps-3">
//                     <p style={{ margin: "0", fontSize: "18px" }}>{productDetails.productName}</p>
//                     <p className="product-description">
//                       Category: {getCategoryName(productDetails.categoryId)}
//                     </p>
//                     <p className="product-description">
//                       Variety: {getVarietyName(productDetails.varietyId)}
//                     </p>
//                     <p className="product-description">
//                       Form: {getFormName(productDetails.formId)}
//                     </p>
//                     <p className="product-description">
//                       Price Range: {productDetails.askMinPrice|| "N/A"}
//                     </p>
                    
//                     <p className="product-description">
//                       Location: {productDetails.deliveryAddress  || "N/A"}
//                     </p>
//                   </div>
//                 </div>
//               </div>
//             ) : (
//               <p className="text-muted">Loading product details...</p>
//             )}
//           </div>

//           {/* Seller Details */}
//           <div className="col-md-7">
//             <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: "8px" }}>
//               <h4>Sellers</h4>
//               {priceDetails.length > 0 ? (
//                 priceDetails.map((negotiation, index) => (
//                   <div
//                     key={index}
//                     className={`card p-3 shadow-sm mb-4 seller-card position-relative ${
//                       selectedSeller === negotiation.sellerUId ? "active" : ""
//                     }`}
//                     style={{ borderRadius: "8px" }}
//                   >
//                     <div className="d-flex">
//                       <div className="col-2">
//                         <img
//                           src={sellerImage}
//                           alt="Seller"
//                           className="seller-image"
//                           style={{ width: "50px", height: "50px", borderRadius: "50%" }}
//                         />
//                       </div>
//                       <div className="col-10" style={{ marginTop: "10px" }}>
//                         <div className="d-flex justify-content-between">
//                           <div>
//                           <h6>{negotiation.sellerUId}</h6>
//                             <p>{negotiation.sellerName || "Seller Name"}</p>
//                             <p>{negotiation.email || "Seller Email"}</p>
//                             <p>{negotiation.phoneNumber || "Seller phone no"}</p>
//                             <p className="seller-address">
//   {`${negotiation.adminCity || "N/A"}, ${negotiation.adminState || "N/A"}, ${negotiation.adminCountry || "N/A"} - ${negotiation.adminPinCode || "N/A"}`}
// </p>

//                             <div className="alert w-100">Final Price: {negotiation.adminFinalPrice || "N/A"}</div>
//                           </div>
//                           <div className="text-end" style={{ marginTop: "10px" }}>
//                             <p>Initial Price: {negotiation.adminInitialPrice}</p>
//                             <p className="seller-info">Quantity: {negotiation.availableAmount || 0}</p>
//                           </div>
//                         </div>
//                         <button
//                           className={`btn btn-outline-primary btn-sm ${
//                             selectedSeller === negotiation.sellerUId ? "btn-secondary" : ""
//                           }`}
//                           onClick={() => handleSelectSeller(negotiation.sellerUId)}
//                         >
//                           {selectedSeller === negotiation.sellerUId ? "Cancel" : "Send Proposal"}
//                         </button>
//                       </div>
//                     </div>

//                     {/* Proposal Card */}
//                     {selectedSeller === negotiation.sellerUId && (
//                       <div className="mt-3 p-3 border rounded shadow-sm">
//                         <h6>Submit Proposal</h6>
//                         <div className="d-flex align-items-center">
//                           <input
//                             type="number"
//                             className="form-control me-2"
//                             placeholder="Proposal Final Price"
//                             value={proposalPrice}
//                             onChange={(e) => setProposalPrice(e.target.value)}
//                           />
//                           <button
//                             className="btn btn-dark"
//                             onClick={() => handleProposalSubmit(negotiation.sellerUId)}
//                           >
//                             Submit
//                           </button>
//                         </div>
//                       </div>
//                     )}
//                   </div>
//                 ))
//               ) : (
//                 <p className="text-muted">No negotiations available for this query.</p>
//               )}
//             </div>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default PriceDetails;
import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import sellerImage from "../../assets/Profile-pic.png";
import productImage from "../../assets/Product.png";
import Topbar from "../Dashboard/Topbar";

import InquiriesService from "../../Services/InquiriesService";
import { getAllProducts } from "../../Services/ProductService"; // Import mapping APIs
import { getCategories } from "../../Services/AddCategoryService";
import { getForms, getVarieties } from "../../Services/VarietyFormService";
import Back from '../Dashboard/Back';
import { rejectSeller } from '../../Services/RejectSellerService';
import { approveSeller } from "../../Services/ApproveSellerService"
import { submitProposalPrice } from "../../Services/ProposalPriceService"; // Import the new service

const PriceDetails = () => {
  const { queryId } = useParams(); // Get queryId from the route
  const [productDetails, setProductDetails] = useState(null); // State for product details
  const [priceDetails, setPriceDetails] = useState([]); // Seller negotiations
  const [categories, setCategories] = useState([]); // Category mapping
  const [forms, setForms] = useState([]); // Form mapping
  const [varieties, setVarieties] = useState([]); // Variety mapping
  const [selectedSeller, setSelectedSeller] = useState(null); // Selected seller state
  const [proposalPrice, setProposalPrice] = useState(""); // Proposal price
  const [submittedPrices, setSubmittedPrices] = useState({}); // To store submitted prices for negotiations
  // Fetch inquiry, product, and mapping details
  useEffect(() => {
    const fetchDetails = async () => {
      try {
        // Fetch inquiry details to get productId
        const inquiryDetails = await InquiriesService.getInquiryById(queryId);
        setPriceDetails(inquiryDetails.sellerNegotiations || []);

        // Fetch all products and find the matching product by productId
        const allProducts = await getAllProducts();
        const product = allProducts.find(
          (p) => p.productId === inquiryDetails.productId
        );

        if (product) {
          setProductDetails(product);
        } else {
          console.warn(`Product with ID ${inquiryDetails.productId} not found.`);
        }

        // Fetch mappings for category, form, and variety
        const fetchedCategories = await getCategories();
        const fetchedForms = await getForms();
        const fetchedVarieties = await getVarieties();

        setCategories(fetchedCategories);
        setForms(fetchedForms);
        setVarieties(fetchedVarieties);
      } catch (error) {
        console.error("Error fetching details:", error.message);
        alert("Failed to fetch details. Please try again later.");
      }
    };

    fetchDetails();
  }, [queryId]);
  const handleRejectSeller = async (snId) => {
    try {
      const response = await rejectSeller(snId);
      alert(`Seller with SN ID ${snId} rejected successfully!`);
      console.log("API Response:", response);
    } catch (error) {
      console.error("Error rejecting seller:", error.message);
      alert(error.message || "Failed to reject seller. Please try again.");
    }
  };
  const handleApproveSeller = async (snId) => {
    try {
      const response = await approveSeller(snId);
      alert(`Seller with SN ID ${snId} approved successfully!`);
      console.log("API Response:", response);
    } catch (error) {
      console.error("Error approving seller:", error.message);
      alert(error.message || "Failed to approve seller. Please try again.");
    }
  }
  // Helper functions to get names from IDs
  const getCategoryName = (categoryId) => {
    const category = categories.find((c) => c.id === categoryId);
    return category ? category.categoryId : "N/A";
  };

  const getFormName = (formId) => {
    const form = forms.find((f) => f.id === formId);
    return form ? form.formName : "N/A";
  };

  const getVarietyName = (varietyId) => {
    const variety = varieties.find((v) => v.id === varietyId);
    return variety ? variety.varietyName : "N/A";
  };

  // Handle seller selection with checkbox
  const handleSelectSeller = (sellerId) => {
    setSelectedSeller((prev) => (prev === sellerId ? null : sellerId));
    setProposalPrice(""); // Reset proposal price on selection change
  };

  const handleProposalSubmit = async (snId) => {
    if (!proposalPrice || isNaN(proposalPrice) || Number(proposalPrice) <= 0) {
      alert("Please enter a valid proposal price.");
      return;
    }
  
    try {
      const response = await submitProposalPrice(snId, proposalPrice); // Call the service with snId and proposalPrice
      alert(`Successfully submitted final price: ${proposalPrice} for SN ID: ${snId}`);
      console.log("API Response:", response);
    } catch (error) {
      console.error("Error submitting proposal price:", error.message);
      alert(error.message || "Failed to submit the proposal price. Please try again.");
    }

  setSubmittedPrices((prev) => ({
    ...prev,
    [snId]: proposalPrice,
  }));
  setProposalPrice(''); // Clear input after submission
}
  
  

  return (
    <div className="d-flex flex-column">
      {/* Top Bar */}
      <Topbar title="Query" userName="Neha Sharma" showSearchBar={false} />
      <div className="container mt-4">
        <Back />
        <div className="row">
          {/* Product Details */}
          <div className="col-md-5">
            <h6>Product Details</h6>
            {productDetails ? (
              <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: "8px" }}>
                <div className="row">
                  <div className="col-3">
                    <img
                      src={productImage}
                      alt="Product"
                      style={{ width: "82px", height: "80px", borderRadius: "8px", objectFit: "cover" }}
                    />
                  </div>
                  <div className="col-9 ps-3">
                    <p style={{ margin: "0", fontSize: "18px" }}>{productDetails.productName}</p>
                    <p className="product-description">
                      Category: {getCategoryName(productDetails.categoryId)}
                    </p>
                    <p className="product-description">
                      Variety: {getVarietyName(productDetails.varietyId)}
                    </p>
                    <p className="product-description">
                      Form: {getFormName(productDetails.formId)}
                    </p>
                    <p className="product-description">
                      Price Range: {productDetails.priceRange || "N/A"}
                    </p>
                    <p className="product-description">
                      Certification: {productDetails.certification || "N/A"}
                    </p>
                    <p className="product-description">
                      Location: {productDetails.location || "N/A"}
                    </p>
                  </div>
                </div>
              </div>
            ) : (
              <p className="text-muted">Loading product details...</p>
            )}
          </div>

          {/* Seller Details */}
          <div className="col-md-7">
            <div className="card p-3 shadow-sm mb-4" style={{ borderRadius: "8px" }}>
              <h4>Sellers</h4>
              {priceDetails && priceDetails.length > 0 ? (
  priceDetails.map((negotiation, index) => (
    <div
      key={index}
      className={`card p-3 shadow-sm mb-4 seller-card position-relative`}
      style={{ borderRadius: "8px" }}
    >
      <div className="d-flex">
        <div className="col-2">
          <img
            src={sellerImage}
            alt="Seller"
            className="seller-image"
            style={{ width: "50px", height: "50px", borderRadius: "50%" }}
          />
        </div>
        <div className="col-10" style={{ marginTop: "10px" }}>
          <div className="d-flex justify-content-between">
            <div>
              <h6>{negotiation.sellerUId}</h6>
              <p>{negotiation.sellerName || "Seller Name"}</p>
              <p>{negotiation.email || "Seller Email"}</p>
              <p>{negotiation.phoneNumber || "Seller Phone No"}</p>
              <p className="seller-address">
                {`${negotiation.adminCity || "N/A"}, ${negotiation.adminState || "N/A"}, ${negotiation.adminCountry || "N/A"} - ${negotiation.adminPinCode || "N/A"}`}
              </p>
              <p>snId: {negotiation.snId || "N/A"}</p>
              <p>status: {negotiation.status || "N/A"}</p>

              <div className="alert w-100 ">
                Seller Negotiated Price: {negotiation.sellerNegotiatePrice || "N/A"}
              </div>
              {/* <div className="alert w-100 my-4">
                Admin Price: {negotiation.adminFinalPrice || "N/A"}
              </div> */}
              {submittedPrices[negotiation.snId] && (
                  <div className="mt-2">
                    <span className="badge bg-warning mb-4  w-100">
                      Price Submitted: {submittedPrices[negotiation.snId]}
                    </span>
                  </div>
                )}
              <div className="d-flex justify-content-between">
                <button
                  className="btn btn-success btn-sm"
                  onClick={() => handleApproveSeller(negotiation.snId)} // Pass snId here
                >
                  Approve
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleRejectSeller(negotiation.snId)} // Pass snId here
                >
                  Reject
                </button>
              </div>
            </div>
            <div className="form-check position-absolute" style={{ top: "10px", right: "10px" }}>
              <div className="form-check mt-2">
                <input
                  type="checkbox"
                  className="form-check-input"
                  id={`selectSeller${negotiation.snId}`}
                  checked={selectedSeller === negotiation.snId}
                  onChange={() => handleSelectSeller(negotiation.snId)} // Use snId here
                />
                <label
                  className="form-check-label ms-2"
                  htmlFor={`selectSeller${negotiation.snId}`}
                >
                  Select Seller
                </label>
              </div>
              <div className="text-end" style={{ marginTop: "10px" }}>
                <p>Initial Price: {negotiation.adminInitialPrice}</p>
                <p className="seller-info">Quantity: {negotiation.availableAmount || 0}</p>
                
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Proposal Card */}
      {selectedSeller === negotiation.snId && (
              <div className="mt-3 p-3 border rounded shadow-sm">
                <div className="d-flex align-items-center">
                  <input
                    type="number"
                    className="form-control me-2"
                    placeholder="Proposal Final Price"
                    value={proposalPrice}
                    onChange={(e) => setProposalPrice(e.target.value)}
                  />
                  <button
                    className="btn btn-dark"
                    onClick={() => handleProposalSubmit(negotiation.snId)}
                  >
                    Submit
                  </button>
                </div>
                {/* Display the badge with the submitted price */}
                
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

