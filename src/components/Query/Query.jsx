// import React, { useState, useEffect } from "react";
// import { useNavigate } from "react-router-dom";
// import InquiriesService from "../../Services/InquiriesService";
// import profilePic1 from "../../assets/Ellipse 3.png";
// import Topbar from "../Dashboard/Topbar";
// import Pagination from "../Pagination/Pagination"; 
// import axios from "axios";

// const ProductsPerPage = 4;

// const Query = () => {
//   const [selectedQuery, setSelectedQuery] = useState(null);
//   const [queries, setQueries] = useState([]);
//   const [userMappings, setUserMappings] = useState({});
//   const [currentPage, setCurrentPage] = useState(1);
//   const navigate = useNavigate();

//   // Calculate total pages
//   const totalPages = Math.ceil(queries.length / ProductsPerPage);

//   // Fetch all users and build a mapping of userId -> fullName
//   useEffect(() => {
//     const fetchUsers = async () => {
//       try {
//         const response = await axios.get(
//           "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/allUsers",
//           {
//             headers: {
//               Authorization: `Bearer ${localStorage.getItem("authToken")}`,
//             },
//           }
//         );
//         const users = response.data;
//         const mappings = users.reduce((map, user) => {
//           map[user.userId] = user.fullName;
//           return map;
//         }, {});
//         setUserMappings(mappings);
//       } catch (error) {
//         console.error("Error fetching user mappings:", error.message);
//         alert("Failed to fetch user details. Please try again.");
//       }
//     };

//     fetchUsers();
//   }, []);

//   // Fetch all inquiries
//   useEffect(() => {
//     const fetchInquiries = async () => {
//       try {
//         const data = await InquiriesService.fetchAllInquiries();
//         setQueries(data);
//       } catch (error) {
//         console.error("Error fetching inquiries:", error.message);
//         if (error.message === "Token is not found or is expired.") {
//           alert("Session expired. Please log in again.");
//           navigate("/login");
//         }
//       }
//     };

//     fetchInquiries();
//   }, [navigate]);

//   const handleOpenPriceDetails = async (queryId) => {
//     try {
//       const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//       const priceDetails = inquiryDetails.sellerNegotiations || [];
//       navigate(`/price-details/${queryId}`, { state: { priceDetails, queryId } });
//     } catch (error) {
//       if (error.response?.status === 403) {
//         alert("Session expired. Please log in again.");
//         navigate("/login");
//       } else {
//         console.error("Error fetching inquiry details:", error.message);
//         alert("Failed to fetch inquiry details. Please try again later.");
//       }
//     }
//   };

//   const handleCheckboxChange = (queryId) => {
//     setSelectedQuery((prevQueryId) => (prevQueryId === queryId ? null : queryId));
//   };

//   const handleSendQuery = () => {
//     if (selectedQuery) {
//       navigate(`/send-query/${selectedQuery}`);
//     } else {
//       alert("Please select a query first.");
//     }
//   };

//   const handleSendNegotiationToBuyer = async (queryId) => {
//     try {
//       const inquiryDetails = await InquiriesService.getInquiryById(queryId);
//       if (!inquiryDetails.productId) {
//         throw new Error("Product ID not found in inquiry details");
//       }
//       const productId = inquiryDetails.productId;
//       navigate("/price-negotiation", { state: { queryId, productId } });
//     } catch (error) {
//       console.error("Error sending negotiation to buyer:", error.message);
//       alert("Failed to initiate negotiation. Please try again.");
//     }
//   };

//   const startIndex = (currentPage - 1) * ProductsPerPage;
//   const endIndex = startIndex + ProductsPerPage;
//   const currentQueries = queries.slice(startIndex, endIndex);

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
//             <h5 className="my-4">All Queries</h5>

