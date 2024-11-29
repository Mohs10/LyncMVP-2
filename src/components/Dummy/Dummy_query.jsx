import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import profilePic1 from '../../assets/Ellipse 3.png';
import Topbar from '../Dashboard/Topbar';

const Query = () => {
  const [selectedQuery, setSelectedQuery] = useState(null);
  const timelineEndRef = useRef(null);
  const navigate = useNavigate();

  // Sample queries data with a description field for products
  const queries = [
    {
      id: 1,
      name: 'Aanjali Mehta',
      products: '+3 Product',
      description: 'This is the detailed description of the query product. It includes specifications, usage, and pricing.',
      date: '26th July 2024',
      location: 'Maharashtra, Pune',
      timeline: [
        { time: '8:00pm', date: '19th Aug 2024', description: 'Testing Requested', actions: ['Add Details'] },
        { time: '8:00pm', date: '19th Aug 2024', description: 'Order Placed', actions: ['View Details', 'Notify Buyer'] },
        { time: '6:00pm', date: '19th Aug 2024', description: 'Sample Requested', actions: ['View Details'] },
        { time: '3:45pm', date: '19th Aug 2024', description: 'Price Sent to Buyer', actions: ['View Details'] },
        { time: '2:45pm', date: '18th Aug 2024', description: 'Requests Sent to Sellers', actions: ['View Price Updates'] },
        { time: '2:00pm', date: '18th Aug 2024', description: 'Query Open', actions: ['Send Query'] },
      ],
    },
  ];

  useEffect(() => {
    if (timelineEndRef.current) {
      timelineEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [selectedQuery]);

  // Function to handle action button clicks
  const handleActionClick = (action, event) => {
    switch (action) {
      case 'Send Query':
        navigate('/send-query');
        break;
      case 'View Price Updates':
        navigate(`/price-details/${selectedQuery}`);
        break;
      case 'View Details':
        if (event.description === 'Sample Requested') {
          navigate('/sample-request');
        } else {
          console.log('Action:', action);
        }
        break;
      default:
        console.log('Unknown action:', action);
    }
  };

  return (
    <div className="d-flex flex-column">
      {/* Top Bar */}
      <Topbar title="Testimonials" userName="Neha Sharma" showSearchBar={false} />

      {/* Main Content Container */}
      <div className="container mt-4">
        <div className="row">
          {/* Left Panel - Queries List */}
          <div className="col-md-5">
            <input
              type="text"
              className="form-control yellow-border"
              placeholder="Search by Order ID"
              onChange={(e) => console.log(e.target.value)}
            />
            <h5 className="my-4">Today's Queries</h5>

            {queries.map((query) => (
              <div
                key={query.id}
                className={`card p-3 shadow-sm mb-3 ${selectedQuery === query.id ? 'selected-query' : ''}`}
                onClick={() => setSelectedQuery(query.id)}
                style={{ cursor: 'pointer', border: '1px solid #e0e0e0' }}
              >
                <div className="d-flex justify-content-between align-items-center mb-2">
                  <div className="form-check">
                    <input type="checkbox" className="form-check-input" id={`queryCheckbox${query.id}`} />
                    <label className="form-check-label ms-2" htmlFor={`queryCheckbox${query.id}`}>
                      Query ID {query.id}
                    </label>
                  </div>
                  <button
                    className="btn btn-outline-primary btn-sm"
                    onClick={() => navigate(`/query-product/${query.id}`)}
                  >
                    Open
                  </button>
                </div>
                <hr style={{ borderTop: '2px dashed #BABABA' }} />
                <div className="d-flex align-items-start mt-3">
                  <img
                    src={profilePic1}
                    alt="Profile"
                    className="rounded-circle"
                    style={{ width: '60px', height: '60px', marginRight: '15px' }}
                  />
                  <div className="flex-grow-1">
                    <h6 className="mb-1">{query.name}</h6>
                    <p className="mb-0 text-muted">{query.products}</p>
                  </div>
                  <div className="text-end">
                  <div className="text-muted">Query Date</div>
                    <p className="mb-0">{query.date}</p>
                    <div className="mb-1 text-muted mt-2">Location</div>
                    <p className="mb-0">{query.location}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Right Panel - Timeline */}
          <div className="col-md-7">
            <div className="card p-4 shadow-sm">
              <div className="col-md-9">
                <h5 className="fw-bold mb-4">Timeline</h5>
                {selectedQuery !== null ? (
                  <div className="timeline-container">
                    {queries
                      .find((query) => query.id === selectedQuery)
                      ?.timeline
                      .map((event, index) => (
                        <div key={index} className="timeline-item d-flex mb-4 p-3 shadow-sm">
                          {/* Left Column - Description and Actions */}
                          <div className="col-12 d-flex flex-column">
                            <div className="d-flex justify-content-between align-items-center">
                              <span className="timeline-dot me-3"></span>
                              <p className="mb-0 poppins-medium-12px">{event.description}</p>
                              <div className="d-flex flex-column align-items-start">
                                {event.actions.map((action, i) => (
                                  <button
                                    key={i}
                                    className="btn btn-dark btn-sm mb-2 btn-underline"
                                    onClick={() => handleActionClick(action, event)}
                                  >
                                    {action}
                                  </button>
                                ))}
                              </div>
                            </div>
                          </div>

                          {/* Right Column - Time and Date */}
                          <div className="col-md-5 text-end">
                            <small className="text-muted">
                              {event.time}
                              <br />
                              {event.date}
                            </small>
                          </div>
                        </div>
                      ))}
                    <div ref={timelineEndRef}></div> {/* Reference for auto-scroll */}
                  </div>
                ) : (
                  <p className="text-muted">Select a query to view its timeline.</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Query;
