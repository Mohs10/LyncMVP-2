// import React, { useState, useEffect } from 'react';
// import { useParams, useLocation, useNavigate } from 'react-router-dom';
// import InquiriesService from '../../Services/InquiriesService';
// import Topbar from '../Dashboard/Topbar'; // Assuming Topbar is a reusable component

// const QueryDetails = () => {
//   const { queryId } = useParams();
//   const { state } = useLocation();
//   const navigate = useNavigate();

//   const [query, setQuery] = useState(state?.query || null);
//   const [loading, setLoading] = useState(!query);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     if (!query) {
//       const fetchQueryDetails = async () => {
//         setLoading(true);
//         try {
//           const details = await InquiriesService.getInquiryById(queryId);
//           setQuery(details);
//           setError(null);
//         } catch (err) {
//           setError(err.message || 'Failed to fetch query details.');
//         } finally {
//           setLoading(false);
//         }
//       };

//       fetchQueryDetails();
//     }
//   }, [queryId, query]);

//   if (loading) {
//     return <p>Loading...</p>;
//   }

//   if (error) {
//     return (
//       <div>
//         <p className="text-danger">{error}</p>
//         <button className="btn btn-dark" onClick={() => navigate(-1)}>
//           Back to Queries
//         </button>
//       </div>
//     );
//   }

//   if (!query) {
//     return <p>No query details available.</p>;
//   }

//   return (
//     <div className="d-flex flex-column">
//       <Topbar title="Query Details" userName="Neha Sharma" showSearchBar={false} />

//       <div className="container-fluid mt-4">
//         <div className="row">
//           {/* Left Column: Buyer and Product Details */}
//           <div className="col-md-6">
//             <div className="card p-4 mb-4">
//               <h5 className="text-center mb-4 heading-with-bar">Buyer and Product Details</h5>
//               <div className="card-body">
//                 <p><strong>Query ID:</strong> {query.qid}</p>
//                 <p><strong>Buyer ID:</strong> {query.buyerUId}</p>
//                 <p><strong>Product:</strong> {query.productName} ({query.varietyName})</p>
//                 <p><strong>Quantity:</strong> {query.quantity} {query.quantityUnit}</p>
//                 <p><strong>Price Range:</strong> {query.askMinPrice} - {query.askMaxPrice} {query.priceUnit}</p>
//                 <p><strong>Price Terms:</strong> {query.priceTerms}</p>
//                 <p><strong>Packaging Material:</strong> {query.packagingMaterial}</p>
//               </div>
//             </div>

//             <div className="card p-4 mb-4">
//               <h5 className="text-center mb-4 heading-with-bar">Certifications</h5>
//               <ul>
//                 <li>NPOP: {query.npop ? 'Yes' : 'No'}</li>
//                 <li>NOP: {query.nop ? 'Yes' : 'No'}</li>
//                 <li>EU: {query.eu ? 'Yes' : 'No'}</li>
//                 <li>GSDC: {query.gsdc ? 'Yes' : 'No'}</li>
//                 <li>IPM: {query.ipm ? 'Yes' : 'No'}</li>
//                 <li>Other: {query.other ? query.otherCertification || 'Yes' : 'No'}</li>
//               </ul>
//             </div>
//           </div>

//           {/* Right Column: Delivery and Order Details */}
//           <div className="col-md-6">
//             <div className="card p-4 mb-4">
//               <h5 className="text-center mb-4 heading-with-bar">Delivery and Order Details</h5>
//               <div className="card-body">
//                 <p><strong>Delivery Address:</strong> {query.deliveryAddress}</p>
//                 <p><strong>City:</strong> {query.city}, {query.state}, {query.country}</p>
//                 <p><strong>Pincode:</strong> {query.pincode}</p>
//                 <p><strong>Delivery Date:</strong> {query.specifyDeliveryDate}</p>
//                 <p><strong>Target Lead Time:</strong> {query.targetLeadTime}</p>
//                 <p><strong>Payment Terms:</strong> {query.paymentTerms}</p>
//                 <p><strong>Order Status:</strong> {query.orderStatus || 'Pending'}</p>
//               </div>
//             </div>

//             <div className="card p-4 mb-4">
//               <h5 className="text-center mb-4 heading-with-bar">Order Timestamps</h5>
//               <div className="card-body">
//                 <p><strong>Raised On:</strong> {query.raiseDate} at {query.raiseTime}</p>
//                 <p><strong>Last Sent On:</strong> {query.sentDate} at {query.sentTime}</p>
//                 <p><strong>Seller ID:</strong> {query.sellerUId}</p>
//                 <p><strong>Seller Final Price:</strong> {query.sellerFinalPrice || 'N/A'}</p>
//               </div>
//             </div>
//           </div>
//         </div>

