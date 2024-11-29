import React, { useState } from 'react';
import { addProduct } from '../../Services/AddProductService'; // Importing the service
import Topbar from './Topbar';
import AddVariety from './AddVariety';
import AddCategory from './AddCategory'; // Import AddCategory component
import AddForm from './AddForm'; // Import AddForm component
import defaultImage from '../../assets/Product2.png';
const AddProduct = () => {
  const [productImage, setProductImage] = useState(null);
  const [formData, setFormData] = useState({
    productDescription: '',
    productVariety: '',
    productForm: '',
    hsnCode: '',
    productName: '',
    certifications: {
      nopp: false,
      nop: false,
      eu: false,
      gsdc: false,
    },
    chalkyGrains: '',
    grainSize: '',
    kettValue: '',
    moistureContent: '',
    brokenGrain: '',
    admixture: '',
    dd: '',
    anyOther: '', // State for "Any Other"
  });

  const [addedVarieties, setAddedVarieties] = useState([]);
  const [addedForms, setAddedForms] = useState([]); // State for added forms
  const [addedCategories, setAddedCategories] = useState([]); // State for added categories

  const handleImageUpload = (e) => {
    setProductImage(URL.createObjectURL(e.target.files[0]));
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (type === 'checkbox') {
      setFormData((prevData) => ({
        ...prevData,
        certifications: {
          ...prevData.certifications,
          [name]: checked,
        },
      }));
    } else {
      setFormData((prevData) => ({
        ...prevData,
        [name]: value,
      }));
    }
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    const productData = {
      productName: formData.productName,
      hsnCode: formData.hsnCode,
      categoryId: addedCategories[0], // Assuming the first category is selected
      varietyIds: addedVarieties,
      formIds: addedForms,
      productDescription: formData.productDescription,
      certifications: [
        { certificationName: 'NPOP', isCertified: formData.certifications.nopp },
        { certificationName: 'NOP', isCertified: formData.certifications.nop },
        { certificationName: 'EU', isCertified: formData.certifications.eu },
        { certificationName: 'GSDC', isCertified: formData.certifications.gsdc },
      ],
      specifications: [
        { specificationName: 'Weight', specificationValue: '1kg', specificationValueUnits: 'kg' },
        { specificationName: 'Dimensions', specificationValue: '20cm x 30cm', specificationValueUnits: 'cm' },
      ],
    };
    
    console.log('Sending request with data:', productData);
    
    try {
      const response = await addProduct(productData); // Call the service
      console.log('Product added successfully:', response);
      // Reset form data
      setFormData({
        productDescription: '',
        productVariety: '',
        productForm: '',
        hsnCode: '',
        productName: '',
        certifications: {
          nopp: false,
          nop: false,
          eu: false,
          gsdc: false,
        },
        chalkyGrains: '',
        grainSize: '',
        kettValue: '',
        moistureContent: '',
        brokenGrain: '',
        admixture: '',
        dd: '',
        anyOther: '',
      });
      setAddedVarieties([]);
      setAddedForms([]); // Clear added forms
      setAddedCategories([]); // Clear added categories
      setProductImage(null);
    } catch (error) {
      console.error('Failed to add product:', error);
    }
  };

  return (
    <div className="d-flex flex-column">
      <Topbar title="Add Product" userName="Neha Sharma" showSearchBar={false} />

      <form className="container-fluid mt-4" onSubmit={handleFormSubmit}>
        <div className="row">
          {/* Left Section: Image and Order Specifications */}
          <div className="col-md-6">
            <div className="product-image-upload">
              {productImage ? (
                <img
                  src={productImage}
                  alt="Product"
                  className="uploaded-image"
                  style={{ width: '400px', height: '278px' }}
                />
              ) : (
                <img
                  src={defaultImage}
                  alt="Default Product"
                  className="default-image"
                  style={{ width: '450px', height: '278px', objectFit: 'cover' }}
                />
              )}
            </div>

            <div className="card p-4 mb-4 mt-4">
            <h5 className="text-center mb-4 heading-with-bar">Order Specifications</h5>
            <div className='card-body'>
           
              {/* Product Description */}
              <div className="row mb-4">
                  <label className="col-12">Add Product Description</label>
                  <div className="col-12">
                    <textarea
                      name="productDescription"
                      className="form-control"
                      rows="4"
                      placeholder="Enter product description..."
                      value={formData.productDescription}
                      onChange={handleChange}
                    />
                  </div>
                </div>
                <div className="row mb-4">
                  <AddVariety
                    addedVarieties={addedVarieties}
                    setAddedVarieties={setAddedVarieties}
                  />
                 
                  <AddForm
                    addedForms={addedForms}
                    setAddedForms={setAddedForms}
                  />
                   <AddCategory
                    addedCategories={addedCategories}
                    setAddedCategories={setAddedCategories}
                  />
                </div>
                <div className="row mt-4 p-3 mb-4">
                  <h6>Required Certifications</h6>
                  <div className="col-4">
                    <div className="certification border-section">
                      <div className="form-check">
                        <input
                          className="form-check-input"
                          type="checkbox"
                          id="nopp"
                          onChange={handleChange}
                        />
                        <label className="form-check-label" htmlFor="nopp">
                          NPOP
                        </label>
                      </div>
                      <div className="form-check">
                        <input
                          className="form-check-input"
                          type="checkbox"
                          id="nop"
                          onChange={handleChange}
                        />
                        <label className="form-check-label" htmlFor="nop">
                          NOP
                        </label>
                      </div>
                      <div className="form-check">
                        <input
                          className="form-check-input"
                          type="checkbox"
                          id="eu"
                          onChange={handleChange}
                        />
                        <label className="form-check-label" htmlFor="eu">
                          EU
                        </label>
                      </div>
                      <div className="form-check">
                        <input
                          className="form-check-input"
                          type="checkbox"
                          id="gsdc"
                          onChange={handleChange}
                        />
                        <label className="form-check-label" htmlFor="gsdc">
                          GSDC
                        </label>
                      </div>
                    </div>
                  </div>
                  <div className="row mb-4">
                    <div className="col-md-12">
                      <label className="col-12"></label>
                      <div className="row">
                        <div className="col-8">
                          <input
                            type="text"
                            className="form-control"
                            placeholder="Any Others"
                          />
                        </div>
                        <div className="col-4 d-flex align-items-end">
                          <button
                            className="btn btn-dark"
                            style={{ height: '48px', width: '110px' }}
                          >
                            Add
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
         
            </div>
          {/* Right Section: Product Details and Specifications */}
          <div className="col-md-6">
            <div className="card p-4 mb-4">
          <div className='card-body'>
              <div className="form-group row mb-3">
                <label className="col-12 col-form-label">HSN Code</label>
                <div className="col-12">
                  <input
                    type="text"
                    name="hsnCode"
                    className="form-control"
                    placeholder="HSN Code"
                    value={formData.hsnCode}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <div className="form-group row mb-3">
                <label className="col-12 col-form-label">Product Name</label>
                <div className="col-12">
                  <input
                    type="text"
                    name="productName"
                    className="form-control"
                    placeholder="Product Name"
                    value={formData.productName}
                    onChange={handleChange}
                  />
                </div>
              </div>

              <input type="file" onChange={handleImageUpload} className="form-control mt-2" />
            </div>
            </div>
            <div className="col-md-12">
              <div className="card p-4 mb-4">
              <h5 className="text-center mb-4 heading-with-bar">Product Specifications</h5>
              <div className='card-body'>
                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Chalky Grains</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="chalkyGrains"
                      className="form-control"
                      value={formData.chalkyGrains}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Grain Size</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="grainSize"
                      className="form-control"
                      value={formData.grainSize}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Kett Value</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="kettValue"
                      className="form-control"
                      value={formData.kettValue}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Moisture Content</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="moistureContent"
                      className="form-control"
                      value={formData.moistureContent}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Broken Grain</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="brokenGrain"
                      className="form-control"
                      value={formData.brokenGrain}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Admixture</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="admixture"
                      className="form-control"
                      value={formData.admixture}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">DD</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="dd"
                      className="form-control"
                      value={formData.dd}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="form-group row mb-3">
                  <label className="col-4 col-form-label">Any Other</label>
                  <div className="col-6">
                    <input
                      type="text"
                      name="anyOther"
                      className="form-control"
                      value={formData.anyOther}
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>
            </div>
            </div>
            
          </div>
        </div>
        <div className="d-flex justify-content-center">
  <button
    className="btn btn-warning border me-1 text-center"
    style={{ width: "200px" }} // Adjust the width as needed
  >
    Submit
  </button>
</div>
      </form>
    </div>
  );
};

export default AddProduct;
