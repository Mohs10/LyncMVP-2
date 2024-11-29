import React, { useState, useEffect } from 'react';
import { addProduct } from '../../Services/AddProductService';
import { useParams, useNavigate } from 'react-router-dom';
import { getAllCategories } from '../../Services/CategoryService';
import { addForm, getForms } from '../../Services/AddFormService';
import { addVariety, getVarieties } from '../../Services/AddVarietyService'; // Ensure correct import paths
import { findProductById } from '../../Services/ProductService';
import Topbar from './Topbar';
import AddVariety from './AddVariety';
import AddCategory from './AddCategory';
import AddForm from './AddForm';
import {editProductById} from '../../Services/EditProductService'
import defaultImage from '../../assets/Product2.png';


const CertificationList = ({ certifications, customCertifications, handleCheckboxChange, handleAddCustomCertification, anyOther, setAnyOther }) => (
  
  <div className="col-4">
    <div className="certification border-section"style={{ width: '250px' }}    >

    {console.log('certifications js:', certifications)}
    {console.log('customCertifications js:', customCertifications)}

    {/* {console.log('certifications:', certifications)} */}


    {Object.entries(certifications).map(([name, checked]) => (

      
        <div className="form-check" key={name}>
          <input
            className="form-check-input"
            type="checkbox"
            id={name}
            name={name}
            checked={checked}
            onChange={(e) => handleCheckboxChange(e, 'certifications')}
          />
          <label className="form-check-label" htmlFor={name}>
            {name.toUpperCase()}
          </label>
        </div>
      ))}
      {customCertifications.map((cert, index) => (
        <div className="form-check" key={index}>
          <input
            className="form-check-input"
            type="checkbox"
            id={`custom-${index}`}
            checked={cert.isCertified}
            onChange={() => handleCheckboxChange(index, 'customCertifications')}
          />
          <label className="form-check-label" htmlFor={`custom-${index}`}>
            {cert.certificationName}
          </label>
        </div>
      ))}
      <div className="row mb-4">
        <div className="col-8">
          <input
            type="text"
            className="form-control"
            placeholder="Any Others"
            value={anyOther}
            onChange={(e) => setAnyOther(e.target.value)}
          />
        </div>
        <div className="col-4 d-flex align-items-end">
          <button type="button" className="btn btn-dark" onClick={handleAddCustomCertification}>
            Add
          </button>
        </div>
      </div>
    </div>
  </div>
);

const SpecificationList = ({ customSpecifications, newSpecification, setNewSpecification, handleAddSpecification, handleSpecificationChange }) => (
  <div>
    
    {customSpecifications.map((spec, index) => (
      <div className="form-group row mb-3" key={index}>
        <label className="col-4 col-form-label">{spec.specificationName}</label>
        <div className="col-6">
          <input
            type="text"
            className="form-control"
            placeholder="Enter value"
            value={spec.specificationValue}
            onChange={(e) => handleSpecificationChange(index, e.target.value)}
          />
        </div>
      </div>
    ))}

<div className="form-group row mb-3">
      {/* <label className="col-12 col-form-label">Add New Specification</label> */}
      <div className="col-4">
        <input
          type="text"
          className="form-control"
          placeholder="Any Other"
          value={newSpecification}
          onChange={(e) => setNewSpecification(e.target.value)}
        />
      </div>
      <div className="col-2 d-flex align-items-end">
        <button type="button" className="btn btn-dark" onClick={handleAddSpecification}>
          Add
        </button>
      </div>
    </div>
  </div>
);