//         {/* Back Button */}
//         <div className="d-flex justify-content-center">
//           <button
//             className="btn btn-warning border me-1 text-center"
//             style={{ width: '200px' }}
//             onClick={() => navigate(-1)}
//           >
//             Back to Queries
//           </button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default QueryDetails;

import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import InquiriesService from '../../Services/InquiriesService';
import Topbar from '../Dashboard/Topbar'; // Assuming Topbar is a reusable component
import defaultImage from '../../assets/Product2.png';
import Back from '../Dashboard/Back';
const QueryDetails = () => {
  const { queryId } = useParams();
  const { state } = useLocation();
  const navigate = useNavigate();

  const [query, setQuery] = useState(state?.query || null);
  const [loading, setLoading] = useState(!query);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!query) {
      const fetchQueryDetails = async () => {
        setLoading(true);
        try {
          const details = await InquiriesService.getInquiryById(queryId);
          setQuery(details);
          setError(null);
        } catch (err) {
          setError(err.message || 'Failed to fetch query details.');
        } finally {
          setLoading(false);
        }
      };

      fetchQueryDetails();
    }
  }, [queryId, query]);

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return (
      <div>
        <p className="text-danger">{error}</p>
        <button className="btn btn-dark" onClick={() => navigate(-1)}>
          Back to Queries
        </button>
      </div>
    );
  }

  if (!query) {
    return <p>No query details available.</p>;
  }

  return (
    <div className="d-flex flex-column">
      {/* Topbar */}
      <Topbar title="Query Details" userName="Neha Sharma" showSearchBar={false} />

      <div className="container-fluid mt-4">
        <Back />
        <div className="row">
          {/* Left Column: Image and Buyer Details */}
          <div className="col-md-6">
            <div className="card p-4 mb-4">
              <div className="product-image-upload text-center mb-3">
                <img
                  src={defaultImage}
                  className="uploaded-image"
                  style={{ width: '100%', height: '300px', objectFit: 'cover' }}
                  alt="Product"
                />
              </div>
              <div className="card-body">
               
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Buyer ID</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.buyerUId}
                      readOnly
                    />
                  </div>
                </div>
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Query ID</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.qid}
                      readOnly
                    />
                  </div>
                </div>
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Specify Quantity</label>
                  <div className="col-12 d-flex">
                    <input
                      type="text"
                      className="form-control me-2"
                      value={query.quantity || 'N/A'}
                      readOnly
                    />
                    <input
                      type="text"
                      className="form-control"
                      value={query.quantityUnit || 'N/A'}
                      readOnly
                    />
                  </div>
                </div>
                <div className="form-group row mb-3">
  <label className="col-12 col-form-label">Price Terms</label>
  <div className="col-12">
    <input
      type="text"
      className="form-control"
      value={query.priceTerms || 'N/A'}
      readOnly
    />
  </div>
  </div>
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Price Range</label>
                  <div className="col-12 d-flex">
                    <input
                      type="text"
                      className="form-control me-2"
                      placeholder="Min"
                      value={query.askMinPrice || 'N/A'}
                      readOnly
                    />
                    <input
                      type="text"
                      className="form-control me-2"
                      placeholder="Max"
                      value={query.askMaxPrice || 'N/A'}
                      readOnly
                    />
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Unit"
                      value={query.priceUnit || 'N/A'}
                      readOnly
                    />
                  </div>
                </div>
                <p className='my-2'>Required certification</p>

  
  <div className="card-body checkbox-container">
    <div className="form-check mb-3">
      <input
        className="form-check-input custom-checkbox"
        type="checkbox"
        id="npop"
        checked={query.npop || false}
        readOnly
      />
      <label className="form-check-label" htmlFor="npop">
        NPOP
      </label>
    </div>
    <div className="form-check mb-3">
      <input
        className="form-check-input custom-checkbox"
        type="checkbox"
        id="nop"
        checked={query.nop || false}
        readOnly
      />
      <label className="form-check-label" htmlFor="nop">
        NOP
      </label>
    </div>
    <div className="form-check mb-3">
      <input
        className="form-check-input custom-checkbox"
        type="checkbox"
        id="eu"
        checked={query.eu || false}
        readOnly
      />
      <label className="form-check-label" htmlFor="eu">
        EU
      </label>
    </div>
    <div className="form-check mb-3">
      <input
        className="form-check-input custom-checkbox"
        type="checkbox"
        id="gsdc"
        checked={query.gsdc || false}
        readOnly
      />
      <label className="form-check-label" htmlFor="gsdc">
        GSDC
      </label>
    </div>
    <div className="form-check mb-3">
      <input
        className="form-check-input custom-checkbox"
        type="checkbox"
        id="ipm"
        checked={query.ipm || false}
        readOnly
      />
      <label className="form-check-label" htmlFor="ipm">
        IPM
      </label>
    </div>
    <div className="form-check mb-3">
      <input
        className="form-check-input custom-checkbox"
        type="checkbox"
        id="other"
        checked={query.other || false}
        readOnly
      />
      <label className="form-check-label" htmlFor="other">
        Other
      </label>
    </div>
  </div>
  <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Target Lead Time</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.targetLeadTime}
                      readOnly
                    />
                  </div>
                 </div>
                 
              </div>
            </div>
            {/* {query.specifications && query.specifications.length > 0 ? (
  <div className="row">
    <div className="col-md-12">
      <div className="card p-4 mb-4">
        <div className="card-body">
          <h6 className="mb-4">Specifications</h6>
          <div className="table-responsive">
            <table className="table">
              <tbody>
                {query.specifications.map((spec, index) => (
                  <tr key={index}>
                    <td style={{ fontSize: '12px', fontWeight: 'bold', color: '#333' }}>
                      {spec.specificationName}
                    </td>
                    <td>
                      <input
                        type="text"
                        className="form-control"
                        value={spec.specificationValue}
                        readOnly
                      />
                    </td>
                    <td>
                      <input
                        type="text"
                        className="form-control"
                        value={spec.specificationValueUnits === "N/A" ? "NA" : spec.specificationValueUnits}
                        readOnly
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
) : (
  <div className="row">
    <div className="col-md-12">
      <div className="card p-4 mb-4">
        <div className="card-body">
          <h6 className="mb-4">Specifications</h6>
          <p style={{ fontSize: '14px', color: '#555' }}>
            Buyer did'nt add any specifications for this Query
          </p>
        </div>
      </div>
    </div>
  </div>
)} */}


          </div>

          {/* Right Column: Product Details */}
          <div className="col-md-6">
            <div className="card p-4 mb-4">
              <div className="card-body">
               
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Product Name</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.productName}
                      readOnly
                    />
                  </div>
                </div>
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Product Description</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.formName}
                      readOnly
                    />
                  </div>
                </div>
                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Select Product Variety</label>
                  <div className="col-12">
                    <div className="variety-box">
                      <span>{query.varietyName || 'N/A'}</span>
                    </div>
                  </div>
                </div>
               
              </div>
            </div>
            <div className="col-md-12">
            <div className="card p-4 mb-4">
              <div className="card-body">
                
            <div className="col-md-12">
            <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Packaging Material</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.packagingMaterial}
                      readOnly
                    />
                  </div>
                  </div>
                  
  <div className="form-group row mb-3">
  <label className="col-12 col-form-label">Payment Terms</label>
  <div className="col-12">
    <input
      type="text"
      className="form-control"
      value={query.paymentTerms || 'N/A'}
      readOnly
    />
  </div>
  </div>
 
  <div className="form-group row mb-3">
  <label className="col-12 col-form-label">Delivery Location</label>
  <div className="col-12">
    <input
      type="text"
      className="form-control"
      value={query.deliveryAddress || ''}
      readOnly
    />
  </div>
