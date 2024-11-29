import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import DatePicker from 'react-datepicker';
import SellerBuyerService from '../../Services/SellerBuyerService';
import EditUserService from '../../Services/EditUserService';
import Topbar from './Topbar';
import Back from './Back';
import 'react-datepicker/dist/react-datepicker.css';
import DefaultImage from '../../assets/Ellipse 3.png'; // Adjust path as needed


const UserEdit = () => {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [isEditable, setIsEditable] = useState(false); // Track if form is editable
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
    country: 'India',
    state: '',
    city: '',
    pinCode: '',
    address: '',
    incorporationDate: '',
    companyName: '',
    gstIn: '',
    companyLocation: '',
    companyEmail: '',
    registrationNumber: '',
    wareHouseAddress: '',
    activeUser: false,
    seller: false,
    buyer: false,
    userId: '',
    createdAt: '',
    updatedAt: '',
    panNumber: '',
    companyCountry: '',
    companyState: '',
    companyCity: '',
    companyPinCode: '',
    warehouseCountry: '',
    warehouseState: '',
    warehouseCity: '',
    warehousePinCode: ''
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [states, setStates] = useState([]); // State to hold states based on country

  const statesData = {
    India: [
      'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
      'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka', 
      'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram', 
      'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 
      'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal',
    ],
    USA: [], // Empty array for USA, as you want a text input instead of a dropdown
  };

  const countries = ['India', 'USA']; // Example list of countries

  useEffect(() => {
    const fetchUserDetails = async () => {
      try {
        const userData = await SellerBuyerService.findUserById(userId);
        setFormData({
          fullName: userData.fullName || '',
          email: userData.email || '',
          phoneNumber: userData.phoneNumber || '',
          country: userData.country || '',
          state: userData.state || '',
          city: userData.city || '',
          pinCode: userData.pinCode || '',
          address: userData.address || '',
          wareHouseAddress: userData.wareHouseAddress || '',
          incorporationDate: userData.incorporationDate ? new Date(userData.incorporationDate) : '',
          companyName: userData.companyName || '',
          companyEmail: userData.companyEmail || '',
          registrationNumber: userData.registrationNumber || '',
          gstIn: userData.gstIn || '',
          companyLocation: userData.companyLocation || '',
          activeUser: userData.activeUser || false,
          seller: userData.seller || false,
          buyer: userData.buyer || false,
          userId: userData.userId || '',
          createdAt: userData.createdAt || '',
          updatedAt: userData.updatedAt || '',
          panNumber: userData.panNumber || '',
          storageLicense: userData.storageLicense || '',
          profilePictureUrl: userData.profilePictureUrl || '',
          storageLicenseFileUrl: userData.storageLicenseFileUrl || '',
          profilePicture: userData.profilePicture || '',
          storageLicenseFile: userData.storageLicenseFile || '',
          companyCountry: userData.companyCountry || '',
          companyState: userData.companyState || '',
          companyCity: userData.companyCity || '',
          companyPinCode: userData.companyPinCode || '',
          warehouseCountry: userData.warehouseCountry || '',
          warehouseState: userData.warehouseState || '',
          warehouseCity: userData.warehouseCity || '',
          warehousePinCode: userData.warehousePinCode || '',
          cancelledChequeUrl: userData.cancelledChequeUrl || '',
          certificateUrl: userData.certificateUrl || '',
          cancelledCheque: userData.cancelledCheque || '',
          certificate: userData.certificate || '',
          waiveSampleFree: userData.waiveSampleFree || false,
        });
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUserDetails();
  }, [userId]);

  // Effect to update states when the country changes
  useEffect(() => {
    const selectedCountry = formData.country;
    setStates(statesData[selectedCountry] || []); // Set states based on selected country
  }, [formData.country]);

  const handleUserCountryChange = (e) => {
    const selectedCountry = e.target.value;
    setFormData({
      ...formData,
      country: selectedCountry,
      state: '', // Reset user state when country changes
    });
  };

  const handleCompanyCountryChange = (e) => {
    const selectedCountry = e.target.value;
    setFormData({
      ...formData,
      companyCountry: selectedCountry,
      companyState: '', // Reset company state when country changes
    });
  };

  const handleWarehouseCountryChange = (e) => {
    const selectedCountry = e.target.value;
    setFormData({
      ...formData,
      warehouseCountry: selectedCountry,
      warehouseState: '', // Reset warehouse state when country changes
    });
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value,
    });
    setValidationErrors({ ...validationErrors, [name]: '' });
  };

  const handleDateChange = (date) => {
    setFormData({
      ...formData,
      incorporationDate: date,
    });
    setValidationErrors({ ...validationErrors, incorporationDate: '' });
  };

  // Form Validation
  const validate = () => {
    const errors = {};

    if (!formData.fullName) errors.fullName = 'Full name is required';
    if (!formData.email) errors.email = 'Email is required';
    if (!formData.phoneNumber) errors.phoneNumber = 'Phone number is required';

    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form data before submitting
    const errors = validate();
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);  // Set errors to show validation feedback
      return;
    }

    try {
      // Update user data through EditUserService
      await EditUserService.editUser(
        userId,
        formData, // User data
        formData.profilePicture, // Profile picture file
        formData.certificate, // Certificate file
        formData.cancelledCheque // Cancelled cheque file
      );
      
      // Set success message on successful update
      setSuccessMessage('User updated successfully!');
      
      setTimeout(() => {
        setSuccessMessage('');  // Clear success message after 3 seconds
      }, 3000);
    } catch (error) {
      // Handle errors if the update fails
      setError(error.message);
    }
  };

  if (loading) {
    return <div>Loading user details...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="d-flex flex-column">
      {/* Top Bar */}
      <Topbar title="Edit User" userName="Neha Sharma" showSearchBar={false} />
      
      {/* Main Container */}
      <div className="container" style={{ marginTop: '10px' }}>
        {/* Header Buttons */}
        <div className="d-flex justify-content-between align-items-center mb-2">
          <Back />
          {!isEditable && (
            <button
              className="edit-button"
              onClick={() => setIsEditable(true)}
            >
              <i className="fas fa-edit edit-icon"></i>
            </button>
          )}
        </div>
      
          {/* Form */}
      <form onSubmit={handleSubmit}>
        <div className="card">
          <div className="row">
            {/* Profile Section */}
            <div className="col-3 mb-4">
              <div className="profile-card text-center">
                {/* Profile Picture */}
                <div className="profile-pic-container position-relative">
                <img
    src={formData.profilePictureUrl || DefaultImage }  // Use the imported image here
    alt="Profile"
    className="rounded-circle profile-pic"
    style={{ width: '120px', height: '120px' }}
  />
  
                  {/* <button clasqsName="camera-btn position-absolute">
                    <i className="fa fa-camera" aria-hidden="true"></i>
                  </button> */}
                </div>
  
                {/* User Info */}
                <h5 className="mt-3 mb-1">{formData.fullName}</h5> {/* Display fetched name */}
              {/* <span className="text-muted">({userData.seller})</span>  */}
              <p >(
            {formData.seller ? 'Seller' : 'Buyer'})
            <p className="text-muted small ">{formData.email}</p> 
  
          </p>
             
                {/* Role Button */}
                <div className="dropdown">
            <button
              className="btn btn-dark dropdown-toggle"
              type="button"
              id="roleDropdown"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              {formData.seller ? 'Seller' : 'Buyer'}
            </button>
            <ul className="dropdown-menu" aria-labelledby="roleDropdown">
              <li>
                <button className="dropdown-item" onClick={() => setFormData({ ...formData, seller: true, buyer: false })}>
                  Seller
                </button>
              </li>
              <li>
                <button className="dropdown-item" onClick={() => setFormData({ ...formData, seller: false, buyer: true })}>
                  Buyer
                </button>
              </li>
            </ul>
            </div>
            </div>
            </div>
                    {/* User Details Section */}
                    <div className="col-md-9">
            
            <div className="row">
              {/* User Information Fields */}
              <h4>Profile Details</h4>
              <div className="col-md-6">
              
          {/* Full Name */}
          <div className="form-group mb-3" >
            <label htmlFor="fullName">Full Name</label>
            <input
             type="text"
             id="fullName"
             className="form-control"
             name="fullName"
             value={formData.fullName}
             onChange={handleInputChange}
             readOnly
             
            />
            
          </div>
       </div>
          {/* Email */}
          <div className="col-md-6">
          <div className="form-group mb-3">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              className="form-control"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              readOnly
              
            />
           
          </div>
</div>
          {/* Phone Number */}
          <div className="row">
  {/* Phone Number Field */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label htmlFor="phoneNumber">Phone Number</label>
      <input
        type="text"
        id="phoneNumber"
        className="form-control"
        name="phoneNumber"
        value={formData.phoneNumber}
        onChange={handleInputChange}
        disabled={!isEditable}
        required
      />
      
    </div>
  </div>

  {/* Address Field */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label htmlFor="address">Address</label>
      <input
        type="text"
        id="address"
        className="form-control"
        placeholder="Enter Address"
        name="address"
        value={formData.address}
        onChange={handleInputChange}
        readOnly={!isEditable}
        
      />
    </div>
  </div>
</div>

          {/* Country */}
          <div className="row">
  {/* Address Field */}
 

  {/* Country Field */}
  <div className="col-3">
  <div className="form-group mb-3">
    <label htmlFor="country">Country</label>
    <select
      className="form-control"
      id="country"
      name="country"
      value={formData.country}
      onChange={handleUserCountryChange}
      disabled={!isEditable}
     
    >
      {countries.map((country) => (
        <option key={country} value={country}>
          {country}
        </option>
      ))}
    </select>
  </div>
</div>
<div className="col-3">
  <div className="form-group mb-3">
    <label htmlFor="state">State</label>
    {formData.country === 'India' ? (
      <select
        className="form-control"
        id="state"
        name="state"
        value={formData.state}
        onChange={handleInputChange}
        disabled={!isEditable}
       
      >
        <option value="">Select State</option>
        {statesData['India'].map((state) => (
          <option key={state} value={state}>
            {state}
          </option>
        ))}
      </select>
    ) : (
      <input
        type="text"
        id="state"
        className="form-control"
        placeholder="Enter State"
        name="state"
        value={formData.state}
        onChange={handleInputChange}
        disabled={!isEditable}
        required
      />
    )}
  </div>
</div>


  {/* City Field */}
  <div className="col-3">
    <div className="form-group mb-3">
      <label>City</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter City"
        name="city"
        value={formData.city}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
      
    </div>
  </div>

  {/* Pin Code Field */}
  <div className="col-3">
    <div className="form-group mb-3">
      <label>Pin Code</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter Pin Code"
        name="pinCode"
        value={formData.pinCode}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
      
    </div>
  </div>
</div>

       
         

<h4 className='mb-4'>Company Details</h4>



<div className="row">
  {/* Incorporation Date */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label className="label-top">Incorporation Date</label><br></br>
      <DatePicker
        selected={formData.incorporationDate}
        onChange={handleDateChange}
        className="form-control input-field"
        placeholderText="Select Incorporation Date"
        readOnly={!isEditable}
      />
     
    </div>
  </div>
  {/* Company Name */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label>Company Name</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter Company Name"
        name="companyName"
        value={formData.companyName}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
    
    </div>
  </div>

  {/* GSTIN */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label>GSTIN</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter GSTIN"
        name="gstIn"
        value={formData.gstIn}
        onChange={handleInputChange}
        readOnly={!isEditable}
        required
      />
      
    </div>
  </div>

  {/* PAN No */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label>PAN No</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter PAN No"
        name="panNumber"
        value={formData.panNumber}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
     
    </div>
  </div>

  {/* Company Location */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label>Company Location</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter Company Location"
        name="companyLocation"
        value={formData.companyLocation}
        onChange={handleInputChange}
        readOnly={!isEditable}
        required
      />
    </div>
  </div>

  {/* Country */}
  <div className="col-md-6">
  <div className="form-group mb-3">
    <label htmlFor="country">Country</label>
    <select
      className="form-control"
      id="country"
      name="companyCountry"
      value={formData.companyCountry}
      onChange={handleCompanyCountryChange}
      disabled={!isEditable}
      required
    >
      {countries.map((country) => (
        <option key={country} value={country}>
          {country}
        </option>

      ))}
    </select>
  </div>
</div>
<div className="col-md-6">
  <div className="form-group mb-3">
    <label htmlFor="state">State</label>
    {formData.companyCountry === 'India' ? (
      <select
        className="form-control"
        id="state"
        name="companyState"
        value={formData.companyState}
        onChange={handleInputChange}
        disabled={!isEditable}
        required
      >
        <option value="">Select State</option>
        {statesData['India'].map((state) => (
          <option key={state} value={state}>
            {state}
          </option>
        ))}
      </select>
    ) : (
      <input
        type="text"
        className="form-control"
        id="state"
        name="companyState"
        placeholder="Enter State"
        value={formData.companyState}
        onChange={handleInputChange}
        disabled={!isEditable}
        required
      />
    )}
  </div>
</div>

  {/* City */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label>City</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter City"
        name="city"
        value={formData.city}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
     
    </div>
  </div>

  {/* Pin Code */}
  <div className="col-md-6">
    <div className="form-group mb-3">
      <label>Pin Code</label>
      <input
        type="text"
        className="form-control"
        placeholder="Enter Pin Code"
        name="pinCode"
        value={formData.pinCode}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
     
    </div>
  </div>

  {/* Warehouse Address */}
  <div className="col-md-6">
  <div className="form-group mb-3">
    <label>Warehouse Address</label>
    <input
      type="text"
      className="form-control"
      placeholder="Enter Warehouse Address"
      name="wareHouseAddress"
      value={formData.wareHouseAddress}
      onChange={handleInputChange}
      readOnly={!isEditable}
    />
  </div>
</div>

{/* Warehouse Country */}
<div className="col-md-6">
  <div className="form-group mb-3">
    <label htmlFor="country">Country</label>
    <select
      className="form-control"
      id="country"
      name="warehouseCountry"
      value={formData.warehouseCountry}
      onChange={handleWarehouseCountryChange}
      disabled={!isEditable}
     
    >
      <option value="">Select Country</option> {/* Add this option to handle unselected state */}
      {countries.map((country) => (
        <option key={country} value={country}>
          {country}
        </option>
      ))}
    </select>
  </div>
</div>

{/* Warehouse State */}
<div className="col-md-6">
  <div className="form-group mb-3">
    <label htmlFor="state">State</label>
    {formData.warehouseCountry === 'India' ? (
      // Show dropdown for "India"
      <select
        className="form-control"
        id="state"
        name="warehouseState"
        value={formData.warehouseState}
        onChange={handleInputChange}
        disabled={!isEditable}
        required // State is required if the country is India
      >
        <option value="">Select State</option>
        {statesData['India'].map((state) => (
          <option key={state} value={state}>
            {state}
          </option>
        ))}
      </select>
    ) : (
      // Text input for countries other than "India"
      <input
        type="text"
        className="form-control"
        id="state"
        name="warehouseState"
        placeholder="Enter State"
        value={formData.warehouseState}
        onChange={handleInputChange}
        disabled={!isEditable}
      />
    )}
  </div>
</div>


{/* Warehouse City */}
<div className="col-md-6">
  <div className="form-group mb-3">
    <label>Warehouse City</label>
    <input
      type="text"
      className="form-control"
      placeholder="Enter City"
      name="warehouseCity"
      value={formData.warehouseCity}
      onChange={handleInputChange}
      readOnly={!isEditable}
    />
   
  </div>
</div>

{/* Warehouse Pin Code */}
<div className="col-md-6">
  <div className="form-group mb-3">
    <label>Warehouse Pin Code</label>
    <input
      type="text"
      className="form-control"
      placeholder="Enter Pin Code"
      name="warehousePinCode"
      value={formData.warehousePinCode}
      onChange={handleInputChange}
      readOnly={!isEditable}
    />
    
  </div>
</div>
 {/* Upload Certificate */}

 <div className="col-md-6">
            <div className="form-group mb-3">
              <label htmlFor="profilePicture">Upload Profile Picture</label>
              <input
                type="file"
                id="profilePicture"
                className="form-control"
                name="profilePicture"
                onChange={(e) => setFormData({ ...formData, profilePicture: e.target.files[0] })}
                disabled={!isEditable}
              />
              {formData.profilePictureUrl && (
                <div className="mt-2">
                  <a href={formData.profilePictureUrl} target="_blank" rel="noopener noreferrer">
                    View Uploaded Profile Picture
                  </a>
                </div>
              )}
            </div>
          </div>

          {/* Upload Certificate */}
          <div className="col-md-6">
            <div className="form-group mb-3">
              <label htmlFor="certificate">Scope Certificate</label>
              <input
                type="file"
                id="certificate"
                className="form-control"
                name="certificate"
                onChange={(e) => setFormData({ ...formData, certificate: e.target.files[0] })}
                disabled={!isEditable}
              />
              {formData.certificateUrl && (
                <div className="mt-2">
                  <a href={formData.certificateUrl} target="_blank" rel="noopener noreferrer">
                    View Scope Certificate
                  </a>
                </div>
              )}
            </div>
          </div>

          {/* Upload Cancelled Cheque */}
          <div className="col-md-6">
            <div className="form-group mb-3">
              <label htmlFor="cancelledCheque">Upload Cancelled Cheque</label>
              <input
                type="file"
                id="cancelledCheque"
                className="form-control"
                name="cancelledCheque"
                onChange={(e) => setFormData({ ...formData, cancelledCheque: e.target.files[0] })}
                disabled={!isEditable}
              />
              {formData.cancelledChequeUrl && (
                <div className="mt-2">
                  <a href={formData.cancelledChequeUrl} target="_blank" rel="noopener noreferrer">
                    View Uploaded Cancelled Cheque
                  </a>
                </div>
              )}
            </div>
          </div>
          {successMessage && (
          <div className="alert alert-success mt-3" role="alert">
            {successMessage}
          </div>
        )}
 
          
</div>
</div>

        {/* Submit Button */}
        {/* Submit Button */}
        {isEditable && (
                    <div className="d-flex justify-content-end mt-4">
                      <button type="submit" className="btn btn-warning">
                        Update User
                      </button>
                      
                    </div>
                  )}

        {/* Success Message */}
        
      </div>
      </div>
      </div>
    </form>
        
     
       
        
        {/* <UserAsBuyer /> */}
      </div>
    </div>
  );
};

export default UserEdit;