const EditProduct = () => {
  const { productId } = useParams();

  const [productImage, setProductImage] = useState(new FormData());
  const [productViewImage, setProductViewImage] = useState();
  const [productImageAWS, setProductImageAWS] = useState();

  const [formData, setFormData] = useState({
    productDescription: '',
    productVariety: '',
    
    categoryId:'',
    productForm: '',
    varietyIds:[],
    formIds: [],
    specifications: [],
    productImageUrl:'',
    hsnCode: '',
    productName: '',
    certifications: [] ,
  });
  const [category ,setCategory] = useState();
  const [varieties, setVarieties] = useState([]);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const productData = await findProductById(productId);

        setProductImageAWS(productData.productImageUrl);

        console.log('productData:', productData);
        console.log('productImage js:', productImageAWS);


        if (!productData) throw new Error('Product not found.');
  
        // Set formData with fetched product data
        setFormData((prevData) => ({
          ...prevData,
          productName: productData.productName || '',
          hsnCode: productData.hsnCode || '',
          categoryId: productData.categoryId || '',
          productDescription: productData.productDescription || '',
          productImageUrl:productData.productImageUrl||'',
          varietyIds: productData.varietyIds.map(String) || [],
          formIds: productData.formIds.map(String) || [],
          certifications: productData.certifications.reduce((acc, cert) => {
            acc[cert.certificationName.toLowerCase()] = cert.isCertified || false;
            return acc;
          }, {}),
          specifications: productData.specifications || []
        }));
       setCategory(productData.categoryId ||0);
        // Additional state updates if needed
        setAddedVarieties(productData.varietys || []);
        setAddedForms(productData.forms || []);
        setCustomSpecifications(productData.specifications || []);
        setCustomCertifications(
          productData.certifications.map((cert) => ({
            certificationName: cert.certificationName,
            isCertified: cert.isCertified || false
          }))
        );

      } catch (error) {
        console.error('Error fetching data:', error);
        setErrorMessage(`Error fetching data: ${error.message || error}`);
      } finally {
        setLoading(false);
      }
    };
  
    fetchInitialData();
  }, [productId]);
  
  
  
  
  // console.log('Fetched Product:', fetchedProduct);

  const [addedVarieties, setAddedVarieties] = useState([]);
  const [addedForms, setAddedForms] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [customCertifications, setCustomCertifications] = useState([]);
  const [newSpecification, setNewSpecification] = useState('');
  const [customSpecifications, setCustomSpecifications] = useState([]);
  const [anyOther, setAnyOther] = useState('');
  const [certifications, setCertifications] = useState([
]);
  const handleImageUpload = (event) => {
    const file = event.target.files[0]; // Get the first file from the input
    if (file) {
      setProductImage(file); // Store the image file in state
      setProductViewImage(URL.createObjectURL(file));
      
  
    }
  };

  
  
  const handleCheckboxChange = (event, type, certificationName) => {
    const { checked } = event.target;
  
    console.log('Checkbox changed:');
    console.log('Certification Name:', certificationName);
    console.log('Checked:', checked);
    console.log('Type:', type);
  
    if (type === 'certifications') {
      // Update predefined certifications
      setCertifications(prevState =>
        prevState.map(cert =>
          cert.certificationName === certificationName
            ? { ...cert, isCertified: checked }
            : cert
        )
      );
    } else if (type === 'customCertifications') {
      // Update custom certifications
      setCustomCertifications(prevState =>
        prevState.map(cert =>
          cert.certificationName === certificationName
            ? { ...cert, isCertified: checked }
            : cert
        )
      );
    }
  };

  const handleAddCustomCertification = () => {
    if (anyOther.trim()) {
      console.log('Adding custom certification with name:', anyOther);
      setCustomCertifications(prev => [
        ...prev,
        { certificationName: anyOther, isCertified: false }
      ]);
    } else {
      console.log('No certification name entered');
    }
  };




