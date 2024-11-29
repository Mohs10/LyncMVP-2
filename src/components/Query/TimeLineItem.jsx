

import React from "react";

const TimelineItem = ({ label, completed, timestamp, onAction, isDisabled }) => {
  return (
    <div className="timeline-item d-flex align-items-center mb-3 p-3 shadow-sm">
      <div className="flex-grow-1">
        <h6 className={`mb-1 ${completed ? "text-success" : "text-muted"}`}>
          {label}
        </h6>
        <small className="text-muted">{timestamp}</small>
      </div>
      <button
        className={`btn btn-sm ${isDisabled ? "btn-secondary" : "btn-dark"}`}
        onClick={onAction}
        disabled={isDisabled}
      >
        {completed ? "Completed" : "Action"}
      </button>
    </div>
  );
};

export default TimelineItem;