//             {currentQueries.map((query) => (
//               <div
//                 key={query.qid}
//                 className={`card p-3 shadow-sm mb-3 ${selectedQuery === query.qid ? "selected-query" : ""}`}
//                 style={{ cursor: "pointer", border: "1px solid #e0e0e0" }}
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
//                       query.status === "Pending" ? "text-dark" : "text-success"
//                     }`}
//                   >
//                     {query.status || "Open"}
//                   </span>
//                 </div>
//                 <hr style={{ borderTop: "2px dashed #BABABA" }} />
//                 <div className="d-flex align-items-start mt-3">
//                   <img
//                     src={profilePic1}
//                     alt="Profile"
//                     className="rounded-circle"
//                     style={{ width: "60px", height: "60px", marginRight: "15px" }}
//                   />
//                   <div className="flex-grow-1">
//                     <h6 className="mb-1">
//                       {userMappings[query.buyerUId] || "Unknown Buyer"}
//                     </h6>
//                     <p className="mb-0 text-muted">{`${query.productName} ${query.varietyName}`}</p>
//                   </div>
//                   <div className="text-end">
//                     <div className="text-muted">Query Date</div>
//                     <p className="mb-0">{query.date}</p>
//                     <div className="mb-1 text-muted mt-2">Location</div>
//                     <p className="mb-0">{`${query.city}, ${query.state}`}</p>
//                   </div>
//                 </div>
//                 <button
//                   className="btn btn-outline-dark mt-3 w-50 "
//                   onClick={() => navigate(`/query-details/${query.qid}`, { state: { query } })}
//                 >
//                   View Query Details
//                 </button>
//               </div>
//             ))}
//             <Pagination
//               currentPage={currentPage}
//               totalPages={totalPages}
//               onPageChange={setCurrentPage}
//             />
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
//                {selectedQuery && (
//                 <div className="d-flex flex-column mt-3 mb-4">
//                   <div className="col-md-6">
//                     <button className="btn btn-dark mb-2" onClick={handleSendQuery}>
//                       Send Query
//                     </button>
//                     <button
//                       className="btn btn-dark mb-2"
//                       onClick={() => handleOpenPriceDetails(selectedQuery)}
//                     >
//                       View Price Details
//                     </button>
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
// import React, { useState, useEffect } from "react";
// import { useNavigate, useLocation } from "react-router-dom";
// import InquiriesService from "../../Services/InquiriesService";
// import profilePic1 from "../../assets/Ellipse 3.png";
// import Topbar from "../Dashboard/Topbar";
// import Pagination from "../Pagination/Pagination";
// import QueryTimeline from "../../components/Query/QueryTimeline";
// import axios from "axios";

// const ProductsPerPage = 3;

// const Query = () => {
//   const [selectedQuery, setSelectedQuery] = useState(null);
//   const [queries, setQueries] = useState([]);
//   const [filteredQueries, setFilteredQueries] = useState([]);
//   const [searchQuery, setSearchQuery] = useState("");
//   const [userMappings, setUserMappings] = useState({});
//   const [currentPage, setCurrentPage] = useState(1);
//   const navigate = useNavigate();
//   const location = useLocation();

//   const totalPages = Math.ceil(filteredQueries.length / ProductsPerPage);

//   // Restore state on component load
//   useEffect(() => {
//     if (location.state) {
//       const { restoredQuery, restoredPage, restoredSearch } = location.state;
//       setSelectedQuery(restoredQuery || null);
//       setCurrentPage(restoredPage || 1);
//       setSearchQuery(restoredSearch || "");
//     } else {
//       const restoredQuery = localStorage.getItem("selectedQuery");
//       const restoredPage = localStorage.getItem("currentPage");
//       const restoredSearch = localStorage.getItem("searchQuery");
//       setSelectedQuery(restoredQuery || null);
//       setCurrentPage(restoredPage ? parseInt(restoredPage, 10) : 1);
//       setSearchQuery(restoredSearch || "");
//     }
//   }, [location.state]);

//   // Persist state in localStorage
//   useEffect(() => {
//     localStorage.setItem("selectedQuery", selectedQuery || "");
//     localStorage.setItem("currentPage", currentPage);
//     localStorage.setItem("searchQuery", searchQuery);
//   }, [selectedQuery, currentPage, searchQuery]);

//   // Fetch all users and build mappings
//   useEffect(() => {
//     const fetchUsers = async () => {
//       try {
//         const response = await axios.get(
//           "http://lyncorganikness.ap-south-1.elasticbeanstalk.com/auth/admin/allUsers",
//           {
//             headers: {
//               Authorization: `Bearer ${localStorage.getItem("authToken")}`,
//             },
//           }
//         );
//         const users = response.data;
//         const mappings = users.reduce((map, user) => {
//           map[user.userId] = user.fullName;
//           return map;
//         }, {});
//         setUserMappings(mappings);
//       } catch (error) {
//         console.error("Error fetching user mappings:", error.message);
//         alert("Failed to fetch user details. Please try again.");
//       }
//     };

//     fetchUsers();
//   }, []);

