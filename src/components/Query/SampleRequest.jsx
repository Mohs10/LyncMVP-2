import React, { useState } from "react";
import profilePic from "../../assets/profile-pic.png"; // Assuming there's a profile picture for the seller
import productImage from "../../assets/Product.png"; // Assuming there's a product image
import Topbar from "../Dashboard/Topbar";

const SampleRequest = () => {
  const [showModal, setShowModal] = useState(false); // Modal visibility state
  const [quantity, setQuantity] = useState(""); // State for Quantity field
  const [address, setAddress] = useState(""); // State for Address field
  const [deliveryTime, setDeliveryTime] = useState(""); // State for Deliver Within field

  const handleSendQuery = () => {
    setShowModal(true); // Open modal
  };

  const closeModal = () => {
    setShowModal(false); // Close modal
  };

  const handleConfirm = () => {
    // Handle the logic for confirming the sample request
    console.log("Quantity:", quantity);
    console.log("Address:", address);
    console.log("Deliver Within:", deliveryTime);
    setShowModal(false); // Close the modal after confirming
  };

  return (
    <div className="d-flex flex-column">
      {/* Top Bar */}
      <Topbar title="Sample Request" userName="Neha Sharma" showSearchBar={false} />

      {/* Main Content */}
      <div className="container mt-4">
        <div className="row">
          {/* Left Panel - Product Information */}
          <div className="col-md-6">
            <div className="card p-3 shadow-sm mb-4">
              <h5 className="fw-bold mb-3">Product Information</h5>
              <div className="row">
                <div className="col-3">
                  <img
                    src={productImage}
                    alt="Product"
                    style={{
                      width: "100px",
                      height: "100px",
                      borderRadius: "8px",
                      objectFit: "cover",
                    }}
                  />
                </div>
                <div className="col-9">
                  <p style={{ margin: "0", fontSize: "18px" }}>Sonamasuri Rice</p>
                  <p className="product-description">Variety: Polished</p>
                  <p className="product-description">Quantity: 50kg</p>
                  <p className="product-description">Price Range: 500-800</p>
                  <p className="product-description">Certification: FSSAI</p>
                  <p className="product-description">
                    Location: Palodara, Kanol, Vadodara, Gujarat 395009, India
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Right Panel - Sample Request Details */}
          <div className="col-md-6">
            <div className="card p-3 shadow-sm">
              <h5 className="fw-bold mb-3">Sample Request Details</h5>

              {/* Sample Details */}
              <div className="card mb-3 p-3">
                <h6 className="fw-bold">Sample Details</h6>
                <p>Item Total: 500g Sonamasuri</p>
                <p>Sample Fee Waiver: Enabled</p>
                <p>Delivery Charges: ₹100</p>
                <hr />
                <p className="fw-bold">
                  Total Payment: <span className="float-end">₹0</span>
                </p>
              </div>

              {/* Delivery Details */}
              <div className="card mb-3 p-3">
                <h6 className="fw-bold">Delivery Details</h6>
                <p>
                  Plot No 906/22/C, Gandhinagar Sector 28, Gandhinagar-Gujarat -
                  382028, India.
                </p>
              </div>

              {/* Seller Details */}
              <div className="card p-3" style={{ backgroundColor: "#FFF7E6" }}>
                <h6 className="fw-bold">Seller Details</h6>
                <div className="d-flex align-items-center">
                  <img
                    src={profilePic}
                    alt="Seller"
                    className="rounded-circle"
                    style={{
                      width: "50px",
                      height: "50px",
                      marginRight: "15px",
                    }}
                  />
                  <div>
                    <h6 className="mb-0">Sidharth Mehta</h6>
                    <a
                      href="mailto:sidharth.mehta@gmail.com"
                      className="text-dark"
                    >
                      sidharth.mehta@gmail.com
                    </a>
                    <p className="mb-0">7387464746</p>
                    <p className="mb-0">
                      Plot No 906/22/C, Gandhinagar Sector 28, Gandhinagar-Gujarat
                      - 382028, India.
                    </p>
                  </div>
                </div>
              </div>

              {/* Send Button */}
              <div className="mt-3 d-flex justify-content-center">
                <button
                  className="btn btn-dark w-75"
                  onClick={handleSendQuery}
                >
                  Send
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div
          className="modal show"
          style={{
            display: "block",
            position: "fixed",
            top: "0",
            left: "0",
            width: "100%",
            height: "100%",
            backgroundColor: "rgba(0, 0, 0, 0.5)",
          }}
          tabIndex="-1"
          role="dialog"
        >
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header justify-content-center">
                <h5 className="modal-title text-center">Confirm Sample Request</h5>
                <button
                  type="button"
                  className="close"
                  onClick={closeModal}
                >
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>

              <div className="modal-body p-4 mb-4">
                {/* Quantity Field */}
                <div className="mb-3">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Quantity"
                    value={quantity}
                    onChange={(e) => setQuantity(e.target.value)}
                  />
                </div>

                {/* Address Field */}
                <div className="mb-3">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Address"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                </div>

                {/* Deliver Within Field */}
                <div className="mb-3">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Deliver Within"
                    value={deliveryTime}
                    onChange={(e) => setDeliveryTime(e.target.value)}
                  />
                </div>
              </div>

              <div className="modal-footer justify-content-center ">
                
                <button
                  type="button"
                  className="btn btn-dark w-25"
                  onClick={handleConfirm}
                >
                  Confirm
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SampleRequest;