</div>

<div className="form-group row mb-3">
  {/* Country and State */}
  <div className="col-md-6 mb-3">
    <label className="col-form-label">Country</label>
    <input
      type="text"
      className="form-control"
      value={query.country || ''}
      readOnly
    />
  </div>
  <div className="col-md-6 mb-3">
    <label className="col-form-label">State</label>
    <input
      type="text"
      className="form-control"
      value={query.state || ''}
      readOnly
    />
  </div>
</div>

<div className="form-group row mb-3">
  {/* City and Pin-code */}
  <div className="col-md-6 mb-3">
    <label className="col-form-label">City</label>
    <input
      type="text"
      className="form-control"
      value={query.city || ''}
      readOnly
    />
  </div>
  <div className="col-md-6 mb-3">
    <label className="col-form-label">Pin-code</label>
    <input
      type="text"
      className="form-control"
      value={query.pincode || ''}
      readOnly
    />
  </div>
</div>

                <div className="form-group row mb-3">
                  <label className="col-12 col-form-label">Delivery Date</label>
                  <div className="col-12">
                    <input
                      type="text"
                      className="form-control"
                      value={query.specifyDeliveryDate}
                      readOnly
                    />
                  </div>
                </div>
                </div>
                </div>
               
              </div>
            </div>
            </div>
          </div>
        </div>

        {/* Back Button */}
        <div className="d-flex justify-content-center">
          <button
            className="btn btn-warning border me-1 text-center"
            style={{ width: '200px' }}
            onClick={() => navigate(-1)}
          >
            Back to Queries
          </button>
        </div>
      </div>
    
  );
};

export default QueryDetails;

