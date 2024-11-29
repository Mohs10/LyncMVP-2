import React, { useState } from 'react';
import PropTypes from 'prop-types';

const FileFetcher = ({ cancelledChequeUrl, certificateUrl, onFileFetched }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Fetch file from the provided URL and return as a File object
  const fetchFile = async (fileUrl) => {
    try {
      setLoading(true);
      setError(null);

      const response = await fetch(fileUrl);
      if (!response.ok) {
        throw new Error('Failed to fetch the file');
      }

      const blob = await response.blob();
      const file = new File([blob], 'file.pdf', { type: blob.type });
      return file;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Handle cancelled cheque file fetch
  const handleCancelledChequeClick = async () => {
    try {
      const file = await fetchFile(cancelledChequeUrl);
      onFileFetched(file, 'cancelledCheque');
      alert('Cancelled Cheque fetched successfully');
    } catch (err) {
      alert('Error fetching Cancelled Cheque: ' + err.message);
    }
  };

  // Handle scope certificate file fetch
  const handleCertificateClick = async () => {
    try {
      const file = await fetchFile(certificateUrl);
      onFileFetched(file, 'certificate');
      alert('Scope Certificate fetched successfully');
    } catch (err) {
      alert('Error fetching Scope Certificate: ' + err.message);
    }
  };

  return (
    <div className="file-fetcher">
      {error && <div className="alert alert-danger">{error}</div>}
      {loading ? (
        <div>Loading...</div>
      ) : (
        <div>
          <button
            className="btn btn-primary"
            onClick={handleCancelledChequeClick}
            disabled={!cancelledChequeUrl || loading}
          >
            Fetch Cancelled Cheque
          </button>
          <button
            className="btn btn-primary ml-3"
            onClick={handleCertificateClick}
            disabled={!certificateUrl || loading}
          >
            Fetch Scope Certificate
          </button>
        </div>
      )}
    </div>
  );
};

FileFetcher.propTypes = {
  cancelledChequeUrl: PropTypes.string.isRequired,
  certificateUrl: PropTypes.string.isRequired,
  onFileFetched: PropTypes.func.isRequired,
};

export default FileFetcher;

