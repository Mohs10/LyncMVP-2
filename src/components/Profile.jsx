
import React from "react";
import Sidebar from './Sidebar';
import Topbar from './Topbar';
import './Profile.css';


export const Profile =() => {
    return (
        <div className="container mt-5">
      <Topbar />
      <div className="content-wrapper" style={{ display: 'flex', flexDirection: 'row' }}>
        <Sidebar />
        <div  className='container-fluid 'style={{ width: '1060px', marginTop: '50px' , marginLeft:'150px'}}>
          <h4 className="mb-4">Profile content will appear here.</h4>
          </div>
        </div>
        </div>
        );
        };
export default Profile;






























// import React, { useState } from "react";
// import Sidebar from './Sidebar';
// import Topbar from './Topbar';
// // import { Form, Button, Row, Col, InputGroup } from "react-bootstrap";
// // import { FaCamera, FaFlag } from "react-icons/fa";
// import './Profile.css';

// export const Profile = () => {
// //   const [activeTab, setActiveTab] = useState("profile");
// //   const [profileData, setProfileData] = useState({
// //     fullName: " ",
// //     email: "",
// //     phone: "",
// //     address: "",
// //     country: "",
// //     state: "",
// //     city: "",
// //     pinCode: ""
// //   });

// //   const handleTabSwitch = (tab) => {
// //     setActiveTab(tab);
// //   };

//   // const handleInputChange = (e) => {
//   //   const { name, value } = e.target;
//   //   setProfileData({ ...profileData, [name]: value });
//   // };

//   // const countryOptions = ["India", "USA", "Canada", "UK"];
//   // const stateOptions = ["Gujrat", "Maharashtra", "Rajasthan"];
//   // const cityOptions = ["Vadodara", "Ahmedabad", "Surat"];

//   // const inputStyle = {
//   //   width: "100%",
//   //   height: "48px",
//   //   boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
//   //   marginBottom: "15px"
//   // };

//   // const buttonStyle = {
//   //   width: "270px",
//   //   height: "50px",
//   //   display: "block",
//   //   margin: "0 auto",
//   // };

//   // const cameraIconStyle = {
//   //   display: "block", // camera icon as a block element
//   //   margin: "0 auto 0 auto", // position below the image and center
//   //   backgroundColor: "white",
//   //   borderRadius: "50%",
//   //   boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.2)",
//   // };

//   return (
//     <div className="container mt-5">
//       <Topbar />
//       <div className="content-wrapper" style={{ display: 'flex', flexDirection: 'row' }}>
//         <Sidebar />
//         {/* <div className="middle-content mx-auto my-4 p-4 shadow-lg bg-white rounded" > */}

        
//           {/* <div className="d-flex justify-content-center mb-4">
//             <Button
//               variant={activeTab === "profile" ? "dark" : "light"}
//               onClick={() => handleTabSwitch("profile")}
//               className="me-2"
//             >
//               Profile
//             </Button>
//             <Button
//               variant={activeTab === "company" ? "dark" : "light"}
//               onClick={() => handleTabSwitch("company")}
//             >
//               Company
//             </Button>
//           </div> */}

          
//           {/* {activeTab === "profile" && ( */}
// {/* //             <Form>
// //               <Row className="mb-3">
// //                 <Col md={4} className="text-center position-relative"> */}
// //                   {<img
// //                     src="..\src\assets\Profile-pic.png"
// //                     alt="Profile"
// //                     className="rounded-circle"
// //                     width="100"
// //                   />}
                  
// //                   <Button variant="light" className="position-static" style={cameraIconStyle}>
// //                     <FaCamera />
// //                     <input type="file" className="position-absolute top-0 start-0 opacity-0" />
// //                   </Button>
// //                   <div>Sidharth Mehta</div>
// //                   <div>sidharth.mehta@gmail.com</div>
// //                 </Col>

