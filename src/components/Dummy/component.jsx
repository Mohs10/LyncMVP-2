import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Topbar from './Topbar';
import './Product_list.css';
import { getAllProducts } from '../../Services/ProductService';

const ProductsPerPage = 8; // Number of products per page

const ProductList = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [sortOrder, setSortOrder] = useState('A-Z');
  const [selectedCategory, setSelectedCategory] = useState('All');

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const productData = await getAllProducts();
        setProducts(productData.reverse());
        setFilteredProducts(productData.reverse());
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  // Handler for sorting by name
  const handleSort = (order) => {
    setSortOrder(order);
    const sortedProducts = [...filteredProducts].sort((a, b) => {
      if (order === 'A-Z') {
        return a.productName.localeCompare(b.productName);
      } else if (order === 'Z-A') {
        return b.productName.localeCompare(a.productName);
      }
      return 0;
    });
    setFilteredProducts(sortedProducts);
  };

  // Handler for filtering by category
  const handleFilterByCategory = (category) => {
    setSelectedCategory(category);
    if (category === 'All') {
      setFilteredProducts(products);
    } else {
      const filtered = products.filter(
        (product) => product.category.categoryName === category
      );
      setFilteredProducts(filtered);
    }
  };

  // Pagination logic
  const indexOfLastProduct = currentPage * ProductsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - ProductsPerPage;
  const currentProducts = filteredProducts.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(filteredProducts.length / ProductsPerPage);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handlePrevPage = () => {
    if (currentPage > 1) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const getPaginationRange = () => {
    let start = Math.max(1, currentPage - 1);
    let end = Math.min(totalPages, start + 2);

    if (end - start < 2) {
      start = Math.max(1, end - 2);
    }

    return [...Array(end - start + 1)].map((_, index) => start + index);
  };

  if (loading) {
    return <div>Loading products...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="d-flex flex-column">
      <Topbar title="Product List" userName="Neha Sharma" showSearchBar={true} />

      <div className="container-fluid mt-4">
        {/* Filter and Action Buttons */}
        <div className="d-flex flex-wrap justify-content-between align-items-center mt-4">
          <div className="btn-group mb-3 mb-md-0">
            <label>Sort By:</label>
            <button
              className={`btn btn-outline-warning border ${sortOrder === 'A-Z' ? 'active' : ''}`}
              onClick={() => handleSort('A-Z')}
            >
              A-Z
            </button>
            <button
              className={`btn btn-outline-warning border ${sortOrder === 'Z-A' ? 'active' : ''}`}
              onClick={() => handleSort('Z-A')}
            >
              Z-A
            </button>
          </div>

          <div className="d-flex flex-wrap align-items-center">
            <label>Filter by Category:</label>
            <select
              className="form-select me-2"
              value={selectedCategory}
              onChange={(e) => handleFilterByCategory(e.target.value)}
            >
              <option value="All">All Categories</option>
              {/* Assuming you have a list of categories */}
              {products
                .map((product) => product.category.categoryName)
                .filter((value, index, self) => self.indexOf(value) === index)
                .map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
            </select>
          </div>
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
              {currentProducts.map((product) => (
                <tr key={product.productId}>
                  <td className="d-flex align-items-center">
                    <a onClick={() => handleViewProduct(product.productId)}>
                      <img
                        src={product.productImageUrl1 || 'default-image-url'}
                        alt={product.productName}
                        className="me-3 product-image"
                        style={{ cursor: 'pointer' }}
                      />
                    </a>
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
                    <button className="btn-action" onClick={() => handleViewProduct(product.productId)}>
                      <i className="fa fa-eye"></i>
                    </button>
                    <button className="btn-action" onClick={() => handleEditProduct(product.productId)}>
                      <i className="fa fa-edit"></i>
                    </button>
                    <button className="btn-action" onClick={() => handleDeleteProduct(product.productId)}>
                      <i className="fa fa-trash"></i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <nav aria-label="Page navigation">
          <ul className="pagination pagination-custom">
            <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
              <span className="page-link" onClick={handlePrevPage}>
                <i className="fas fa-chevron-left"></i>
              </span>
            </li>
            {getPaginationRange().map((pageNumber) => (
              <li
                key={pageNumber}
                className={`page-item ${pageNumber === currentPage ? 'active' : ''}`}
                onClick={() => handlePageChange(pageNumber)}
              >
                <span className="page-link">{pageNumber}</span>
              </li>
            ))}
            <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
              <span className="page-link" onClick={handleNextPage}>
                <i className="fas fa-chevron-right"></i>
              </span>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  );
};

export default ProductList;