// Use `getUniqueCertifications` wherever you render certifications


  const handleAddSpecification = () => {
    if (newSpecification.trim()) {
      setCustomSpecifications((prev) => [...prev, { specificationName: newSpecification, specificationValue: '', specificationValueUnits: '' }]);
      setNewSpecification('');
    }
  };

  const handleSpecificationChange = (index, value) => {
    setCustomSpecifications((prev) =>
      prev.map((spec, idx) => (idx === index ? { ...spec, specificationValue: value } : spec))
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Convert certifications object to array of objects with certificationName and isCertified properties
    const certificationsArray = [
      ...customCertifications, // Only include the new custom certifications
    ];
    
    console.log('customCertifications jx:', certificationsArray);

    try {
      const productData = {
        ...formData,
        certifications: certificationsArray,
        specifications: customSpecifications,
        varietyIds: addedVarieties,
        formIds: addedForms,
        categoryId: category,
        productImage, // Adding productImage here

      };
      console.log('formData jx:', formData);
      // console.log('productImage:', productData.productImageUrl);
      console.log('Certifications 2 jx:', productData.certifications);

      console.log('productData jx:', productData);
      await editProductById(productId, productData, productImage);
      alert('Product Updated successfully!');
    } catch (error) {
      console.error('Error adding product:', error);
      alert('Failed to add product.');
    }
  };

  return (
    <div className="d-flex flex-column">
      <Topbar title="Edit Product" userName="Neha Sharma" showSearchBar={false} />
      <form className="container-fluid mt-4" onSubmit={handleSubmit}>
        <div className="row">
          <div className="col-md-6">
          <div className="product-image-upload">
              {productViewImage ? (
                <img
                  src={productViewImage}
                  alt="Uploaded Product"
                  className="uploaded-image"
                  style={{ width: '450px', height: '278px' }}
                />
              ) : productImageAWS ? (
                <img
                  src={productImageAWS}
                  alt="Product"
                  className="uploaded-image"
                  style={{ width: '450px', height: '278px' }}
                />
              ) : (
                <img
                  src={productImageAWS}
                  alt="Default Product"
                  className="default-image"
                  style={{ width: '450px', height: '278px', objectFit: 'cover' }}
                />
              )}
              <input
                type="file"
                onChange={handleImageUpload}
                className="form-control mt-2"
              />
    </div>
            <div className="card p-4 mb-4 mt-4">
              <h5 className="text-center mb-4 heading-with-bar">Product Specifications</h5>
              <div className="card-body">

              <label htmlFor="productDescription" className="form-group label"> Product Description</label>
                <textarea
                  name="productDescription"
                  className="form-control"
                  rows="4"
                  placeholder="Enter product description..."
                  value={formData.productDescription}
                  onChange={(e) => setFormData({ ...formData, productDescription: e.target.value })}
                />
                <AddVariety addedVarieties={addedVarieties} setAddedVarieties={setAddedVarieties} />
                <AddForm addedForms={addedForms} setAddedForms={setAddedForms} />
                <AddCategory selectedCategory={category} setSelectedCategory={setCategory} />

                <h5 className="text-center mb-4 ">Product Certifications</h5>

                <div>
      {/* Log data before passing it to CertificationList */}
      {console.log('Parent Component State:', {
        certifications: [], // Empty array in this case
        customCertifications,
        anyOther,
      })}

      <CertificationList
        certifications={[]}
        customCertifications={customCertifications}
        handleCheckboxChange={handleCheckboxChange}
        handleAddCustomCertification={handleAddCustomCertification}
        anyOther={anyOther}
        setAnyOther={setAnyOther}
      />
    </div>
              </div>
            </div>
          </div>
          <div className="col-md-6">
            <div className="card p-4 mb-4">
              <div className="form-group row mb-3">
                <label className="col-12 col-form-label">HSN Code</label>
                <input
                  type="text"
                  name="hsnCode"
                  className="form-control"
                  placeholder="HSN Code"
                  value={formData.hsnCode}
                  onChange={(e) => setFormData({ ...formData, hsnCode: e.target.value })}
                />
              </div>
              <div className="form-group row mb-3">
                <label className="col-12 col-form-label">Product Name</label>
                <input
                  type="text"
                  name="productName"
                  className="form-control"
                  placeholder="Product Name"
                  value={formData.productName}
                  onChange={(e) => setFormData({ ...formData, productName: e.target.value })}
                />
              </div>
            </div>
            <div className="card p-4 mb-4">
              <h5 className="text-center mb-4 heading-with-bar">Specifications</h5>
              <SpecificationList
                customSpecifications={customSpecifications}
                newSpecification={newSpecification}
                setNewSpecification={setNewSpecification}
                handleAddSpecification={handleAddSpecification}
                handleSpecificationChange={handleSpecificationChange}
              />
            </div>
            {/* <button type="submit" className="btn btn-primary btn-lg btn-block mt-4">Submit Product</button> */}
          </div>
        </div>

        <div className="d-flex justify-content-center">
          <button
          type="submit"
            className="btn btn-warning border me-1 text-center"
            style={{ width: "200px" }}
          >
            Submit Product
          </button>
        </div>
      </form>
    </div>
  );
};

export default EditProduct;
