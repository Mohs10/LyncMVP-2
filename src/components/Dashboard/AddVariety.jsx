import React, { useState, useEffect } from 'react';
import { addVariety, getVarieties } from '../../Services/AddVarietyService'; // Ensure correct import paths

const AddVariety = ({ addedVarieties, setAddedVarieties }) => {
  const [variety, setVariety] = useState('');
  const [selectedVariety, setSelectedVariety] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [allVarieties, setAllVarieties] = useState([]);

  // Function to fetch all varieties
  const fetchVarieties = async () => {
    try {
      const varieties = await getVarieties();
      setAllVarieties(varieties);
      console.log("Fetched varieties:", varieties); // Log the fetched varieties
    } catch (error) {
      setError(error.message);
      console.error("Error fetching varieties:", error); // Log fetch error
    }
  };

  // Fetch varieties when the component mounts
  useEffect(() => {
    fetchVarieties();
  }, []);

  // Function to handle adding a new variety from input box
  const handleAddVariety = async () => {
    setIsLoading(true);
    setError('');

    try {
      if (variety.trim() !== '') {
        const addedVariety = await addVariety(variety);

        // Check if addedVariety is valid and contains varietyName
        if (addedVariety && addedVariety.varietyName) {
          setAddedVarieties((prevVarieties) => [...prevVarieties, addedVariety]);
          console.log("Added variety to badges:", addedVariety.varietyName); // Log the added variety name
        } else {
          console.warn("Variety added but response does not contain varietyName.");
        }

        setVariety(''); // Clear the input field
        await fetchVarieties(); // Reload dropdown options
      } else {
        setError('Variety name cannot be empty.');
      }
    } catch (error) {
      setError('Failed to add variety.');
      console.error("Error adding variety:", error); // Log add variety error
    } finally {
      setIsLoading(false);
    }
  };

  // Handle selecting an existing variety from the dropdown
  const handleSelectVariety = (e) => {
    const selected = e.target.value;
    const varietyObject = allVarieties.find(v => v.varietyName === selected);
    setSelectedVariety(selected);
    
    // Add the selected variety as a badge if it’s not already added
    if (selected && !addedVarieties.some(v => v.varietyName === selected)) {
      setAddedVarieties((prevVarieties) => [...prevVarieties, varietyObject]);
      console.log("Added existing variety to badges:", selected); // Log the selected variety
    }
  };

  // Function to handle removing a variety from the list
  const removeVariety = (index) => {
    const varietyToRemove = addedVarieties[index];
    setAddedVarieties((prevVarieties) => prevVarieties.filter((_, i) => i !== index));
    console.log("Removed variety:", varietyToRemove); // Log the removed variety
  };

  return (
    <div className="form-group row mb-3">
      <label className="col-12 col-form-label">Add Product Variety</label>

      {/* Dropdown for selecting an existing variety */}
      <div className="col-8">
        <select
          className="form-control"
          value={selectedVariety}
          onChange={handleSelectVariety}
          disabled={isLoading}
        >
          <option value="">Select a variety</option>
          {allVarieties.map((variety, index) => (
            <option key={index} value={variety.varietyName}>
              {variety.varietyName}
            </option>
          ))}
        </select>
      </div>

      {/* Input for adding a new variety */}
      <div className="col-8 mt-2">
        <input
          type="text"
          className="form-control"
          placeholder="Enter variety name"
          value={variety}
          onChange={(e) => setVariety(e.target.value)}
          disabled={isLoading}
        />
      </div>

      <div className="col-4 mt-2">
        <button
          type="button"
          className="btn btn-dark"
          style={{ height: '42px', width: '110px' }}
          onClick={handleAddVariety}
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
        {/* Show the added varieties as badges */}
        {addedVarieties.map((variety, index) => (
          <span
            key={index}
            className="badge btn-outline-warning text-dark me-2"
            style={{ cursor: 'pointer',fontWeight: 400,border: '1px solid #FFD500', backgroundColor: 'white' }}
            onClick={() => removeVariety(index)}
          >
            {variety.varietyName} &times;
          </span>
        ))}
      </div>
    </div>
  );
};

export default AddVariety;