//   // Fetch inquiries and sort by date
//   useEffect(() => {
//     const fetchInquiries = async () => {
//       try {
//         const data = await InquiriesService.fetchAllInquiries();
//         const sortedData = [...data].sort((a, b) => new Date(b.date) - new Date(a.date));
//         setQueries(sortedData);
//         setFilteredQueries(sortedData);
//       } catch (error) {
//         console.error("Error fetching inquiries:", error.message);
//         if (error.message === "Token is not found or is expired.") {
//           alert("Session expired. Please log in again.");
//           navigate("/login");
//         }
//       }
//     };

//     fetchInquiries();
//   }, [navigate]);

//   // Search functionality
//   const handleSearchChange = (e) => {
//     const searchTerm = e.target.value.toLowerCase();
//     setSearchQuery(searchTerm);

//     const filtered = queries.filter((queryItem) => {
//       const userName = userMappings[queryItem.buyerUId]?.toLowerCase() || "";
//       return (
//         queryItem.qid.toLowerCase().includes(searchTerm) || userName.includes(searchTerm)
//       );
//     });

//     setFilteredQueries(filtered);
//     setCurrentPage(1);
//   };

//   const handleCheckboxChange = (queryId) => {
//     setSelectedQuery((prevQueryId) => (prevQueryId === queryId ? null : queryId));
//   };

//   const handleViewDetails = (query) => {
//     navigate(`/query-details/${query.qid}`, {
//       state: {
//         restoredQuery: selectedQuery,
//         restoredPage: currentPage,
//         restoredSearch: searchQuery,
//       },
//     });
//   };

//   const startIndex = (currentPage - 1) * ProductsPerPage;
//   const endIndex = startIndex + ProductsPerPage;
//   const currentQueries = filteredQueries.slice(startIndex, endIndex);

//   return (
//     <div className="d-flex flex-column">
//       <Topbar title="Queries" userName="Neha Sharma" showSearchBar={false} />

//       <div className="container mt-4">
//         <div className="row">
//           <div className="col-md-5">
//             <input
//               type="text"
//               className="form-control yellow-border"
//               placeholder="Search by Query ID or User Name"
//               value={searchQuery}
//               onChange={handleSearchChange}
//             />
//             <h5 className="my-4">All Queries</h5>

//             {currentQueries.map((query) => (
//               <div
//                 key={query.qid}
//                 className={`card p-3 shadow-sm mb-3 ${
//                   selectedQuery === query.qid ? "selected-query" : ""
//                 }`}
//                 style={{ cursor: "pointer", border: "1px solid #e0e0e0" }}
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
//                       query.status === "Pending" ? "text-dark" : "text-success"
//                     }`}
//                   >
//                     {query.status || "Open"}
//                   </span>
//                 </div>
//                 <div className="d-flex align-items-start mt-3">
//                   <img
//                     src={profilePic1}
//                     alt="Profile"
//                     className="rounded-circle"
//                     style={{ width: "60px", height: "60px", marginRight: "15px" }}
//                   />
//                   <div className="flex-grow-1">
//                     <h6 className="mb-1">
//                       {userMappings[query.buyerUId] || "Unknown Buyer"}
//                     </h6>
//                     <p className="mb-0 text-muted">{`${query.productName} ${query.varietyName}`}</p>
//                   </div>
//                   <div className="text-end">
//                     <div className="text-muted">Query Date</div>
//                     <p className="mb-0">{query.date}</p>
//                     <div className="mb-1 text-muted mt-2">Location</div>
//                     <p className="mb-0">{`${query.city}, ${query.state}`}</p>
//                   </div>
//                 </div>
//                 <button
//                   className="btn btn-outline-dark mt-3 w-50"
//                   onClick={() => handleViewDetails(query)}
//                 >
//                   View Query Details
//                 </button>
//               </div>
//             ))}
//             <Pagination
//               currentPage={currentPage}
//               totalPages={totalPages}
//               onPageChange={setCurrentPage}
//             />
//           </div>

//           <div className="col-md-7">
//             <div className="card p-4 shadow-sm">
//               {selectedQuery ? (
//                 <QueryTimeline queryId={selectedQuery} />
//               ) : (
//                 <p className="text-muted">Select a query to view its timeline.</p>
//               )}
//             </div>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default Query;
import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import InquiriesService from "../../Services/InquiriesService";
import profilePic1 from "../../assets/Ellipse 3.png";
import Topbar from "../Dashboard/Topbar";
import Pagination from "../Pagination/Pagination";
import axios from "axios";
import QueryTimeline from "../../components/Query/QueryTimeline";
const ProductsPerPage = 3;

