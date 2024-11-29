

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import InquiriesService from "../../Services/InquiriesService";
import TimelineItem from "./TimelineItem";

const QueryTimeline = ({ queryId }) => {
  const [queryDetails, setQueryDetails] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchQueryDetails = async () => {
      try {
        const details = await InquiriesService.getInquiryById(queryId);
        setQueryDetails(details);
      } catch (error) {
        console.error("Error fetching query details:", error.message);
        alert("Failed to fetch query details.");
      }
    };

    fetchQueryDetails();
  }, [queryId]);

  const handleAction = (action) => {
    switch (action) {
      case "sendQuery":
        navigate(`/send-query/${queryId}`);
        break;
      case "viewPriceDetails":
        navigate(`/price-details/${queryId}`, {
          state: { queryId, priceDetails: queryDetails.sellerNegotiations },
        });
        break;
      case "adminQuote":
        navigate(`/adminQuoteToBuyer/${queryId}`);
        break;
        case "negotiateToBuyer":
          navigate("/price-negotiation", {
            state: { queryId, productId: queryDetails.productId },
          });
          
        break;
      default:
        console.error("Unknown action:", action);
    }
  };

  const steps = [
    { id: "sendQuery", label: "Send Query", completed: !!queryDetails?.sentDate },
    {
      id: "viewPriceDetails",
      label: "View Price Details by Seller",
      completed: queryDetails?.sellerNegotiations?.length > 0,
    },
    {
      id: "adminQuote",
      label: "Admin Quote to Buyer",
      completed: !!queryDetails?.adminInitialPrice,
    },
    {
      id: "negotiateToBuyer",
      label: "Admin Negotiate to Buyer",
      completed: !!queryDetails?.buyerNegotiatePrice,
    },
  ];

  return (
    <div className="timeline-container">
      <h5 className="fw-bold mb-4">Timeline</h5>
      {steps.map((step, index) => (
        <TimelineItem
          key={index}
          label={step.label}
          completed={step.completed}
          timestamp={
            step.completed ? queryDetails?.[`${step.id}Time`] : "Pending"
          }
          onAction={() => handleAction(step.id)}
          isDisabled={!step.completed && index !== 0 && !steps[index - 1].completed}
        />
      ))}
    </div>
  );
};

export default QueryTimeline;

