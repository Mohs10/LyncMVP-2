import React, { useState } from 'react';
import Carousel from 'react-bootstrap/Carousel';
import 'bootstrap/dist/css/bootstrap.min.css';

const Product = ({ products }) => {
  const [index, setIndex] = useState(0);

  const handleSelect = (selectedIndex) => {
    setIndex(selectedIndex);
  };

  const handlePrev = () => {
    setIndex(index === 0 ? Math.ceil(products.length / 4) - 1 : index - 1);
  };

  const handleNext = () => {
    setIndex(index === Math.ceil(products.length / 4) - 1 ? 0 : index + 1);
  };

  return (
    <div className="container ">
     
      <div className="carousel-container">
        <Carousel
          activeIndex={index}
          onSelect={handleSelect}
          interval={8000}
          indicators={false}
          controls={false}  // Disabling the default controls
        >
          {Array.from({ length: Math.ceil(products.length / 4) }, (_, i) => (
            <Carousel.Item key={i}>
              <div className="row">
                {products.slice(i * 4, i * 4 + 4).map((product, index) => (
                  <div className="col-md-4 product-card mb-2" key={index}>
                    <div className="thumb-wrapper">
                     
                      
                        <img
                          src={product.img}
                          alt={product.name}
                          className="img-fluid"
                          style={{ width: '100%', height: 'auto', borderRadius: '8px' }}
                        />
                      <h5 className="product-name">{product.name}</h5>
                      {/* <div className="thumb-content">
                        <h4>{product.name}</h4>
                        <p className="item-price"><strike>$400.00</strike> <b>${product.price || 250}</b></p>
                        <p className="stock-status">
                          {product.stock > 0 ? 'In Stock' : 'Out of Stock'}
                        </p>
                        <a href="#" className="btn btn-primary">Add to Cart</a>
                      </div> */}
                    </div>
                  </div>
                ))}
              </div>
            </Carousel.Item>
          ))}
        </Carousel>
        {/* Custom Prev and Next buttons */}
        <button className="carousel-control-prev" onClick={handlePrev}>
          <i className="fa fa-chevron-left"></i>
        </button>
        <button className="carousel-control-next" onClick={handleNext}>
          <i className="fa fa-chevron-right"></i>
        </button>
      </div>
    </div>
  );
};

export default Product;
