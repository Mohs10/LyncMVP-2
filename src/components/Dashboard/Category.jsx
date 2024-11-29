import React, { useState, useEffect } from 'react';
import Topbar from './Topbar'; // Assuming you have a Topbar component
import Back from './Back'; // Assuming you have a Back component
import { getAllCategories, addCategory } from '../../Services/CategoryService'; // Import Category Service

const CategoriesPerPage = 10; // Set number of categories to display per page

const AddCategory = () => {
  const [categoryName, setCategoryName] = useState('');
  const [categories, setCategories] = useState([]); // Initialize as an empty array
  const [currentPage, setCurrentPage] = useState(1); // Track the current page
  const [successMessage, setSuccessMessage] = useState(''); // For success notification
  const [errorMessage, setErrorMessage] = useState(''); // For error notification

  // Fetch categories from the API when the component mounts
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const fetchedCategories = await getAllCategories();
        setCategories(fetchedCategories); // Set the categories from the API
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };

    fetchCategories();
  }, []);

  const handleInputChange = (e) => {
    setCategoryName(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (categoryName.trim()) {
      try {
        await addCategory(categoryName); // Add the new category through the service
        setCategoryName(''); // Reset the input field
        setSuccessMessage('Category added successfully!'); // Set success message
        setErrorMessage(''); // Clear any previous error message

        // Fetch updated categories
        const fetchedCategories = await getAllCategories();
        setCategories(fetchedCategories); // Update the categories state

        // Hide success message after 3 seconds
        setTimeout(() => setSuccessMessage(''), 3000);
      } catch (error) {
        console.error('Error adding category:', error);
      }
    } else {
      // If category name is empty, show error message
      setErrorMessage('This field is required.');
    }
  };

  // Pagination logic
  const indexOfLastCategory = currentPage * CategoriesPerPage;
  const indexOfFirstCategory = indexOfLastCategory - CategoriesPerPage;
  const currentCategories = categories.slice(indexOfFirstCategory, indexOfLastCategory);

  const totalPages = Math.ceil(categories.length / CategoriesPerPage);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  return (
    <div className="d-flex flex-column">
      {/* Topbar */}
      <Topbar title="Add Category" userName="Neha Sharma" showSearchBar={false} />

      {/* Page content */}
      <div className="container-fluid mt-4">
        <div className="margin-bottom-custom">
          <Back /> {/* Back button */}
        </div>

        {/* Row with two cards */}
        <div className="row">
          {/* Left Card - Add Category Form */}
          <div className="col-md-6 mb-4">
            <div className="card p-4">
              <h4>Add Category</h4>

              {/* Success Message Inside Card */}
              {successMessage && (
                <div className="alert alert-success" role="alert">
                  {successMessage}
                </div>
              )}

              {/* Error Message Inside Card */}
              {errorMessage && (
                <div className="alert alert-danger" role="alert">
                  {errorMessage}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="form-group mb-4">
                  <label>Category Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Enter Category Name"
                    value={categoryName}
                    onChange={handleInputChange}
                  />
                </div>

                {/* Add Category Button */}
                <div className="d-flex justify-content-end">
                  <button className="btn btn-warning" type="submit">
                    + Add Category
                  </button>
                </div>
              </form>
            </div>
          </div>

          {/* Right Card - List of Categories */}
          <div className="col-md-6 mb-4">
            <div className="card p-4">
              <h4>Category List</h4>
              <ul className="list-group">
                {currentCategories.length > 0 ? (
                  currentCategories.map((category, index) => (
                    <li key={index} className="list-group-item">
                      {category.categoryName}
                    </li>
                  ))
                ) : (
                  <li className="list-group-item">No categories added yet.</li>
                )}
              </ul>

              {/* Pagination Controls */}
              {totalPages > 1 && (
                <nav aria-label="Page navigation">
                  <ul className="pagination pagination-custom mt-3">
                    
                    {[...Array(totalPages).keys()].map((pageNumber) => (
                      <li
                        key={pageNumber}
                        className={`page-item ${pageNumber + 1 === currentPage ? 'active' : ''}`}
                        onClick={() => handlePageChange(pageNumber + 1)}
                      >
                        <span className="page-link">{pageNumber + 1}</span>
                      </li>
                    ))}
                  </ul>
                </nav>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddCategory;
