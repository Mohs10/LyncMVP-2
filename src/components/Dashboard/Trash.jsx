import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getInactiveProducts } from '../../Services/TrashService'; // Import the service for fetching inactive products
import { activateProduct } from '../../Services/ProductService'; // Import the activate product service
import Topbar from './Topbar'; // Import the Topbar component
import Back from './Back'; // Import Back component if it's in a separate file

const Trash = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [productsPerPage] = useState(5); // Number of products per page

  // State to manage search query
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchInactiveProducts = async () => {
      try {
        const inactiveProducts = await getInactiveProducts(); // Fetch inactive products directly
        setProducts(inactiveProducts); // Set the inactive products
      } catch (err) {
        setError('Failed to load inactive products.');
      } finally {
        setLoading(false);
      }
    };

    fetchInactiveProducts();
  }, []);

  // Function to handle product restoration
  const handleRestore = async (productId) => {
    try {
      await activateProduct(productId); // Call the activate product service
      // Remove the restored product from the list
      setProducts(products.filter(product => product.productId !== productId));
    } catch (err) {
      setError('Failed to restore product.');
    }
  };

  // Function to handle search query changes
  const handleInactiveProductSearch = (query) => {
    setSearchQuery(query); // Update the search query state
    setCurrentPage(1); // Reset current page to 1 whenever search query changes
  };

  // Filter products based on search query before applying pagination
  const filteredProducts = products.filter((product) =>
    product.productName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Pagination logic
  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = filteredProducts.slice(indexOfFirstProduct, indexOfLastProduct);
  const totalPages = Math.ceil(filteredProducts.length / productsPerPage);

  if (loading) {
    return <div>Loading inactive products...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="d-flex flex-column">
      {/* Topbar Component */}
      <Topbar 
        title="Inactive Product" 
        userName="Neha Sharma" 
        showSearchBar={true} 
        handleInactiveProductSearch={handleInactiveProductSearch} // Pass the search handler
      />

      <div className="container-fluid mt-4">
        {/* Back Button */}
        <div className="margin-bottom-custom">
          <Back />
        </div>
        <div className="table-responsive mt-4">
          <table className="table table-hover custom-table">
            <thead className="table-header">
              <tr>
                <th>Product</th>
                <th>Category</th>
                <th>Variety</th>
                <th>Type</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {currentProducts.length > 0 ? (
                currentProducts.map((product) => (
                  <tr key={product.productId}>
                    <td className="d-flex align-items-center">
                      <img
                        src={product.productImageUrl1 || 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_sIhHxPJsvahah4pUDPtiX26SAgt8CzRvQsJ4UII5y1S1utsDVOoRmsmQi-HHhuJtjkw&usqp=CAU'}
                        alt={product.productName}
                        className="me-3 product-image"
                        style={{ cursor: 'pointer' }}
                      />
                      <span>{product.productName}</span>
                    </td>
                    <td>{product.category.categoryName}</td>
                    <td>{product.variety.varietyName}</td>
                    <td>
                      {product.types && product.types.length > 0
                        ? product.types.map((type) => type.typeName).join(', ')
                        : 'N/A'}
                    </td>
                    <td>
                      <button className="btn-action" onClick={() => handleRestore(product.productId)}>
                        <i className="fa fa-undo"></i> Restore
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" className="text-center">No inactive products found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        {/* Pagination Controls */}
        <nav aria-label="Page navigation">
  <ul className="pagination justify-content-left mt-4">
    {/* Previous Button */}
    <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
      <button
        className="page-link no-border"
        onClick={() => currentPage > 1 && setCurrentPage(currentPage - 1)}
        disabled={currentPage === 1}
        aria-label="Previous"
      >
        &#8249; Previous {/* Left-pointing angle bracket */}
      </button>
    </li>

    {/* Page Numbers */}
    {Array.from({ length: totalPages }, (_, index) => index + 1)
      .filter(
        (page) =>
          page === 1 || 
          page === totalPages || 
          (page >= currentPage - 1 && page <= currentPage + 1)
      )
      .map((page, index, array) => (
        <React.Fragment key={page}>
          {index > 0 && page !== array[index - 1] + 1 && (
            <li className="page-item disabled">
              <span className="page-link no-border">...</span>
            </li>
          )}
          <li className={`page-item ${page === currentPage ? 'active' : ''}`}>
            <button
              className="page-link no-border"
              onClick={() => setCurrentPage(page)}
              aria-label={`Page ${page}`}
            >
              {page}
            </button>
          </li>
        </React.Fragment>
      ))}

    {/* Next Button */}
    <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
      <button
        className="page-link no-border"
        onClick={() =>
          currentPage < totalPages && setCurrentPage(currentPage + 1)
        }
        disabled={currentPage === totalPages}
        aria-label="Next"
      >
        Next &#8250; {/* Right-pointing angle bracket */}
      </button>
    </li>
  </ul>
</nav>

      </div>
    </div>
  );
};

export default Trash;
