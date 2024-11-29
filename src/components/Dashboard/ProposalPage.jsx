import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { sendFinalPrice, selectSeller } from "./services";
import axios from "axios";

const ProposalPage = () => {
  const { queryId } = useParams();
  const [sellers, setSellers] = useState([]);
  const [selectedSellerId, setSelectedSellerId] = useState("");
  const [proposalPrice, setProposalPrice] = useState("");

  useEffect(() => {
    // Fetch seller negotiations based on the query ID
    const fetchNegotiations = async () => {
      try {
        const response = await axios.get(
          `http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/getInquiryById/${queryId}`,
          {
            headers: {
              "Authorization": `Bearer ${localStorage.getItem("authToken")}`,
            },
          }
        );
        setSellers(response.data.sellerNegotiations || []);
      } catch (error) {
        console.error("Error fetching seller negotiations:", error);
      }
    };

    fetchNegotiations();
  }, [queryId]);

  const handleSendProposal = async () => {
    try {
      if (!selectedSellerId || !proposalPrice) {
        alert("Please select a seller and enter a proposal price.");
        return;
      }
      await sendFinalPrice(selectedSellerId, proposalPrice);
      alert("Proposal sent successfully.");
    } catch (error) {
      console.error("Error sending proposal:", error);
      alert("Failed to send proposal.");
    }
  };

  const handleSelectSeller = async (sellerId) => {
    try {
      await selectSeller(sellerId);
      setSelectedSellerId(sellerId);
      alert("Seller selected successfully.");
    } catch (error) {
      console.error("Error selecting seller:", error);
      alert("Failed to select seller.");
    }
  };

  return (
    <div>
      <h1>Proposal Page for Query ID: {queryId}</h1>
      <table border="1">
        <thead>
          <tr>
            <th>Seller Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Negotiated Price</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {sellers.map((seller) => (
            <tr key={seller.sellerUId}>
              <td>{seller.sellerName}</td>
              <td>{seller.email}</td>
              <td>{seller.phoneNumber}</td>
              <td>{seller.sellerNegotiatePrice}</td>
              <td>
                <button onClick={() => handleSelectSeller(seller.sellerUId)}>
                  Select Seller
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {selectedSellerId && (
        <div>
          <h2>Send Proposal</h2>
          <label>
            Proposal Price:
            <input
              type="number"
              value={proposalPrice}
              onChange={(e) => setProposalPrice(e.target.value)}
            />
          </label>
          <button onClick={handleSendProposal}>Send Proposal</button>
        </div>
      )}
    </div>
  );
};

export default ProposalPage;