// //                 <Col md={8}>
// //                   <Row>
// //                     <Col md={6}>
// //                       <Form.Group controlId="formFullName">
// //                         <Form.Label className="form-label-custom">Full Name</Form.Label>
// //                         <Form.Control
// //                           type="text"
// //                           placeholder="Enter full name"
// //                           name="fullName"
// //                           value={profileData.fullName}
// //                           onChange={handleInputChange}
// //                           style={inputStyle}
// //                         />
// //                       </Form.Group>
// //                     </Col>
// //                     <Col md={6}>
// //                       <Form.Group controlId="formPhone">
// //                         <Form.Label className="form-label-custom">Phone</Form.Label>
// //                         <InputGroup>
                          
// //                           <Form.Control
// //                             type="text"
// //                             placeholder=""
// //                             name="phone"
// //                             value={profileData.phone}
// //                             onChange={handleInputChange}
// //                             style={inputStyle}
// //                           />
// //                         </InputGroup>
// //                       </Form.Group>
// //                     </Col>
// //                   </Row>

// //                   <Row>
// //   <Col md={6}>
// //     <Form.Group controlId="formEmail">
// //       <Form.Label className="form-label-custom">Email</Form.Label>
// //       <Form.Control
// //         type="email"
// //         placeholder=""
// //         name="email"
// //         value={profileData.email}
// //         onChange={handleInputChange}
// //         style={inputStyle}
// //       />
// //     </Form.Group>
// //   </Col>
// //   <Col md={6}>
// //     <Form.Group controlId="formAddress">
// //       <Form.Label className="form-label-custom">Address</Form.Label>
// //       <Form.Control
// //         type="text"
// //         placeholder=""
// //         name="address"
// //         value={profileData.address}
// //         onChange={handleInputChange}
// //         style={inputStyle}
// //       />
// //     </Form.Group>
// //   </Col>
// // </Row>
// //                   <Row>
// //                     <Col md={6}>
// //                       <Form.Group controlId="formCountry">
// //                         <Form.Label className="form-label-custom">Country</Form.Label>
// //                         <Form.Select
// //                           name="country"
// //                           value={profileData.country}
// //                           onChange={handleInputChange}
// //                           style={inputStyle}
// //                         >
// //                           {countryOptions.map((country, index) => (
// //                             <option key={index} value={country}>
// //                               {country}
// //                             </option>
// //                           ))}
// //                         </Form.Select>
// //                       </Form.Group>
// //                     </Col>
// //                     <Col md={6}>
// //                       <Form.Group controlId="formState">
// //                         <Form.Label className="form-label-custom">State</Form.Label>
// //                         <Form.Select
// //                           name="state"
// //                           value={profileData.state}
// //                           onChange={handleInputChange}
// //                           style={inputStyle}
// //                         >
// //                           {stateOptions.map((state, index) => (
// //                             <option key={index} value={state}>
// //                               {state}
// //                             </option>
// //                           ))}
// //                         </Form.Select>
// //                       </Form.Group>
// //                     </Col>
// //                   </Row>

// //                   <Row>
// //                     <Col md={6}>
// //                       <Form.Group controlId="formCity">
// //                         <Form.Label className="form-label-custom">City</Form.Label>
// //                         <Form.Select
// //                           name="city"
// //                           value={profileData.city}
// //                           onChange={handleInputChange}
// //                           style={inputStyle}
// //                         >
// //                           {cityOptions.map((city, index) => (
// //                             <option key={index} value={city}>
// //                               {city}
// //                             </option>
// //                           ))}
// //                         </Form.Select>
// //                       </Form.Group>
// //                     </Col>
// //                     <Col md={6}>
// //                       <Form.Group controlId="formPinCode">
// //                         <Form.Label className="form-label-custom">Pin Code</Form.Label>
// //                         <Form.Control
// //                           type="text"
// //                           placeholder=""
// //                           name="pinCode"
// //                           value={profileData.pinCode}
// //                           onChange={handleInputChange}
// //                           style={inputStyle}
// //                         />
// //                       </Form.Group>
// //                     </Col>
// //                   </Row>

// //                   <Button variant="dark" type="submit" style={buttonStyle}>
// //                     Save
// //                   </Button>
// //                 </Col>
// //               </Row>
// //             </Form>
//           // )}
//         // </div>
//       </div>
//     </div>
//   );
// };

// export default Profile;