const Query = () => {
  const [selectedQuery, setSelectedQuery] = useState(null);
  const [queries, setQueries] = useState([]);
  const [filteredQueries, setFilteredQueries] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [userMappings, setUserMappings] = useState({});
  const [currentPage, setCurrentPage] = useState(1);
  const navigate = useNavigate();
  const location = useLocation();

  const totalPages = Math.ceil(filteredQueries.length / ProductsPerPage);

 
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
    
  // Fetch all users and build mappings
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

  // Fetch inquiries and sort by date and time
  useEffect(() => {
    const fetchInquiries = async () => {
      try {
        const data = await InquiriesService.fetchAllInquiries();
        const sortedData = [...data].sort((a, b) => {
          const dateTimeA = new Date(`${a.raiseDate}T${a.raiseTime}`);
          const dateTimeB = new Date(`${b.raiseDate}T${b.raiseTime}`);
          return dateTimeB - dateTimeA; // Sort descending
        });
        setQueries(sortedData);
        setFilteredQueries(sortedData);
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

  // Search functionality
  const handleSearchChange = (e) => {
    const searchTerm = e.target.value.toLowerCase();
    setSearchQuery(searchTerm);

    const filtered = queries.filter((queryItem) => {
      const userName = userMappings[queryItem.buyerUId]?.toLowerCase() || "";
      return (
        queryItem.qid.toLowerCase().includes(searchTerm) || userName.includes(searchTerm)
      );
    });

    setFilteredQueries(filtered);
    setCurrentPage(1);
  };

  const handleCheckboxChange = (queryId) => {
    setSelectedQuery((prevQueryId) => (prevQueryId === queryId ? null : queryId));
  };

  const handleViewDetails = (query) => {
    navigate(`/query-details/${query.qid}`, {
      state: {
        restoredQuery: selectedQuery,
        restoredPage: currentPage,
        restoredSearch: searchQuery,
      },
    });
  };
  const handleSendQuery = () => {
    if (selectedQuery) {
      navigate(`/send-query/${selectedQuery}`);
    } else {
      alert("Please select a query first.");
    }
  };
  const startIndex = (currentPage - 1) * ProductsPerPage;
  const endIndex = startIndex + ProductsPerPage;
  const currentQueries = filteredQueries.slice(startIndex, endIndex);

  return (
    <div className="d-flex flex-column">
      <Topbar title="Queries" userName="Neha Sharma" showSearchBar={false} />

      <div className="container mt-4">
        <div className="row">
          <div className="col-md-5">
            <input
              type="text"
              className="form-control yellow-border"
              placeholder="Search by Query ID or User Name"
              value={searchQuery}
              onChange={handleSearchChange}
            />
            <h5 className="my-4">All Queries</h5>

            {currentQueries.map((query) => (
              <div
                key={query.qid}
                className={`card p-3 shadow-sm mb-3 ${
                  selectedQuery === query.qid ? "selected-query" : ""
                }`}
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
                <div className="d-flex align-items-start mt-3">
                  <img
                    src={profilePic1}
                    alt="Profile"
                    className="rounded-circle"
                    style={{ width: "60px", height: "60px", marginRight: "15px" }}
                  />
                  <div className="flex-grow-1">
                    <h6 className="mb-1">
                      {userMappings[query.buyerUId] || "Unknown Buyer"}
                    </h6>
                    <p className="mb-0 text-muted">
                      {`${query.productName || ""} ${query.varietyName || ""}`}
                    </p>
                  </div>
                  <div className="text-end">
                    <div className="text-muted">Query Date</div>
                    <p className="mb-0">{query.raiseDate}</p>
                    <div className="text-muted mt-2">Query Time</div>
                    <p className="mb-0">{query.raiseTime}</p>
                    <div className="mb-1 text-muted mt-2">Location</div>
                    <p className="mb-0">{`${query.city || "N/A"}, ${query.state || "N/A"}`}</p>
                  </div>
                </div>
                <button
                  className="btn btn-outline-dark mt-3 w-50"
                  onClick={() => handleViewDetails(query)}
                >
                  View Query Details
                </button>
              </div>
            ))}
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
            />
          </div>

          <div className="col-md-7">
            <div className="card p-4 shadow-sm">
              {selectedQuery ? (
                <QueryTimeline queryId={selectedQuery} />
              ) : (
                <p className="text-muted">Select a query to view its timeline.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
   
    
  );
};

export default Query;

