import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Topbar from './Topbar';
import './Product_list.css';
import Pagination from '../Pagination/Pagination';
import { getAllProducts, inactivateProduct } from '../../Services/ProductService';
import { getAllCategories } from '../../Services/CategoryService';
import { Modal, Button } from 'react-bootstrap';

const ProductsPerPage = 8;

const ProductList = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All'); // State for selected category
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [noResults, setNoResults] = useState(false);
  const [currentPage, setCurrentPage] = useState(parseInt(new URLSearchParams(location.search).get('page'), 10) || 1);

  const [showModal, setShowModal] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  useEffect(() => {
    const fetchProductsAndCategories = async () => {
      try {
        const [productData, categoryData] = await Promise.all([
          getAllProducts(),
          getAllCategories(),
        ]);

        setProducts(productData);
        setCategories(categoryData);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };
    fetchProductsAndCategories();
  }, []);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    queryParams.set('page', currentPage);
    navigate({ search: queryParams.toString() }, { replace: true });
  }, [currentPage, navigate]);

  const getCategoryName = (categoryId) => {
    const category = categories.find((cat) => cat.categoryId === categoryId);
    return category ? category.categoryName : 'Unknown';
  };

  const handleCategoryChange = (event) => {
    setSelectedCategory(event.target.value);
    setCurrentPage(1);
  };

  const handleSearch = (term) => {
    setSearchTerm(term);
    setNoResults(filteredProducts.length === 0 && term.trim() !== '');
    setCurrentPage(1);
  };

  const handleOpenModal = (product) => {
    setSelectedProduct(product);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setSelectedProduct(null);
    setShowModal(false);
  };

  const handleConfirmRemove = async () => {
    try {
      await inactivateProduct(selectedProduct.productId);
      setProducts((prevProducts) => prevProducts.filter((product) => product.productId !== selectedProduct.productId));
    } catch (error) {
      console.error('Error removing product:', error);
      setError('Could not remove the product.');
    } finally {
      handleCloseModal();
    }
  };

  const filteredProducts = products
    .filter((product) =>
      (selectedCategory === 'All' || product.categoryId === parseInt(selectedCategory)) &&
      product.productName.toLowerCase().includes(searchTerm.toLowerCase())
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
        <div className="no-results">No results found</div>
      )}

      <div className="container-fluid mt-4">
        <div className="row align-items-center mt-2">
          <div className="col-md-6 d-flex justify-content-start">
            <button
              className="btn btn-dark border me-1 button-14px"
              onClick={() => navigate('/add-product')}
            >
              + Add New Product
            </button>
          </div>

          <div className="col-md-6 d-flex justify-content-end">
            <div className="d-flex align-items-center custom-filter-container">
              <select
                className="custom-products-dropdown"
                value={selectedCategory}
                onChange={handleCategoryChange}
              >
                <option value="All">All Categories</option>
                {categories.map((category) => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.categoryName}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </div>

      <div className="container-fluid mt-4">
        <div className="table-responsive mt-4">
          <table className="table table-hover custom-table">
            <thead className="table-header">
              <tr>
                <th>Product</th>
                <th>HSN Code</th>
                <th>Category</th>
                <th>Stock</th>
                <th>Tag</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {currentProducts.map((product) => (
                <tr key={product.productId}>
                  <td className="product-cell">
                    <img
                      src={product.productImageUrl || 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_sIhHxPJsvahah4pUDPtiX26SAgt8CzRvQsJ4UII5y1S1utsDVOoRmsmQi-HHhuJtjkw&usqp=CAU'}
                      alt={product.productName}
                      className="me-3 product-image"
                    />
                    <span>{product.productName}</span>
                  </td>
                  <td>{product.hsnCode || 'N/A'}</td>
                  <td>{getCategoryName(product.categoryId)}</td>
                  <td>{product.stock || 'N/A'}</td>
                  <td>
                    <span className={`badge ${product.stock > 0 ? 'badge-in-stock' : 'badge-out-of-stock'}`}>
                      {product.stock > 0 ? 'In Stock' : 'Out of Stock'}
                    </span>
                  </td>
                  <td>
                    <button className="btn-action" onClick={() => navigate(`/product/view/${product.productId}`)}>
                      <i className="fa fa-eye"></i>
                    </button>
                    <button className="btn-action" onClick={() => navigate(`/product-edit/edit/${product.productId}`)}>
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
          <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
        </div>

        <Modal show={showModal} onHide={handleCloseModal} centered>
          <Modal.Header closeButton>
            <Modal.Title>Confirm Removal</Modal.Title>
          </Modal.Header>
          <Modal.Body>Are you sure you want to remove this product?</Modal.Body>
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
    </div>
  );
};

export default ProductList;
