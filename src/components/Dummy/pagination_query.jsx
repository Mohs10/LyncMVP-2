

// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';

// import InquiriesService from '../../Services/InquiriesService';
// import profilePic1 from '../../assets/Ellipse 3.png';
// import Topbar from '../Dashboard/Topbar';

// const Query = () => {
//   const [selectedQuery, setSelectedQuery] = useState(null);
//   const [queries, setQueries] = useState([]);

//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchInquiries = async () => {
//       try {
//         const data = await InquiriesService.fetchAllInquiries(); // Assuming this returns an array of queries
//         setQueries(data);
//       } catch (error) {
//         console.error('Error fetching inquiries:', error.message);
//         if (error.message === 'Token is not found or is expired.') {
//           navigate('/login');
//         }
//       }
//     };

//     fetchInquiries();
//   }, [navigate]);

//   const handleOpenProductDetails = async (queryId) => {
//     try {
//       const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//       navigate('/query-product', { state: { inquiryDetails } });
//     } catch (error) {
//       console.error('Error fetching inquiry details:', error.message);
//     }
//   };

//   const handleSendQuery = () => {
//     if (selectedQuery) {
//       navigate(`/send-query/${selectedQuery}`);
//     }
//   };

//   // const handleOpenPriceDetails = async (queryId) => {
//   //   try {
//   //     const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//   //     const priceDetails = inquiryDetails.sellerNegotiations || [];
//   //     navigate('/price-details', { state: { priceDetails } });
//   //   } catch (error) {
//   //     console.error('Error fetching inquiry details:', error.message);
//   //   }
//   // };
//   const handleOpenPriceDetails = async (queryId) => {
//     try {
//       const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//       const priceDetails = inquiryDetails.sellerNegotiations || [];
//       navigate('/price-details', { state: { priceDetails, queryId } });
//     } catch (error) {
//       console.error('Error fetching inquiry details:', error.message);
//     }
//   };

//   const handleCheckboxChange = (queryId) => {
//     setSelectedQuery((prevQueryId) => (prevQueryId === queryId ? null : queryId));
//   };

//   const handleSendNegotiationToBuyer = async (queryId) => {
//     try {
//       const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//       if (!inquiryDetails.productId) {
//         throw new Error('Product ID not found in inquiry details');
//       }
//       const productId = inquiryDetails.productId;
//       navigate('/price-negotiation', { state: { queryId, productId } });
//     } catch (error) {
//       console.error('Error sending negotiation to buyer:', error.message);
//     }
//   };

//   return (
//     <div className="d-flex flex-column">
//       <Topbar title="Queries" userName="Neha Sharma" showSearchBar={false} />

//       <div className="container mt-4">
//         <div className="row">
//           <div className="col-md-5">
//             <input
//               type="text"
//               className="form-control yellow-border"
//               placeholder="Search by Order ID"
//               onChange={(e) => console.log(e.target.value)} // Handle search functionality here
//             />
//             <h5 className="my-4">Today's Queries</h5>

//             {queries.map((query) => (
//               <div
//                 key={query.qid}
//                 className={`card p-3 shadow-sm mb-3 ${selectedQuery === query.qid ? 'selected-query' : ''}`}
//                 style={{ cursor: 'pointer', border: '1px solid #e0e0e0' }}
//               >
//                 <div className="d-flex justify-content-between align-items-center mb-2">
//                   <div className="form-check">
//                     <input
//                       type="checkbox"
//                       className="form-check-input"
//                       id={`queryCheckbox${query.qid}`}
//                       checked={selectedQuery === query.qid}
//                       onChange={() => handleCheckboxChange(query.qid)}
//                     />
//                     <label className="form-check-label ms-2" htmlFor={`queryCheckbox${query.qid}`}>
//                       Query ID {query.qid}
//                     </label>
//                   </div>
//                   <span
//                     className={`btn btn-sm btn-outline-dark ${
//                       query.status === 'Pending' ? 'text-dark' : 'text-success'
//                     }`}
//                   >
//                     {query.status || 'Open'}
//                   </span>
//                 </div>
//                 <hr style={{ borderTop: '2px dashed #BABABA' }} />
//                 <div className="d-flex align-items-start mt-3">
//                   <img
//                     src={profilePic1}
//                     alt="Profile"
//                     className="rounded-circle"
//                     style={{ width: '60px', height: '60px', marginRight: '15px' }}
//                   />
//                   <div className="flex-grow-1">
//                     <h6 className="mb-1">{query.buyerUId}</h6>
//                     <p className="mb-0 text-muted">{`${query.productName} ${query.varietyName}`}</p>
//                   </div>
//                   <div className="text-end">
//                     <div className="text-muted">Query Date</div>
//                     <p className="mb-0">{query.date}</p>
//                     <div className="mb-1 text-muted mt-2">Location</div>
//                     <p className="mb-0">{query.city}, {query.state}</p>
//                   </div>
//                 </div>
//                 <button
//                   className="btn btn-dark mt-3 w-50"
//                   onClick={() => navigate(`/query-details/${query.qid}`, { state: { query } })}
//                 >
//                   View Query Details
//                 </button>
//                 {/* <button
//                   className="btn btn-primary mt-2 w-50"
//                   onClick={() => handleOpenProductDetails(query.qid)}
//                 >
//                   Open Query Product
//                 </button> */}
//               </div>
//             ))}
//           </div>

