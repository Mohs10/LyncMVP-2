import React, { useState, useEffect } from "react";
import { addAddress, getAllAddresses } from "../../Services/AddAddressService";
import InquiriesService from "../../Services/InquiriesService";

const QueryModal = ({
  selectedSellers,
  selectedAddresses,
  instructions,
  handleCloseModal,
  handleAddressChange,
  handleAddNewAddress,
  setInstructions,
  setMessage,
  qid, // Pass the inquiry ID from the parent component
}) => {
  const [addresses, setAddresses] = useState([]);
  const [showAddAddressModal, setShowAddAddressModal] = useState(false);
  const [newAddress, setLocalNewAddress] = useState("");
  const [country, setCountry] = useState("");
  const [state, setState] = useState("");
  const [city, setCity] = useState("");
  const [pinCode, setPinCode] = useState("");
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [adminInitialPrice, setAdminInitialPrice] = useState("");
  const [avgLeadTime, setAvgLeadTime] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [payload, setPayload] = useState({});

  useEffect(() => {
    const fetchAddresses = async () => {
      try {
        const fetchedAddresses = await getAllAddresses();
        setAddresses(fetchedAddresses);
      } catch (error) {
        alert("Failed to fetch addresses.");
      }
    };

    fetchAddresses();
  }, []);

  // Handle inquiry fetching
  useEffect(() => {
    const fetchInquiryDetails = async () => {
      if (!queryId) return;
      try {
        const inquiryData = await InquiriesService.getInquiryById(queryId);
        console.log("Fetched Inquiry Details:", inquiryData);
        // You can use inquiryData here if needed (e.g., populate the form or validate)
      } catch (error) {
        console.error("Error fetching inquiry details:", error.message);
      }
    };

    fetchInquiryDetails();
  }, [qid]);

  const handleSendQuery = async () => {
    setIsSubmitting(true);
    setMessage("");
  
    const payload = {
      sellerUIds: selectedSellers.map((seller) => seller.id),
      adminInitialPrice: parseFloat(adminInitialPrice),
      avgLeadTime: parseFloat(avgLeadTime),
      adminAddressId: selectedAddress,
      description: instructions,
    };
  
    try {
      const responseData = await InquiriesService.sendInquiryToSeller(qid, payload); // Use the service method
      console.log("Inquiry sent successfully:", responseData);
      setMessage("Inquiry sent successfully!");
      resetFields();
    } catch (error) {
      console.error("Error sending inquiry:", error.message);
      setMessage(error.message || "An unexpected error occurred.");
    } finally {
      setIsSubmitting(false);
    }
  };
  

  const resetFields = () => {
    setSelectedAddress(null);
    setAdminInitialPrice("");
    setAvgLeadTime("");
    setInstructions("");
  };
  

  return (
    <div className="modal show" style={{ display: "block", position: "fixed", top: "0", left: "0", width: "100%", height: "100%", backgroundColor: "rgba(0, 0, 0, 0.5)" }} tabIndex="-1" role="dialog">
      <div className="modal-dialog" role="document">
        <div className="modal-content">
          <div className="modal-header justify-content-center">
            <h5 className="modal-title text-center">Select Address</h5>
            <button type="button" className="close" onClick={handleCloseModal}>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div className="modal-body card-container p-4">
            <div className="card p-3 shadow-sm mb-4">
              {addresses.length > 0 ? (
               addresses.map((address, index) => (
                <div key={address._id || index} 
                className="card p-3 shadow-sm mb-4 d-flex align-items-center" 
                style={{
                  cursor: "pointer",
                  display: "flex",
                  flexDirection: "row", // Ensure the radio button and text are in a row
                  alignItems: "center",
                }}
               >
              
                    <input
                      type="radio"
                      className="form-check-input me-3"
                      name="address"
                      value={address._id}
                      checked={selectedAddress === address._id} // Set checked if address is selected
                      onChange={() => handleAddressClick(address._id)} // Handle selection
                    />
                    <div>
                      <h5>{address.address}</h5>
                      <p>{address.city}, {address.state}, {address.country} - {address.pincode}</p>
                    </div>
                  </div>
                ))
              ) : (
                <p>No addresses available.</p>
              )}
            </div>

            <div className="text-center mt-3">
              <button className="add-address-button mt-2" onClick={() => setShowAddAddressModal(true)}>
                + Add a new address
              </button>
            </div>

            <textarea className="form-control mt-3" rows="3" placeholder="Any other instructions" value={instructions} onChange={(e) => setInstructions(e.target.value)} />
          </div>

          <div className="modal-footer justify-content-center">
    <button
      type="button"
      className="btn btn-dark"
      onClick={handleSendQuery} // Close the modal
    >
      Submit Query
    </button>
  </div>
        </div>
      </div>

      {/* Add Address Modal */}
      {showAddAddressModal && (
        <div className="modal show" style={{ display: "block", position: "fixed", top: "0", left: "0", width: "100%", height: "100%", backgroundColor: "rgba(0, 0, 0, 0.5)" }} tabIndex="-1" role="dialog">
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header justify-content-center">
                <h5 className="modal-title text-center">Add New Address</h5>
                <button type="button" className="close" onClick={() => setShowAddAddressModal(false)}>
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>

              <div className="modal-body">
                <input type="text" className="form-control mb-4" placeholder="Enter new address" value={newAddress} onChange={(e) => setLocalNewAddress(e.target.value)} />
                <div className="row">
                  <div className="col-md-6">
                    <input type="text" className="form-control mb-4" placeholder="Country" value={country} onChange={(e) => setCountry(e.target.value)} />
                  </div>
                  <div className="col-md-6">
                    <input type="text" className="form-control mb-4" placeholder="State" value={state} onChange={(e) => setState(e.target.value)} />
                  </div>
                </div>
                <div className="row">
                  <div className="col-md-6">
                    <input type="text" className="form-control mb-4" placeholder="City" value={city} onChange={(e) => setCity(e.target.value)} />
                  </div>
                  <div className="col-md-6">
                    <input type="text" className="form-control mb-4" placeholder="Pin Code" value={pinCode} onChange={(e) => setPinCode(e.target.value)} />
                  </div>
                </div>
              </div>

              <div className="modal-footer justify-content-center">
                <button type="button" className="btn btn-secondary" onClick={() => setShowAddAddressModal(false)}>
                  Cancel
                </button>
                <button type="button" className="btn btn-dark" onClick={handleAddAddress}>
                  Add Address
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default QueryModal;