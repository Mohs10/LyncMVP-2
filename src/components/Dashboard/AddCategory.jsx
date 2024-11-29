import React, { useState, useEffect } from 'react';
import { getCategories } from '../../Services/AddCategoryService'; // Ensure correct import paths

const AddCategory = ({ selectedCategory, setSelectedCategory }) => {
  const [allCategories, setAllCategories] = useState([]); // State to hold all available categories
  const [error, setError] = useState('');

  // Fetch available categories when the component mounts
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const categories = await getCategories(); // Fetch categories from the service
        setAllCategories(categories);
      } catch (error) {
        setError('Failed to fetch available categories.');
      }
    };
    fetchCategories();
  }, []);

  return (
    <div className="form-group row mb-3">
      <label className="col-12 col-form-label">Category</label>
      <div className="col-12">
        {/* Dropdown for selecting category */}
        <select
          className="form-control"
          value={selectedCategory || ''}
          onChange={(e) => setSelectedCategory(e.target.value)}
          disabled={!allCategories.length}
        >
          <option value="">Select a category</option>
          {allCategories.map((category, index) => (
            <option key={index} value={category.categoryId}>
              {category.categoryName}
            </option>
          ))}
        </select>
      </div>

      {error && (
        <div className="col-12 mt-2 text-danger">
          <small>{error}</small>
        </div>
      )}
    </div>
  );
};

export default AddCategory;
