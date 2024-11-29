import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Topbar from './Topbar';
import './Product_list.css';
import Pagination from '../Pagination/Pagination'; // Ensure the path is correct
import { getAllProducts, inactivateProduct } from '../../Services/ProductService';
import { Modal, Button } from 'react-bootstrap'; // Import Bootstrap Modal and Button

const ProductsPerPage = 8;

const ProductList = () => {
  const navigate = useNavigate();
  const location = useLocation();
  
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterCategory, setFilterCategory] = useState('All');
  const [filterVariety, setFilterVariety] = useState('All');
  const [searchTerm, setSearchTerm] = useState('');
  const [noResults, setNoResults] = useState(false);
  const [currentPage, setCurrentPage] = useState(parseInt(new URLSearchParams(location.search).get('page'), 10) || 1);

  // Fetch products on mount
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const productData = await getAllProducts();
        setProducts(productData);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  // Updating the product list after adding a product
  const handleAddProduct = (newProduct) => {
    // Prepend the new product to the existing products list
    setProducts((prevProducts) => [newProduct, ...prevProducts]);
  };
  

  const handleSearch = (term) => {
    setSearchTerm(term);
    setNoResults(filteredProducts.length === 0 && term.trim() !== '');
    setCurrentPage(1);
  };

  const filteredProducts = products
    .filter((product) =>
      product.productName.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (filterCategory === 'All' || product.category.categoryName === filterCategory) &&
      (filterVariety === 'All' || product.variety.varietyName === filterVariety)
    )
    .reverse();

  const indexOfLastProduct = currentPage * ProductsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - ProductsPerPage;
  const currentProducts = filteredProducts.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(filteredProducts.length / ProductsPerPage);

  if (loading) {
    return <div>Loading products...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="d-flex flex-column">
      <Topbar title="Product List" showSearchBar={true} handleProductSearch={handleSearch} />
      
      {noResults && (
        <div style={{ left: '0', fontSize: '1.2rem', color: 'red', textAlign: 'center', marginTop: '15px' }}>
          No results found
        </div>
      )}
      
      <div className="container-fluid mt-4">
        {/* Your other UI components */}
      </div>

      <div className="container-fluid mt-4">
        <div className="table-responsive mt-4">
          <table className="table table-hover custom-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>HSN Code</th>
                <th>Category</th>
                <th>Variety</th>
                <th>Type</th>
                <th>Stock</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {currentProducts.map((product) => (
                <tr key={product.productId}>
                  <td className="d-flex align-items-center">
                    <img
                      src={product.productImageUrl1 || 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_sIhHxPJsvahah4pUDPtiX26SAgt8CzRvQsJ4UII5y1S1utsDVOoRmsmQi-HHhuJtjkw&usqp=CAU'}
                      alt={product.productName}
                      className="me-3 product-image"
                    />
                    <span>{product.productName}</span>
                  </td>
                  <td>{product.hsnCode || 'N/A'}</td>
                  <td>{product.category.categoryName}</td>
                  <td>{product.variety.varietyName}</td>
                  <td>{product.types?.map((type) => type.typeName).join(', ') || 'N/A'}</td>
                  <td>{product.stock > 0 ? `${product.stock} In Stock` : 'Out of Stock'}</td>
                  <td>
                    <button className="btn-action" onClick={() => navigate(`/product/view/${product.productId}`)}>
                      <i className="fa fa-eye"></i>
                    </button>
                    <button className="btn-action" onClick={() => navigate(`/product/edit/${product.productId}`)}>
                      <i className="fa fa-edit"></i>
                    </button>
                    <button className="btn-action" onClick={() => handleOpenModal(product)}>
                      <i className="fa fa-trash"></i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
      </div>

  

     

      {/* Confirmation Modal */}
      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Removal</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Are you sure you want to remove this product?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleConfirmRemove}>
            Confirm
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ProductList;
