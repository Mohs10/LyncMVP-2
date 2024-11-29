import React from "react";
import Sidebar from './Sidebar';
import Topbar from './Topbar';

export const New =() => {
    return (
        <div className="container mt-5">
      <Topbar />
      <div className="content-wrapper" style={{ display: 'flex', flexDirection: 'row' }}>
        <Sidebar />
        </div>
        </div>
        );
        };
export default New;