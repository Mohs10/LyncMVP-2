import React, { useState } from 'react';
import { addProduct } from '../../Services/AddProductService';
import Topbar from './Topbar';
import AddVariety from './AddVariety';
import AddCategory from './AddCategory';
import AddForm from './AddForm';
import defaultImage from '../../assets/Product2.png';

const CertificationList = ({certifications,customCertifications,handleCheckboxChange,handleAddCustomCertification,anyOther,setAnyOther}) => (
  <div className="col-4">

    <div className="certification border-section"             style={{ width: '250px' }}    >
      {/* Loop through predefined certifications */}
      {certifications.length > 0 && certifications.map((certification) => (
        <div className="form-check" key={certification.certificationName}>
          <input
            className="form-check-input"
            type="checkbox"
            id={certification.certificationName}
            name={certification.certificationName}
            checked={certification.isCertified}
            onChange={(e) => handleCheckboxChange(e, 'certifications', certification.certificationName)}
          />
          <label className="form-check-label" htmlFor={certification.certificationName}>
            {certification.certificationName.toUpperCase()}
          </label>
        </div>
      ))}

      {/* Loop through custom certifications */}
      {customCertifications.length > 0 && customCertifications.map((cert, index) => (
        <div className="form-check" key={index}>
          <input
            className="form-check-input"
            type="checkbox"
            id={`custom-${index}`}
            checked={cert.isCertified}
            onChange={(e) => handleCheckboxChange(e, 'customCertifications', cert.certificationName)}
          />
          <label className="form-check-label" htmlFor={`custom-${index}`}>
            {cert.certificationName}
          </label>
        </div>
      ))}

      {/* Input field for adding custom certifications */}
      <div className="row mb-4">
        <div className="col-8">
          <input
            type="text"
            className="form-control"
            placeholder="Any Other"
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

const AddProduct = () => {
  const [productImage, setProductImage] = useState(new FormData());
  const [productViewImage, setProductViewImage] = useState();

  const [formData, setFormData] = useState({
    productDescription: '',
    productVariety: '',
    productForm: '',
    hsnCode: '',
    productName: '',
    certifications: []  // Empty array to hold the combined certifications
  });
  
  const [addedVarieties, setAddedVarieties] = useState([]);
  const [addedForms, setAddedForms] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [certifications, setCertifications] = useState([
    // Predefined certifications, which might be like this initially
    { certificationName: 'NPOP', isCertified: false },
    { certificationName: 'NOP', isCertified: false },
    { certificationName: 'EU', isCertified: false },
    { certificationName: 'GSDC', isCertified: false },
  ]);
  const [customCertifications, setCustomCertifications] = useState([]);
  const [newSpecification, setNewSpecification] = useState('');
  const [customSpecifications, setCustomSpecifications] = useState([]);
  const [anyOther, setAnyOther] = useState('');

//   const handleImageUpload = (e) => {
//     const file = e.target.files[0]; // Get the uploaded file
//     if (file) {
//         // Create a preview URL for the image
//         setProductImage(URL.createObjectURL(file));
        
//         // Do something with the file (e.g., send it to a server or save it)
//         console.log(file); // Log the file for inspection
//     }
// };



const handleImageUpload = (event) => {
  const file = event.target.files[0]; // Get the first file from the input
  if (file) {
    setProductImage(file); // Store the image file in state
    setProductViewImage(URL.createObjectURL(file));

  }
};

// const handleImageUpload = async (e) => {
//   const file = e.target.files[0]; // Get the uploaded file
//   if (!file) return; // Exit if no file is selected

//   // Validate file type (only allow images)
//   const validImageTypes = ['image/jpeg', 'image/png', 'image/jpg'];
//   if (!validImageTypes.includes(file.type)) {
//     console.error('Invalid file type. Please upload a JPEG or PNG image.');
//     return;
//   }

//   // Validate file size (e.g., limit to 5MB)
//   const maxSizeInBytes = 5 * 1024 * 1024; // 5MB
//   if (file.size > maxSizeInBytes) {
//     console.error('File size exceeds the 5MB limit. Please upload a smaller image.');
//     return;
//   }

//   try {
//     // Optionally compress the image
//     const compressedFile = await compressImage(file, 'compressed-image', 800, 800, 0.8);

//     // Create a preview URL for the compressed image
//     const previewUrl = URL.createObjectURL(compressedFile);
//     setProductImage(previewUrl);

//     // Do something with the compressed file (e.g., send to a server)
//     console.log('Original File:', file);
//     console.log('Compressed File:', compressedFile);
//     console.log('Preview URL:', previewUrl);

//     // Optionally revoke the object URL when done
//     // URL.revokeObjectURL(previewUrl);
//   } catch (error) {
//     console.error('Error processing the image:', error);
//   }
// };





  
  

 // Handle checkbox change (for both predefined and custom certifications)
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

// Handle adding custom certifications
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



// Combine predefined and custom certifications into formData
const updateFormData = () => {
  const combinedCertifications = [
    ...certifications, // Predefined certifications
    ...customCertifications, // Custom certifications
  ];

  setFormData(prevState => ({
    ...prevState,
    certifications: combinedCertifications
  }));

  console.log('Updated formData with certifications:', combinedCertifications);
};
  

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
  
    // Combine predefined certifications and custom certifications into one array
    const certificationsArray = [
      ...certifications,  // Predefined certifications
      ...customCertifications,     // Custom certifications
    ];
  
    try {
      // Create the product data object with the necessary fields
      const productData = {
        ...formData,
        certifications: certificationsArray,  // Include combined certifications
        specifications: customSpecifications, // Include custom specifications
        varietyIds: addedVarieties,           // Include selected varieties
        formIds: addedForms,                  // Include selected forms
        categoryId: selectedCategory,         // Include selected category
        productImage,                          // Include product image
      };
  
      // Call the addProduct function to submit the data
      await addProduct(productData, productImage);  // Pass productImage here as well
  
      // Show success message
      alert('Product added successfully!');
    } catch (error) {
      // Handle any error that occurs during submission
      console.error('Error adding product:', error);
      alert('Failed to add product.');
    }
  };
  

  return (
    <div className="d-flex flex-column">
      <Topbar title="Add Product" userName="Neha Sharma" showSearchBar={false} />
      <form className="container-fluid mt-4" onSubmit={handleSubmit}>
        <div className="row">
          <div className="col-md-6">
            <div className="product-image-upload">
              {productImage ? (
                <img src={productViewImage} alt="Product" className="uploaded-image" style={{ width: '400px', height: '278px' }} />
              ) : (
                <img src={defaultImage} alt="Default Product" className="default-image" style={{ width: '450px', height: '278px', objectFit: 'cover' }} />
              )}
              <input type="file" onChange={handleImageUpload} className="form-control mt-2" />
            </div>
            <div className="card p-4 mb-4 mt-4">
              <h5 className="text-center mb-4 heading-with-bar">Product Specifications</h5>
              <div className="card-body">
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
                <AddCategory selectedCategory={selectedCategory} setSelectedCategory={setSelectedCategory} />
                <h5 className="text-center mb-4 ">Product Certifications</h5>

                <CertificationList
                  certifications={certifications}
                  customCertifications={customCertifications}
                  handleCheckboxChange={handleCheckboxChange}
                  handleAddCustomCertification={handleAddCustomCertification}
                  anyOther={anyOther}
                  setAnyOther={setAnyOther}
                />
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
              <h5 className="text-center mb-4 heading-with-bar">Additional Specifications</h5>
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
            Add Product
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddProduct;
