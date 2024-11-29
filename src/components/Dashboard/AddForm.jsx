import React, { useState, useEffect } from 'react';
import { addForm, getForms } from '../../Services/AddFormService';

const AddForm = ({ addedForms, setAddedForms }) => {
  const [form, setForm] = useState('');
  const [selectedForm, setSelectedForm] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [allForms, setAllForms] = useState([]);

  // Function to fetch all forms
  const fetchForms = async () => {
    try {
      const forms = await getForms();
      setAllForms(forms);
      console.log("Fetched forms:", forms); // Log the fetched forms
    } catch (error) {
      setError(error.message);
      console.error("Error fetching forms:", error); // Log fetch error
    }
  };

  // Fetch forms when the component mounts
  useEffect(() => {
    fetchForms();
  }, []);

  // Function to handle adding the form from input box
  const handleAddForm = async () => {
    setIsLoading(true);
    setError('');

    try {
      if (form.trim() !== '') {
        const addedForm = await addForm(form);

        // Check if addedForm is valid and contains formName
        if (addedForm && addedForm.formName) {
          setAddedForms((prevForms) => [...prevForms, addedForm]);
          console.log("Added form to badges:", addedForm.formName); // Log the added form name
        } else {
          console.warn("Form added but response does not contain formName.");
        }

        setForm(''); // Clear the input field
        await fetchForms(); // Reload dropdown options
      } else {
        setError('Form name cannot be empty.');
      }
    } catch (error) {
      setError('Failed to add form.');
      console.error("Error adding form:", error); // Log add form error
    } finally {
      setIsLoading(false);
    }
  };

  // Handle selecting an existing form from the dropdown
  const handleSelectForm = (e) => {
    const selected = e.target.value;
    const formObject = allForms.find(f => f.formName === selected);
    setSelectedForm(selected);
    
    // Add the selected form as a badge if it’s not already added
    if (selected && !addedForms.some(f => f.formName === selected)) {
      setAddedForms((prevForms) => [...prevForms, formObject]);
      console.log("Added existing form to badges:", selected); // Log the selected form
    }
  };

  // Function to handle removing a form from the list
  const removeForm = (index) => {
    const formToRemove = addedForms[index];
    setAddedForms((prevForms) => prevForms.filter((_, i) => i !== index));
    console.log("Removed form:", formToRemove); // Log the removed form
  };

  return (
    <div className="form-group row mb-3">
      <label className="col-12 col-form-label">Add Form</label>
      
      {/* Dropdown for selecting an existing form */}
      <div className="col-8">
        <select
          className="form-control"
          value={selectedForm}
          onChange={handleSelectForm}
          disabled={isLoading}
        >
          <option value="">Select a form</option>
          {allForms.map((form, index) => (
            <option key={index} value={form.formName}>
              {form.formName}
            </option>
          ))}
        </select>
      </div>

      {/* Input for adding a new form */}
      <div className="col-8 mt-2">
        <input
          type="text"
          className="form-control"
          placeholder="Enter form name"
          value={form}
          onChange={(e) => setForm(e.target.value)}
          disabled={isLoading}
        />
      </div>

      <div className="col-4 mt-2">
        <button
          type="button"
          className="btn btn-dark"
          style={{ height: '42px', width: '110px' }}
          onClick={handleAddForm}
          disabled={isLoading}
        >
          {isLoading ? 'Adding...' : 'Add'}
        </button>
      </div>

      {error && (
        <div className="col-12 mt-2 text-danger">
          <small>{error}</small>
        </div>
      )}

      <div className="mt-2">
        {/* Show the added forms as badges */}
        {addedForms.map((form, index) => (
          <span
            key={index}
            className="badge btn-outline-warning text-dark me-2"
            style={{ cursor: 'pointer',fontWeight: 400,border: '1px solid #FFD500', backgroundColor: 'white' }}
            onClick={() => removeForm(index)}
          >
            {form.formName} &times;
          </span>
        ))}
      </div>
    </div>
  );
};

export default AddForm;