//           <div className="col-md-7">
//             <div className="card p-4 shadow-sm">
//               <h5 className="fw-bold mb-4">Timeline</h5>
//               {selectedQuery ? (
//                 <div className="timeline-container">
//                   {queries.find((query) => query.qid === selectedQuery)?.timeline?.length > 0 ? (
//                     queries
//                       .find((query) => query.qid === selectedQuery)
//                       .timeline.map((event, index) => (
//                         <div key={index} className="timeline-item d-flex mb-4 p-3 shadow-sm">
//                           <div className="col-12 d-flex flex-column">
//                             <div className="d-flex justify-content-between align-items-center">
//                               <span className="timeline-dot me-3"></span>
//                               <p className="mb-0">{event.description}</p>
//                             </div>
//                           </div>
//                           <div className="col-md-5 text-end">
//                             <small className="text-muted">
//                               {event.time}
//                               <br />
//                               {event.date}
//                             </small>
//                           </div>
//                         </div>
//                       ))
//                   ) : (
//                     <p className="text-muted">No timeline events available for this query.</p>
//                   )}
//                 </div>
//               ) : (
//                 <p className="text-muted">Select a query to view its timeline.</p>
//               )}

//               {selectedQuery && (
//                 <div className="d-flex flex-column mt-3 mb-4">
//                   <div className="col-md-6">
//                     <button className="btn btn-dark mb-2" onClick={handleSendQuery}>
//                       Send Query
//                     </button>
//                     <button
//   className="btn btn-dark mb-2"
//   onClick={() => handleOpenPriceDetails(selectedQuery)} // Use selectedQuery here
// >
//   View Price Details
// </button>

                   
//                     <button
//                       className="btn btn-dark mb-2"
//                       onClick={() =>
//                         navigate(`/adminQuoteToBuyer/${selectedQuery}`, {
//                           state: { productId: selectedQuery },
//                         })
//                       }
//                     >
//                       Admin Quote to Buyer
//                     </button>
//                     <button
//                       className="btn btn-dark"
//                       onClick={() => handleSendNegotiationToBuyer(selectedQuery)}
//                     >
//                       Send Negotiation to Buyer
//                     </button>
//                   </div>
//                 </div>
//               )}
//             </div>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default Query;
// File: Query.jsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import InquiriesService from "../../Services/InquiriesService";
import profilePic1 from "../../assets/Ellipse 3.png";
import Topbar from "../Dashboard/Topbar";
import axios from "axios";

