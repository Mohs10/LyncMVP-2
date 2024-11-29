// Pagination.jsx
import React from 'react';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages === 1) return null;

  return (
    <nav aria-label="Page navigation">
      <ul className="pagination justify-content-left mt-4 pagination-small" >
        {/* Previous Button */}
        <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
          <button
            className="page-link no-border"
            onClick={() => currentPage > 1 && onPageChange(currentPage - 1)}
            disabled={currentPage === 1}
            aria-label="Previous"
          >
            &#8249; Previous
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
                  onClick={() => onPageChange(page)}
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
            onClick={() => currentPage < totalPages && onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            aria-label="Next"
          >
            Next &#8250;
          </button>
        </li>
      </ul>
    </nav>
  );
};

export default Pagination;
