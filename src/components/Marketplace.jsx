import React, { useEffect, useState } from 'react';
import Topbar from './Topbar';
import Sidebar from './Sidebar';
import { useNavigate } from 'react-router-dom';
import { getAllProducts, getAllCategories } from '../Services/AllCategoryService';
import './Market.css';
import defaultImage from '../assets/sonamsuri.png';
// const DEFAULT_IMAGE = './assets/pepper.png'; // Fallback image
const STATIC_CATEGORIES = ['All', 'Spices', 'Cereals', 'Pulses', 'Herbs']; // Static categories

const Marketplace = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [categories, setCategories] = useState(STATIC_CATEGORIES);
  const [categoryMap, setCategoryMap] = useState({});
  const [error, setError] = useState(null);
  const [activeCategory, setActiveCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState(''); // State to store search query
  const [loading, setLoading] = useState(true); // Loading state

  useEffect(() => {
    const fetchProductsAndCategories = async () => {
      try {
        setLoading(true); // Set loading to true at the start

        // Fetch all products
        const productData = await getAllProducts();
        console.log('Fetched Products:', productData);
        setProducts(productData);
        setFilteredProducts(productData);

        // Fetch all categories
        const categoryData = await getAllCategories();
        console.log('Fetched Categories:', categoryData);

        const dynamicCategories = categoryData.map((category) => category.categoryName);
        const mergedCategories = Array.from(new Set([...STATIC_CATEGORIES, ...dynamicCategories]));

        // Create a category map for easier lookup
        const categoryMap = categoryData.reduce((map, category) => {
          map[category.categoryName] = category.categoryId;
          return map;
        }, {});

        setCategories(mergedCategories);
        setCategoryMap(categoryMap);
      } catch (error) {
        console.error('Error fetching data:', error.message);
        setError('Failed to fetch data. Please try again later.');
      } finally {
        setLoading(false); // Set loading to false after data is fetched
      }
    };

    fetchProductsAndCategories();
  }, []);

  const handleCategoryClick = (category) => {
    setActiveCategory(category);

    if (category === 'All') {
      // Show all products
      setFilteredProducts(products);
    } else {
      // Get categoryId from categoryMap
      const selectedCategoryId = categoryMap[category];
      if (selectedCategoryId) {
        // Filter products based on categoryId
        const filtered = products.filter((product) => product.categoryId === selectedCategoryId);
        console.log(`Filtered Products for categoryId ${selectedCategoryId}:`, filtered);
        setFilteredProducts(filtered);
      }
    }
  };

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`); // Navigate to product details page
  };

  const scrollTo = (direction) => {
    const container = document.querySelector('.scroll-container');
    console.log(container); // Log the container to ensure it's selected correctly
    const scrollAmount = 220; // Adjust this value to control how far the container scrolls
    if (container) {
      if (direction === 'left') {
        container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
      } else if (direction === 'right') {
        container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
      }
    }
  };

  const handleSearchChange = (query) => {
    setSearchQuery(query);

    // Filter products based on the search query
    if (query) {
      const filtered = products.filter((product) =>
        product.productName.toLowerCase().includes(query.toLowerCase())
      );
      setFilteredProducts(filtered);
    } else {
      // If no search query, show all products
      setFilteredProducts(products);
    }
  };

  return (
    <div className="container mt-5" style={{ marginLeft: '280px', marginRight: '100px' }}>
      <Topbar title="" onSearchChange={handleSearchChange} /> {/* Pass search handler */}
      <div className="d-flex" style={{ flexDirection: 'row' }}>
        <Sidebar />
        <div className="container-fluid" style={{ width: '1060px', marginTop: '50px', marginRight: '50px' }}>
          {/* Filter Buttons */}
          <div className="filter-buttons mb-4">
            <button className="scroll-arrow left" onClick={() => scrollTo('left')}>
              &#10094; {/* Left arrow */}
            </button>
            <div className="scroll-container">
              {categories.map((category) => (
                <button
                  key={category}
                  className={`filter-btn ${activeCategory === category ? 'active' : ''}`}
                  onClick={() => handleCategoryClick(category)}
                >
                  {category}
                </button>
              ))}
            </div>
            <button className="scroll-arrow right" onClick={() => scrollTo('right')}>
              &#10095; {/* Right arrow */}
            </button>
          </div>

          {/* Loading and Error Handling */}
          {loading ? (
            <p className="text-muted">Loading products...</p>
          ) : error ? (
            <p className="text-danger">{error}</p>
          ) : filteredProducts.length === 0 ? (
            <p className="text-muted">No products found in this category.</p>
          ) : (
            <div className="d-flex flex-wrap">
              {filteredProducts.map((product) => (
                <div
                  key={product.productId}
                  className="card mx-2 mb-4 product-card"
                  style={{
                    width: '223px',
                    height: '284px',
                    minWidth: '154px',
                    boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                    borderRadius: '10px',
                    textAlign: 'center',
                    cursor: 'pointer',
                  }}
                  // onClick={() => handleProductClick(product.productId)}
                >
                  <img
                    src={product.productImageUrl || defaultImage} // Use prfaoduct image or fallback
                    alt={product.productName}
                    style={{
                      width: '197px',
                      height: '197px',
                      alignSelf: 'center',
                      borderRadius: '10px',
                      marginTop: '10px',
                    }}
                  />
                  <div className="card-body p-2">
                    <p className="card-text">{product.productName || 'Unnamed Product'}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Marketplace;