const Query = () => {
  const [selectedQuery, setSelectedQuery] = useState(null);
  const [queries, setQueries] = useState([]);
  const [userMappings, setUserMappings] = useState({}); // Map buyerUId to fullName

  const navigate = useNavigate();

  // Fetch all users and build a mapping of userId -> fullName
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get(
          "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/allUsers",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("authToken")}`,
            },
          }
        );
        const users = response.data;
        const mappings = users.reduce((map, user) => {
          map[user.userId] = user.fullName;
          return map;
        }, {});
        setUserMappings(mappings);
      } catch (error) {
        console.error("Error fetching user mappings:", error.message);
        alert("Failed to fetch user details. Please try again.");
      }
    };

    fetchUsers();
  }, []);

  // Fetch all inquiries
  useEffect(() => {
    const fetchInquiries = async () => {
      try {
        const data = await InquiriesService.fetchAllInquiries();
        setQueries(data);
      } catch (error) {
        console.error("Error fetching inquiries:", error.message);
        if (error.message === "Token is not found or is expired.") {
          alert("Session expired. Please log in again.");
          navigate("/login");
        }
      }
    };

    fetchInquiries();
  }, [navigate]);

  const handleOpenPriceDetails = async (queryId) => {
    try {
      const inquiryDetails = await InquiriesService.getInquiryById(queryId);
      const priceDetails = inquiryDetails.sellerNegotiations || []; // Fallback to an empty array if not found
      navigate(`/price-details/${queryId}`, { state: { priceDetails, queryId } });
    } catch (error) {
      if (error.response?.status === 403) {
        alert("Session expired. Please log in again.");
        navigate("/login");
      } else {
        console.error("Error fetching inquiry details:", error.message);
        alert("Failed to fetch inquiry details. Please try again later.");
      }
    }
  };

  const handleCheckboxChange = (queryId) => {
    setSelectedQuery((prevQueryId) => (prevQueryId === queryId ? null : queryId));
  };

  const handleSendQuery = () => {
    if (selectedQuery) {
      navigate(`/send-query/${selectedQuery}`);
    } else {
      alert("Please select a query first.");
    }
  };

  const handleSendNegotiationToBuyer = async (queryId) => {
    try {
      const inquiryDetails = await InquiriesService.getInquiryById(queryId);
      if (!inquiryDetails.productId) {
        throw new Error("Product ID not found in inquiry details");
      }
      const productId = inquiryDetails.productId;
      navigate("/price-negotiation", { state: { queryId, productId } });
    } catch (error) {
      console.error("Error sending negotiation to buyer:", error.message);
      alert("Failed to initiate negotiation. Please try again.");
    }
  };

  return (
    <div className="d-flex flex-column">
      <Topbar title="Queries" userName="Neha Sharma" showSearchBar={false} />

      <div className="container mt-4">
        <div className="row">
          <div className="col-md-5">
            <input
              type="text"
              className="form-control yellow-border"
              placeholder="Search by Order ID"
              onChange={(e) => console.log(e.target.value)} // Handle search functionality here
            />
            <h5 className="my-4">Today's Queries</h5>

            {queries.map((query) => (
              <div
                key={query.qid}
                className={`card p-3 shadow-sm mb-3 ${selectedQuery === query.qid ? "selected-query" : ""}`}
                style={{ cursor: "pointer", border: "1px solid #e0e0e0" }}
              >
                <div className="d-flex justify-content-between align-items-center mb-2">
                  <div className="form-check">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      id={`queryCheckbox${query.qid}`}
                      checked={selectedQuery === query.qid}
                      onChange={() => handleCheckboxChange(query.qid)}
                    />
                    <label className="form-check-label ms-2" htmlFor={`queryCheckbox${query.qid}`}>
                      Query ID {query.qid}
                    </label>
                  </div>
                  <span
                    className={`btn btn-sm btn-outline-dark ${
                      query.status === "Pending" ? "text-dark" : "text-success"
                    }`}
                  >
                    {query.status || "Open"}
                  </span>
                </div>
                <hr style={{ borderTop: "2px dashed #BABABA" }} />
                <div className="d-flex align-items-start mt-3">
                  <img
                    src={profilePic1}
                    alt="Profile"
                    className="rounded-circle"
                    style={{ width: "60px", height: "60px", marginRight: "15px" }}
                  />
                  <div className="flex-grow-1">
                    <h6 className="mb-1">
                      {userMappings[query.buyerUId] || "Unknown Buyer"} {/* Fetch buyer name */}
                    </h6>
                    <p className="mb-0 text-muted">{`${query.productName} ${query.varietyName}`}</p>
                  </div>
                  <div className="text-end">
                    <div className="text-muted">Query Date</div>
                    <p className="mb-0">{query.date}</p>
                    <div className="mb-1 text-muted mt-2">Location</div>
                    <p className="mb-0">{`${query.city}, ${query.state}`}</p>
                  </div>
                </div>
                <button
                  className="btn btn-dark mt-3 w-50"
                  onClick={() => navigate(`/query-details/${query.qid}`, { state: { query } })}
                >
                  View Query Details
                </button>
              </div>
            ))}
          </div>

          <div className="col-md-7">
            <div className="card p-4 shadow-sm">
              <h5 className="fw-bold mb-4">Timeline</h5>
              {selectedQuery ? (
                <div className="timeline-container">
                  {queries.find((query) => query.qid === selectedQuery)?.timeline?.length > 0 ? (
                    queries
                      .find((query) => query.qid === selectedQuery)
                      .timeline.map((event, index) => (
                        <div key={index} className="timeline-item d-flex mb-4 p-3 shadow-sm">
                          <div className="col-12 d-flex flex-column">
                            <div className="d-flex justify-content-between align-items-center">
                              <span className="timeline-dot me-3"></span>
                              <p className="mb-0">{event.description}</p>
                            </div>
                          </div>
                          <div className="col-md-5 text-end">
                            <small className="text-muted">
                              {event.time}
                              <br />
                              {event.date}
                            </small>
                          </div>
                        </div>
                      ))
                  ) : (
                    <p className="text-muted">No timeline events available for this query.</p>
                  )}
                </div>
              ) : (
                <p className="text-muted">Select a query to view its timeline.</p>
              )}
              {selectedQuery && (
                <div className="d-flex flex-column mt-3 mb-4">
                  <div className="col-md-6">
                    <button className="btn btn-dark mb-2" onClick={handleSendQuery}>
                      Send Query
                    </button>
                    <button
                      className="btn btn-dark mb-2"
                      onClick={() => handleOpenPriceDetails(selectedQuery)}
                    >
                      View Price Details
                    </button>
                    <button
                      className="btn btn-dark mb-2"
                      onClick={() =>
                        navigate(`/adminQuoteToBuyer/${selectedQuery}`, {
                          state: { productId: selectedQuery },
                        })
                      }
                    >
                      Admin Quote to Buyer
                    </button>
                    <button
                      className="btn btn-dark"
                      onClick={() => handleSendNegotiationToBuyer(selectedQuery)}
                    >
                      Send Negotiation to Buyer
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Query;
