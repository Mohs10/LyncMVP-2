import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import sellerImage from "../../assets/Profile-pic.png";
import productImage from "../../assets/Product.png";
import Topbar from "../Dashboard/Topbar";
import { sendFinalPrice } from "../../Services/PriceService";
import InquiriesService from "../../Services/InquiriesService";
import { getAllProducts } from "../../Services/ProductService"; // Import mapping APIs
import { getCategories } from "../../Services/AddCategoryService";
import { getForms, getVarieties } from "../../Services/VarietyFormService";
import Back from '../Dashboard/Back';
import { rejectSeller } from '../../Services/RejectSellerService';
import { approveSeller } from "../../Services/ApproveSellerService";
import { submitProposalPrice } from "../../Services/ProposalService"; // Import the new service

const PriceDetails = () => {
  const { queryId } = useParams(); // Get queryId from the route
  const [productDetails, setProductDetails] = useState(null); // State for product details
  const [priceDetails, setPriceDetails] = useState([]); // Seller negotiations
  const [categories, setCategories] = useState([]); // Category mapping
  const [forms, setForms] = useState([]); // Form mapping
  const [varieties, setVarieties] = useState([]); // Variety mapping
  const [selectedSeller, setSelectedSeller] = useState(null); // Selected seller state
  const [proposalPrice, setProposalPrice] = useState(""); // Proposal price

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
          console.warn(Product with ID ${inquiryDetails.productId} not found.);
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
      alert(Seller with SN ID ${snId} rejected successfully!);
      console.log("API Response:", response);
    } catch (error) {
      console.error("Error rejecting seller:", error.message);
      alert(error.message || "Failed to reject seller. Please try again.");
    }
  };

  const handleApproveSeller = async (snId) => {
    try {
      const response = await approveSeller(snId);
      alert(Seller with SN ID ${snId} approved successfully!);
      console.log("API Response:", response);
    } catch (error) {
      console.error("Error approving seller:", error.message);
      alert(error.message || "Failed to approve seller. Please try again.");
    }
  };

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

  // Handle proposal submission
  const handleProposalSubmit = async (sellerId) => {
    if (!proposalPrice || isNaN(proposalPrice)) {
      alert("Please enter a valid proposal price.");
      return;
    }

    try {
      await submitProposalPrice(queryId, parseFloat(proposalPrice)); // Submit the proposal price
      alert(Proposal submitted successfully for Seller ${sellerId});
      setSelectedSeller(null); // Close the proposal card
    } catch (error) {
      console.error("Error submitting proposal:", error.message);
      alert("Failed to submit proposal. Please try again.");
    }
  };

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
              {priceDetails.length > 0 ? (
                priceDetails.map((negotiation, index) => (
                  <div
                    key={index}
                    className={card p-3 shadow-sm mb-4 seller-card position-relative}
                    style={{ borderRadius: "8px" }}
                  >
                    <div className="d-flex">
                      <div className="col-2">
                        <img
                          src={sellerImage}
                          alt="Seller"
                          className="seller-image"
                          style={{ width: "50px", height