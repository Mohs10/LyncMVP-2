import React, { useState, useEffect } from "react";
import SellerBuyerService from "../../Services/SellerBuyerService";
import EditUserService from "../../Services/EditUserService";
import axios from "axios";

const FileFetcher = ({ userId, isEditable }) => {
  const [fileUrls, setFileUrls] = useState({
    certificateUrl: "",
    cancelledChequeUrl: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [uploading, setUploading] = useState(false);

  // Fetch user details on mount
  useEffect(() => {
    const fetchFiles = async () => {
      if (!userId) {
        console.error("No userId provided.");
        setError("User ID is required to fetch files.");
        return;
      }

      setLoading(true);
      try {
        const user = await SellerBuyerService.findUserById(userId);

        if (user) {
          setFileUrls({
            certificateUrl: user.certificateUrl || "",
            cancelledChequeUrl: user.cancelledChequeUrl || "",
          });
        } else {
          setError(`User with ID ${userId} not found.`);
        }
      } catch (error) {
        console.error("Error fetching user details:", error.message);
        setError(error.message || "Failed to fetch user details. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    fetchFiles();
  }, [userId]);

  // Handle file download
  const handleDownload = async (fileType) => {
    const url = fileUrls[`${fileType}Url`];
    if (!url) {
      setError(`No URL available for ${fileType.replace(/([A-Z])/g, " $1").toLowerCase()}.`);
      return;
    }

    try {
      const response = await axios.get(url, {
        responseType: "blob",
      });

      const link = document.createElement("a");
      const blob = new Blob([response.data]);
      link.href = window.URL.createObjectURL(blob);
      link.download = url.split("/").pop();
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      setError(`Failed to download ${fileType}. Please try again.`);
    }
  };

  // Handle file view
  const handleView = (fileType) => {
    const url = fileUrls[`${fileType}Url`];
    if (!url) {
      setError(`No URL available for ${fileType.replace(/([A-Z])/g, " $1").toLowerCase()}.`);
      return;
    }

    window.open(url, "_blank");
  };

  // Handle file upload
  const handleUpload = async (fileType, file) => {
    if (!file) {
      setError("Please select a file to upload.");
      return;
    }

    setUploading(true);
    try {
      const userData = {}; // Include relevant user data if required
      const updatedData = await EditUserService.editUser(
        userId,
        userData,
        null, // Profile picture is not handled here
        fileType === "certificate" ? file : null,
        fileType === "cancelledCheque" ? file : null
      );

      setFileUrls({
        ...fileUrls,
        [`${fileType}Url`]: updatedData[`${fileType}Url`],
      });
      setError("");
    } catch (error) {
      setError(`Failed to upload ${fileType}. Please try again.`);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="container">
      {loading && <p>Loading files...</p>}
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row">
        {/* Scope Certificate Section */}
        <div className="col-md-6">
          <div className="form-group mb-3">
            <div className="row">
              <div className="col-12">
                <button
                  className={`btn ${isEditable ? "btn-dark" : "btn-secondary"} me-2`}
                  onClick={() => handleDownload("certificate")}
                  disabled={!fileUrls.certificateUrl}
                >
                  Download Certificate
                </button>
                <button
                  className={`btn ${isEditable ? "text-decoration-underline text-dark" : "text-decoration-underline text-secondary"}`}
                  onClick={() => handleView("certificate")}
                  disabled={!fileUrls.certificateUrl}
                  style={{
                    backgroundColor: "transparent",
                    border: "none",
                  }}
                >
                  View
                </button>

                {isEditable && (
                  <input
                    type="file"
                    onChange={(e) => handleUpload("certificate", e.target.files[0])}
                    disabled={uploading}
                  />
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Cancelled Cheque Section */}
        <div className="col-md-6">
          <div className="form-group mb-3">
            <div className="row">
              <div className="col-12">
                <button
                  className={`btn ${isEditable ? "btn-dark" : "btn-secondary"} me-2`}
                  onClick={() => handleDownload("cancelledCheque")}
                  disabled={!fileUrls.cancelledChequeUrl}
                >
                  Download Cancelled Cheque
                </button>
                <button
                  className={`btn ${isEditable ? "text-decoration-underline text-dark" : "text-decoration-underline text-secondary"}`}
                  onClick={() => handleView("cancelledCheque")}
                  disabled={!fileUrls.cancelledChequeUrl}
                  style={{
                    backgroundColor: "transparent",
                    border: "none",
                  }}
                >
                  View
                </button>

                {isEditable && (
                  <input
                    type="file"
                    onChange={(e) => handleUpload("cancelledCheque", e.target.files[0])}
                    disabled={uploading}
                  />
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FileFetcher;
