import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import defaultImage from '../../assets/Product2.png';
import { findProductById } from '../../Services/ProductService';
import { getVarieties, getForms } from '../../Services/VarietyFormService';
import { getCategories, getCategoryById } from '../../Services/AddCategoryService'; // Correct named imports
import AddCategory from './AddCategory';

import Topbar from './Topbar';

const ViewProduct = () => {
  const { productId } = useParams();
  const [productData, setProductData] = useState(null);
  const [varieties, setVarieties] = useState([]);
  const [forms, setForms] = useState([]);
  const [category, setCategory] = useState(null); // State to store category data
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');



  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch product details
        const product = await findProductById(productId);
        if (!product) throw new Error('Product not found');
        console.log('Product jx:', product);
        // Fetch varieties and forms
        const [allVarieties, allForms] = await Promise.all([getVarieties(), getForms()]);

        // Fetch category by categoryId
        const fetchedCategory = await getCategories(product.categoryId); // Fetch category using the categoryId from the product
        if (!fetchedCategory) throw new Error('Category not found');

        // Map variety and form names based on IDs in product data
        const productVarieties = product.varietys?.map(varietyId => {
          return allVarieties.find(variety => variety.id === varietyId)?.name || 'Unknown Variety';
        });

        

        const productForms = product.forms?.map(formId => {
          return allForms.find(form => form.id === formId)?.name || 'Unknown Form';
        });
        setCategory(product.categoryId ||0);

        setProductData({
          ...product,
          varietyNames: productVarieties,
          formNames: productForms,
        });

        setVarieties(allVarieties);
        setForms(allForms);

      } catch (error) {
        setErrorMessage(error.message || 'Error fetching product details.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [productId]);

  if (loading) return <p>Loading product details...</p>;
  if (errorMessage) return <p className="text-danger">{errorMessage}</p>;

  return (
    <div className="d-flex flex-column">
      <Topbar title="View Product" userName="Neha Sharma" showSearchBar={false} />
      <div className="container-fluid mt-4">
        <div className="row">
          {/* Product Image Section */}
          <div className="col-md-6">
            <div className="product-image-upload">
              <img
                src={productData.productImageUrl || defaultImage}
                alt={productData.productName}
                className="uploaded-image"
                style={{ width: '450px', height: '300px', objectFit: 'cover' }}
              />
            </div>
            <div className="card p-4 mb-4 mt-4">
            <h5 className="text-center mb-4 heading-with-bar">Product Specifications</h5>
              <div className="card-body ">
              <label htmlFor="productDescription" className="form-group label">Product Description</label>
              <textarea
  id="productDescription"
  className="form-control"
  rows="4"
  value={productData.productDescription || 'N/A'}
  readOnly
  style={{ height: '100px' }}
/>


                <div>
                  
                <div className="form-group mb-4">
                <label
      className="form-group label mt-2"
      style={{ display: 'block', paddingTop: '2px' }} // Moves the label below the badge
    >
      Product Variety
    </label>
    <ul
      
    >
      {productData.varietys?.length ? (
        productData.varietys.map((variety) => (
          <div key={variety.varietyId}>{variety.varietyName}</div>
        ))
      ) : (
        <p>No varieties available.</p>
      )}
    </ul>
    
  </div>

  {/* Product Form */}
  <div className="form-group ">
  <label
      className="form-group label mt-2"
      style={{ display: 'block', paddingTop: '2px' }} // Moves the label below the badge
    >
      Product Form
    </label>
    <ul
     
    >
      {productData.forms?.length ? (
        productData.forms.map((form) => (
          <div key={form.formId}>{form.formName}</div>
        ))
      ) : (
        <p>No forms available.</p>
      )}
    </ul>
   
  </div>
                 
                
                  <AddCategory selectedCategory={category} setSelectedCategory={setCategory} />
                  </div>
                <h5 className="my-4">Certifications</h5>
                <div className="certifications-section">
                  <ul className="certifications-list">
                    {productData.certifications?.map((cert) => (
                      <li
                        key={cert.certificationName}
                        className={`certification-item ${cert.isCertified ? 'certified' : 'not-certified'}`}
                      >
                        <input
                          type="checkbox"
                          checked={cert.isCertified}
                          readOnly
                        />
                        <span>{cert.certificationName}</span>
                      </li>
                    )) || <p>No certifications available.</p>}
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* General Information Section */}
          <div className="col-md-6">
            <div className="card p-4 mb-4 mt-4">
              <h5 className="text">General Information</h5>
              <div className="card-body mb-4">
                <div className="form-group row mb-4">
                  <label htmlFor="hsnCode" className="col-12 col-form-label">
                    HSN Code
                  </label>
                  <input
                    type="text"
                    id="hsnCode"
                    className="form-control"
                    value={productData.hsnCode || 'N/A'}
                    readOnly
                  />
                </div>
                <div className="form-group row mb-4">
                  <label htmlFor="productName" className="col-12 col-form-label">
                    Product Name
                  </label>
                  <input
                    type="text"
                    id="productName"
                    className="form-control"
                    value={productData.productName || 'N/A'}
                    readOnly
                  />
                </div>
                
              </div>
            </div>

            {/* Specifications Section */}
            <div className="card p-4 mb-4">
              <h5 className="text-center mb-4 heading-with-bar">Specifications</h5>
              {productData.specifications?.length > 0 ? (
                productData.specifications.map((spec, index) => (
                  <div className="form-group row mb-3" key={index}>
                    <label className="col-4 col-form-label">{spec.specificationName}</label>
                    <div className="col-8">
                      <input
                        type="text"
                        className="form-control"
                        value={`${spec.specificationValue || 'N/A'} ${spec.specificationValueUnits || ''}`}
                        readOnly
                      />
                    </div>
                  </div>
                ))
              ) : (
                <p>No specifications available.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ViewProduct;
