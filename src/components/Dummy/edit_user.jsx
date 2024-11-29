
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import DatePicker from 'react-datepicker';
import SellerBuyerService from '../../Services/SellerBuyerService';
import EditUserService from '../../Services/EditUserService';
import Topbar from './Topbar';
import Back from './Back';
import 'react-datepicker/dist/react-datepicker.css';

import UserAsBuyer from '../Query/UserAsBuyer'; // Adjust the path if necessary

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
    storageLicense: '',
    profilePictureUrl: '',
    storageLicenseFileUrl: '',
    profilePicture: '',
    storageLicenseFile: '',
    companyCountry: '',
    companyState: '',
    companyCity: '',
    companyPinCode: '',
    warehouseCountry: '',
    warehouseState: '',
    warehouseCity: '',
    warehousePinCode: '',
    cancelledChequeUrl: '',
    certificateUrl: '',
    cancelledCheque: '',
    certificate: '',
    waiveSampleFree: false,
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
  
  const handleCountryChange = (e) => {
    const selectedCountry = e.target.value;
    setFormData({
      ...formData,
      country: selectedCountry,
      state: '', // Reset state when country changes
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

  const validate = () => {
    const errors = {};
    const { fullName, email, phoneNumber, country, state, city, pinCode, companyEmail, registrationNumber, gstIn } = formData;

    if (!/^[a-zA-Z\s]+$/.test(fullName)) {
      errors.fullName = 'Full Name must contain only characters.';
    }
    if (!/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/.test(email)) {
      errors.email = 'Email is not valid.';
    }
    if (!/^\d{10}$/.test(phoneNumber)) {
      errors.phoneNumber = 'Phone Number must be 10 digits.';
    }
    if (!/^[a-zA-Z\s]+$/.test(country)) {
      errors.country = 'Country must contain only characters.';
    }
    if (!/^[a-zA-Z\s]+$/.test(state)) {
      errors.state = 'State must contain only characters.';
    }
    if (!/^[a-zA-Z\s]+$/.test(city)) {
      errors.city = 'City must contain only characters.';
    }
    if (!/^\d+$/.test(pinCode)) {
      errors.pinCode = 'Pin Code must contain only numbers.';
    }
    if (companyEmail && !/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/.test(companyEmail)) {
      errors.companyEmail = 'Company Email is not valid.';
    }
    if (registrationNumber && !/^[a-zA-Z0-9]{21}$/.test(registrationNumber)) {
      errors.registrationNumber = 'Registration Number must be exactly 21 alphanumeric characters.';
    }
    if (gstIn && !/^[a-zA-Z0-9]{15}$/.test(gstIn)) {
      errors.gstIn = 'GSTIN must be exactly 15 alphanumeric characters.';
    }

    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errors = validate();
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors);
      return;
    }
    const enableEditing = () => {
      setIsEditable(true);
      setSuccessMessage('You can edit now!'); // Set success message
      setTimeout(() => {
        setSuccessMessage(''); // Clear the success message after 3 seconds
      }, 3000);
    };
    try {
      await EditUserService.editUser(userId, formData);
      setSuccessMessage('User updated successfully!');
    } catch (error) {
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
    src={formData.profilePictureUrl}  // Use the imported image here
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
              disabled={!isEditable}
              required
            />
            {validationErrors.fullName && <div className="text-danger">{validationErrors.fullName}</div>}
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
              disabled={!isEditable}
              required
            />
            {validationErrors.email && <div className="text-danger">{validationErrors.email}</div>}
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
      {validationErrors.phoneNumber && (
        <div className="text-danger">{validationErrors.phoneNumber}</div>
      )}
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
  onChange={handleCountryChange}
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

      {/* State Field */}
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
      {validationErrors.city && (
        <span className="badge bg-danger">{validationErrors.city}</span>
      )}
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
      {/* {validationErrors.pinCode && (
        <span className="badge bg-danger">{validationErrors.pinCode}</span>
      )} */}
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
      {/* {validationErrors.incorporationDate && (
        <span className="badge bg-danger">{validationErrors.incorporationDate}</span>
      )} */}
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
      {/* {validationErrors.companyName && (
        <span className="badge bg-danger">{validationErrors.companyName}</span>
      )} */}
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
      />
      {/* {validationErrors.gstIn && (
        <span className="badge bg-danger">{validationErrors.gstIn}</span>
      )} */}
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
        name="panNo"
        value={formData.panNo}
        onChange={handleInputChange}
        readOnly={!isEditable}
      />
      {/* {validationErrors.panNo && (
        <span className="badge bg-danger">{validationErrors.panNo}</span>
      )} */}
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
            name="country"
            value={formData.country}
            onChange={handleCountryChange}
            disabled={!isEditable}
            required
          >
            {countries.map((country) => (
              <option key={country} value={country}>
                {country}
              </option>
            ))}
          </select>
          {validationErrors.country && (
            <div className="text-danger">{validationErrors.country}</div>
          )}
        </div>
  </div>

  {/* State */}
  <div className="col-md-6">
  <div className="form-group mb-3">
          <label htmlFor="state">State</label>
          {formData.country === 'India' ? (
            // Show state dropdown if country is India
            <select
              className="form-control"
              id="state"
              name="state"
              value={formData.state}
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
            // Show text input for state if country is USA
            <input
              type="text"
              className="form-control"
              id="state"
              name="state"
              placeholder="Enter State"
              value={formData.state}
              onChange={handleInputChange}
              readOnly={!isEditable}
              required
            />
          )}
          {/* {validationErrors.state && (
            <div className="text-danger">{validationErrors.state}</div>
          )} */}
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
      {validationErrors.city && (
        <span className="badge bg-danger">{validationErrors.city}</span>
      )}
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
      {validationErrors.pinCode && (
        <span className="badge bg-danger">{validationErrors.pinCode}</span>
      )}
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
            name="country"
            value={formData.warehouseCountry}
            onChange={handleCountryChange}
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

{/* Warehouse State */}
<div className="col-md-6">
<div className="form-group mb-3">
          <label htmlFor="state">State</label>
          {formData.country === 'India' ? (
            // Show state dropdown if country is India
            <select
              className="form-control"
              id="state"
              name="state"
              value={formData.state}
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
            // Show text input for state if country is USA
            <input
              type="text"
              className="form-control"
              id="state"
              name="state"
              placeholder="Enter State"
              value={formData.warehouseState}
              onChange={handleInputChange}
              readOnly={!isEditable}
              required
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
    {validationErrors.warehouseCity && (
      <span className="badge bg-danger">{validationErrors.warehouseCity}</span>
    )}
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
    {validationErrors.warehousePinCode && (
      <span className="badge bg-danger">{validationErrors.warehousePinCode}</span>
    )}
  </div>
</div>
 {/* Upload Certificate */}
 <div className="col-md-6">
  <div className="form-group mb-3">
   
    <div>
      <button
        className="btn btn-dark me-2"
        onClick={() => handleDownload("uploadCertificate")}
        disabled={!isEditable}
      >
        Scope certificate
      </button>
      <button
        className="btn btn-secondary"
        onClick={() => handleView("uploadCertificate")}
        disabled={!isEditable}
      >
        View
      </button>
    </div>
  </div>
  </div>
  {/* Cancelled Cheque */}
  <div className="col-md-6">
  <div className="form-group mb-3">
   
    <div>
      <button
        className="btn btn-dark me-2"
        onClick={() => handleDownload("cancelledCheque")}
        disabled={!isEditable}
      >
        Cancelled Cheque
      </button>
      <button
        className="btn btn-secondary"
        onClick={() => handleView("cancelledCheque")}
        disabled={!isEditable}
      >
        View
      </button>
    </div>
  </div>
</div>
</div>
</div>
</div>
        {/* Submit Button */}
        {isEditable && (
    <div className="d-flex justify-content-end mt-4">
      <button type="submit" className="btn btn-warning">
        Update User
      </button>
    </div>
  )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success mt-3" role="alert">
            {successMessage}
          </div>
        )}
      </div>
      </div>
    </form>
   
  </div>
  <UserAsBuyer />
</div>


       
   
  );
};

export default UserEdit;
