import React from 'react';
import Topbar from './Topbar';
import Sidebar from './Sidebar';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';
import sonaMasuri from '../assets/sonamsuri.png';
import turmeric from '../assets/turmeric.png';
import pepper from '../assets/pepper.png';
import chilly from '../assets/chilly.png';
import './Profile.css';


export const Home = () => {
  const products = [
    { name: 'Sona Masuri Rice', image: sonaMasuri },
    { name: 'Turmeric', image: turmeric },
    { name: 'Pepper', image: pepper },
    { name: 'Chilly', image: chilly },
  ];

  const scrollLeft = () => {
    document.getElementById('productContainer').scrollLeft -= 200;
  };

  const scrollRight = () => {
    document.getElementById('productContainer').scrollLeft += 200;
  };

  return (
    <div className="container mt-5" style={{ marginLeft: '280px', marginRight: '100px' }}>
      <Topbar />
      <div className="d-flex" style={{ flexDirection: 'row' }}>
        <Sidebar />
        <div  className='container-fluid 'style={{ width: '1060px', marginTop: '50px', marginRight: '40px' }}>
          <h4 className="mb-4">Trending Products</h4>
          <div className="d-flex align-items-center">
            <button className="btn" onClick={scrollLeft}>
              <FontAwesomeIcon icon={faChevronLeft} />
            </button>
            <div
              id="productContainer"
              className="d-flex"
              style={{
                width: '1000px',
                overflowX: 'hidden', // Hides the scrollbar
                scrollBehavior: 'smooth',
                padding: '0 10px'
              }}
            >
              {products.map((product, index) => (
                <div
                  key={index}
                  className="card mx-2"
                  style={{
                    width: '223px',
                    height: '284px',
                    minWidth: '154px',
                    boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                    borderRadius: '10px',
                    textAlign: 'center'
                  }}
                >
                  <img
                    src={product.image}
                    alt={product.name}
                    style={{
                      width: '197px',
                      height: '197px',
                      alignSelf:'center',
                      borderRadius:'10px',
                      marginTop:'10px'
      
                    }}
                  />
                  <div className="card-body p-2">
                    <p className="card-text">{product.name}</p>
                  </div>
                </div>
              ))}
            </div>
            <button className="btn" onClick={scrollRight}>
              <FontAwesomeIcon icon={faChevronRight} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
