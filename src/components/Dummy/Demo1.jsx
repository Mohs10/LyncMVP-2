import React, { useState } from 'react';
import { addProduct } from '../../Services/AddProductService'; // Importing the service
import Topbar from './Topbar';
import AddVariety from './AddVariety'; // Component for variety selection
import AddCategory from './AddCategory'; // Component for category selection
import AddForm from './AddForm'; // Component for form selection

const AddProduct = () => {
  const [formData, setFormData] = useState({
    hsnCode: '',
    productName: '',
    productDescription: '',
  });

  const [addedVarieties, setAddedVarieties] = useState([]); // Multiple varieties
  const [addedForms, setAddedForms] = useState([]); // Multiple forms
  const [addedCategories, setAddedCategories] = useState([]); // Single category
  const [customCertifications, setCustomCertifications] = useState([]); // Custom certifications
  const [customSpecifications, setCustomSpecifications] = useState([]); // Custom specifications
  const [newSpecification, setNewSpecification] = useState(''); // New specification input
  const [newCertName, setNewCertName] = useState(''); // Input for new certification

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleAddCustomCertification = () => {
    if (newCertName.trim()) {
      setCustomCertifications((prev) => [
        ...prev,
        { certificationName: newCertName, isCertified: false },
      ]);
      setNewCertName('');
    }
  };

  const handleCustomCheckboxChange = (index) => {
    setCustomCertifications((prev) =>
      prev.map((cert, idx) =>
        idx === index ? { ...cert, isCertified: !cert.isCertified } : cert
      )
    );
  };

  const handleAddSpecification = () => {
    if (newSpecification.trim()) {
      setCustomSpecifications((prev) => [
        ...prev,
        { specificationName: newSpecification, specificationValue: '', specificationValueUnits: '' },
      ]);
      setNewSpecification('');
    }
  };

  const handleSpecificationChange = (index, field, value) => {
    setCustomSpecifications((prev) =>
      prev.map((spec, idx) =>
        idx === index ? { ...spec, [field]: value } : spec
      )
    );
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    // Prepare API payload
    const productData = {
      productName: formData.productName,
      hsnCode: formData.hsnCode,
      productDescription: formData.productDescription,
      categoryId: addedCategories[0], // Single category
      varietyIds: addedVarieties, // Multiple varieties
      formIds: addedForms, // Multiple forms
      certifications: [
        ...customCertifications,
      ],
      specifications: [
        ...customSpecifications,
      ],
    };

    console.log('Sending request with data:', productData);

    try {
      const response = await addProduct(productData);
      console.log('Product added successfully:', response);

      // Reset the form
      setFormData({ hsnCode: '', productName: '', productDescription: '' });
      setAddedVarieties([]);
      setAddedForms([]);
      setAddedCategories([]);
      setCustomCertifications([]);
      setCustomSpecifications([]);
    } catch (error) {
      console.error('Failed to add product:', error);
    }
  };

  return (
    <div className="d-flex flex-column">
      <Topbar title="Add Product" userName="Neha Sharma" showSearchBar={false} />

      <form className="container-fluid mt-4" onSubmit={handleFormSubmit}>
        <div className="row">
          {/* Left Section */}
          <div className="col-md-6">
            {/* HSN Code */}
            <div className="form-group row mb-3">
              <label className="col-12 col-form-label">HSN Code</label>
              <div className="col-12">
                <input
                  type="text"
                  name="hsnCode"
                  className="form-control"
                  placeholder="Enter HSN Code"
                  value={formData.hsnCode}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Product Name */}
            <div className="form-group row mb-3">
              <label className="col-12 col-form-label">Product Name</label>
              <div className="col-12">
                <input
                  type="text"
                  name="productName"
                  className="form-control"
                  placeholder="Enter Product Name"
                  value={formData.productName}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* Product Description */}
            <div className="form-group row mb-3">
              <label className="col-12 col-form-label">Description</label>
              <div className="col-12">
                <textarea
                  name="productDescription"
                  className="form-control"
                  rows="4"
                  placeholder="Enter Product Description"
                  value={formData.productDescription}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
          </div>

          {/* Right Section */}
          <div className="col-md-6">
            <AddCategory
              addedCategories={addedCategories}
              setAddedCategories={setAddedCategories}
            />
            <AddVariety
              addedVarieties={addedVarieties}
              setAddedVarieties={setAddedVarieties}
            />
            <AddForm
              addedForms={addedForms}
              setAddedForms={setAddedForms}
            />
          </div>
        </div>

        {/* Custom Certifications */}
        <div className="card mt-4">
          <div className="card-header">Custom Certifications</div>
          <div className="card-body">
            <div className="d-flex mb-3">
              <input
                type="text"
                className="form-control"
                placeholder="Add Certification Name"
                value={newCertName}
                onChange={(e) => setNewCertName(e.target.value)}
              />
              <button type="button" className="btn btn-primary ms-2" onClick={handleAddCustomCertification}>
                Add
              </button>
            </div>
            <ul className="list-group">
              {customCertifications.map((cert, index) => (
                <li key={index} className="list-group-item d-flex justify-content-between">
                  {cert.certificationName}
                  <input
                    type="checkbox"
                    checked={cert.isCertified}
                    onChange={() => handleCustomCheckboxChange(index)}
                  />
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Custom Specifications */}
        <div className="card mt-4">
          <div className="card-header">Custom Specifications</div>
          <div className="card-body">
            <div className="d-flex mb-3">
              <input
                type="text"
                className="form-control"
                placeholder="Add Specification Name"
                value={newSpecification}
                onChange={(e) => setNewSpecification(e.target.value)}
              />
              <button type="button" className="btn btn-primary ms-2" onClick={handleAddSpecification}>
                Add
              </button>
            </div>
            {customSpecifications.map((spec, index) => (
              <div key={index} className="row mb-3">
                <div className="col-4">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Specification Name"
                    value={spec.specificationName}
                    readOnly
                  />
                </div>
                <div className="col-4">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Specification Value"
                    value={spec.specificationValue}
                    onChange={(e) => handleSpecificationChange(index, 'specificationValue', e.target.value)}
                  />
                </div>
                <div className="col-4">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Units (e.g., kg, cm)"
                    value={spec.specificationValueUnits}
                    onChange={(e) => handleSpecificationChange(index, 'specificationValueUnits', e.target.value)}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Submit Button */}
        <div className="d-flex justify-content-center mt-4">
          <button type="submit" className="btn btn-success" style={{ width: '200px' }}>
            Submit
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddProduct;
