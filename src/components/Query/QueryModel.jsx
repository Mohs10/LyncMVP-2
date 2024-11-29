import React, { useState } from "react";

const QueryModal = ({ handleCloseModal }) => {
  const [quantity, setQuantity] = useState("");
  const [address, setAddress] = useState("");
  const [deliveryTime, setDeliveryTime] = useState("");

  const handleSend = () => {
    console.log("Quantity:", quantity);
    console.log("Address:", address);
    console.log("Deliver Within:", deliveryTime);
    handleCloseModal(); // Close the modal after sending
  };

  return (
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
              onClick={handleCloseModal}
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div className="modal-body p-4">
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

          <div className="modal-footer justify-content-center">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={handleCloseModal}
            >
              Close
            </button>
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleSend}
            >
              Send
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default QueryModal;
