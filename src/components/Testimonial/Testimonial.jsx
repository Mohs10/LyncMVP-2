import React, { useState, useEffect } from "react";
import { addTestimonial, getAllTestimonials, updateTestimonial, deleteTestimonial } from '../../Services/TestimonialService'; // Import the services
import Topbar from "../Dashboard/Topbar";

const Testimonial = () => {
  const [testimonials, setTestimonials] = useState([]);
  const [testimonialText, setTestimonialText] = useState("");
  const [name, setName] = useState("");
  const [organization, setOrganization] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [editingTestimonial, setEditingTestimonial] = useState(null); // Track editing testimonial

  // Fetch testimonials when the component mounts
  useEffect(() => {
    const fetchTestimonials = async () => {
      try {
        const data = await getAllTestimonials(); // Fetch all testimonials
        setTestimonials(data); // Set fetched data to state
      } catch (error) {
        setErrorMessage(error.message); // Handle any errors
      }
    };

    fetchTestimonials(); // Call the fetch function
  }, []); // Empty dependency array ensures this runs once when the component mounts

  const handleAddTestimonial = async (e) => {
    e.preventDefault();

    // Input validation
    if (!testimonialText.trim() || !name.trim()) {
      setErrorMessage("Name and testimonial content are required.");
      return;
    }

    const newTestimonialData = {
      name,
      organizationName: organization || null,
      content: testimonialText,
    };

    try {
      let response;
      if (editingTestimonial) {
        // If editing, update the testimonial
        response = await updateTestimonial(editingTestimonial.id, newTestimonialData);
        setSuccessMessage("Testimonial updated successfully!");
      } else {
        // If adding, create a new testimonial
        response = await addTestimonial(newTestimonialData);
        setSuccessMessage("Testimonial added successfully!");
      }

      // Update the testimonials list
      setTestimonials((prevTestimonials) =>
        editingTestimonial
          ? prevTestimonials.map((testimonial) =>
              testimonial.id === editingTestimonial.id ? response : testimonial
            )
          : [response, ...prevTestimonials]
      );

      // Clear form fields
      setTestimonialText("");
      setName("");
      setOrganization("");
      setErrorMessage("");
      setEditingTestimonial(null); // Reset editing state
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  const handleDeleteTestimonial = async (id) => {
    try {
      await deleteTestimonial(id); // Call API to delete the testimonial
      setTestimonials(testimonials.filter((testimonial) => testimonial.id !== id)); // Remove from state
      setSuccessMessage("Testimonial deleted successfully!");
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  const handleEditTestimonial = (id) => {
    const testimonialToEdit = testimonials.find((testimonial) => testimonial.id === id);
    setEditingTestimonial(testimonialToEdit);
    setTestimonialText(testimonialToEdit.content);
    setName(testimonialToEdit.name);
    setOrganization(testimonialToEdit.organizationName || "");
  };

  return (
    <div className="d-flex flex-column">
      {/* Top Bar */}
      <Topbar title="Testimonials" userName="Neha Sharma" showSearchBar={false} />

      <div className="container-fluid mt-4">
        {/* Form Section */}
        <form onSubmit={handleAddTestimonial} className="mb-4">
          {errorMessage && (
            <div className="alert alert-danger" role="alert">
              {errorMessage}
            </div>
          )}
          {successMessage && (
            <div className="alert alert-success" role="alert">
              {successMessage}
            </div>
          )}
          <div className="mb-3">
            <label htmlFor="testimonialText" className="form-label">
              Type Testimonial
            </label>
            <textarea
              id="testimonialText"
              className="form-control"
              rows="6"
              value={testimonialText}
              onChange={(e) => setTestimonialText(e.target.value)}
            />
          </div>

          <div className="row mb-3">
            <div className="col-md-6">
              <label htmlFor="name" className="form-label">
                Name
              </label>
              <input
                type="text"
                id="name"
                className="form-control"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>
            <div className="col-md-6">
              <label htmlFor="organization" className="form-label">
                Organization
              </label>
              <input
                type="text"
                id="organization"
                className="form-control"
                value={organization}
                onChange={(e) => setOrganization(e.target.value)}
              />
            </div>
          </div>
          <button type="submit" className="btn btn-dark">
            {editingTestimonial ? "Update" : "Add"} {/* Show "Update" if editing */}
          </button>
        </form>

        {/* Testimonials List */}
        <div className="testimonials-container">
          <h3 className="mb-4 text-white">Testimonials</h3>
          <div className="row">
            {testimonials.length > 0 ? (
              testimonials.map((testimonial) => (
                <div key={testimonial.id} className="col-md-4 col-sm-12 mb-3">
                  <div className="card p-4 shadow-sm testimonial-card">
                    <blockquote className="blockquote mb-0 text-left">
                      {/* Starting Quote Icon */}
                      <div className="testimonial-quote-icon">
                        <i className="fas fa-quote-left"></i>
                      </div>
                      {/* Testimonial Text */}
                      <p className="testimonial-text">{testimonial.content}</p>
                      {/* Ending Quote Icon */}
                      <div className="testimonial-quote-icon">
                        <i className="fas fa-quote-right"></i>
                      </div>
                    </blockquote>
</div>
                    {/* Card Footer with Name and Organization */}
                    <div className="card-footer testimonial-footer d-flex justify-content-between align-items-center bg-dark">
                      <div className="testimonial-info ms-3 my-2">
                        <strong>{testimonial.name}</strong>
                        <div>{testimonial.organizationName || "No Organization"}</div>
                      </div>

                      <div className="d-flex align-items-center">
                        <button
                          className="btn btn-outline-light btn-sm border-0"
                          onClick={() => handleEditTestimonial(testimonial.id)}
                        >
                          <i className="fas fa-edit"></i>
                        </button>
                        <button
                          className="btn btn-outline-light btn-sm me-3 border-0"
                          onClick={() => handleDeleteTestimonial(testimonial.id)}
                        >
                          <i className="fas fa-trash"></i>
                        </button>
                      </div>
                    </div>
                  </div>
               
              ))
            ) : (
              <p>No testimonials added yet.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Testimonial;
