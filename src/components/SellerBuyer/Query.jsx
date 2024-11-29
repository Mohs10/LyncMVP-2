import React from 'react';
import defaultProfilePic from '../../assets/Profile-pic.png'; // Update the path as necessary

const Query = ({ queries = [] }) => {
  if (!queries.length) {
    return <p>No queries available.</p>;
  }

  return (
    <div className="container mt-4">
      
      <div className="row">
        
        {queries.map((query) => (
          <div key={query.id} className="col-md-6 mb-4">
            <div className="card p-3 shadow-sm" style={{ border: '1px solid #e0e0e0', borderRadius: '10px' }}>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Query Id: {query.id}</span>
                <span className="badge badge-success">{query.status}</span>
              </div>
              <hr style={{ borderTop: '2px dashed #BABABA' }} />
              <div className="d-flex align-items-start mt-3">
                <img
                  src={ defaultProfilePic} // Fallback to default if no image is provided
                  alt="Profile"
                  className="rounded-circle"
                  style={{ width: '60px', height: '60px', marginRight: '15px' }}
                />
                <div className="flex-grow-1">
                  <h6 className="mb-1">{query.name}</h6>
                  </div>
                  <div className="text-end">
                  <div className="text-muted">Query Date</div>
                  <p className="mb-0">{query.date}</p>
                  <div className="text-muted mt-2">Location</div>
                  <p className="mb-0">{query.location}</p>
                </div>
                </div>
              </div>
            </div>
          
        ))}
      </div>
    </div>
  );
};

export default Query;
