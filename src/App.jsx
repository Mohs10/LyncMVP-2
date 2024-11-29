import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Route, Routes, useLocation } from 'react-router-dom';
import Sidebar from './components/Dashboard/Sidebar';
import PrivateRoute from '../PrivateRoute'; // Import the PrivateRoute component
import './App.css';


// Lazy loading components
const Login = lazy(() => import('./components/login'));
const Dashboard = lazy(() => import('./components/Dashboard/Dashboard'));
const Product = lazy(() => import('./components/Dashboard/Add_product'));
const ProductList = lazy(() => import('./components/Dashboard/Product_list'));
const View = lazy(() => import('./components/Dashboard/View'));

const User = lazy(() => import('./components/Dashboard/User'));

const UserEdit = lazy(() => import('./components/Dashboard/Edit_user'));
const Trash = lazy(() => import('./components/Dashboard/Trash'));
const NotFound = lazy(() => import('./components/Dashboard/Notfound')); // Import the NotFound component
const Testimonial = lazy(() => import('./components/Testimonial/Testimonial'));
const Query = lazy(() => import('./components/Query/Query'));
const SendQuery = lazy(() => import('./components/Query/SendQuery'));
const PriceDetails = lazy(() => import('./components/Query/PriceDetails')); // Lazy load PriceDetails component
const QueryProduct = lazy(() => import('./components/Query/QueryProduct')); // Lazy load QueryProduct component
const SampleRequest = lazy(() => import('./components/Query/SampleRequest'));
const NotifyBuyer = lazy(() => import('./components/Query/NotifyBuyer'));
const AdminQuoteToBuyer = lazy(() => import('./components/Query/AdminQuoteToBuyer'));
const PriceNegotiation = lazy(() => import('./components/Query/PriceNegotiation'));
const ProductEdit =lazy(() => import('./components/Dashboard/ProductEdit'));
const QueryDetails=lazy(() => import('./components/Query/QueryDetails'))

const App = () => {
  return (
    <Router>
      <MainContent />
    </Router>
  );
};

const MainContent = () => {
  const location = useLocation();
  const noSidebarRoutes = ['/'];
  const shouldRenderSidebar = !noSidebarRoutes.includes(location.pathname);

  return (
    <div className="d-flex">
      {shouldRenderSidebar && <Sidebar />}
      <div className={`flex-grow-1 ${shouldRenderSidebar ? '' : 'full-width'}`}>
        <Suspense fallback={<div>Loading...</div>}>
          <Routes>
            <Route path="/" element={<Login />} />
            {/* Wrap protected routes with PrivateRoute */}
            <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Dashboard />
                </PrivateRoute>
              }
            />
            <Route
              path="/add-product"
              element={
                <PrivateRoute>
                  <Product />
                </PrivateRoute>
              }
            />
            <Route
              path="/product-list"
              element={
                <PrivateRoute>
                  <ProductList />
                </PrivateRoute>
              }
            />
            <Route path="/adminQuoteToBuyer/:queryId" element={<AdminQuoteToBuyer />} />
            
            <Route
              path="/product/view/:productId"
              element={
                <PrivateRoute>
                  <View />
                </PrivateRoute>
              }
            />
            <Route path="/product-edit/edit/:productId" element={<ProductEdit />} />

              <Route path="/price-details/:queryId" element={<PriceDetails />} />
            <Route
              path="/users"
              element={
                <PrivateRoute>
                  <User />
                </PrivateRoute>
              }
            />
            <Route
              path="/edit-user/:userId"
              element={
                <PrivateRoute>
                  <UserEdit />
                </PrivateRoute>
              }
            />
            
            <Route
              path="/trash"
              element={
                <PrivateRoute>
                  <Trash />
                </PrivateRoute>
              }
            />
            <Route
              path="/query"
              element={
                <PrivateRoute>
                  <Query />
                </PrivateRoute>
              }
            />
            <Route
              path="/testimonial"
              element={
                <PrivateRoute>
                  <Testimonial />
                </PrivateRoute>
              }
            />
            <Route
              path="/send-query/:queryId"
              element={
                <PrivateRoute>
                  <SendQuery />
                </PrivateRoute>
              }
            />
           {/* <Route
  path="/price-details/:queryId"
  element={
    <PrivateRoute>
      <PriceDetails />
    </PrivateRoute>
  }
/> */}
<Route
  path="/query-details/:queryId"
  element={
    <PrivateRoute>
      <QueryDetails />
    </PrivateRoute>
  }
/>

<Route path="/price-negotiation" element={<PriceNegotiation />} />

            
            <Route path="/query-product" element={<QueryProduct />} />
            <Route
              path="/sample-request"
              element={
                <PrivateRoute>
                  <SampleRequest />
                </PrivateRoute>
              }
            />
            <Route
              path="/notify-buyer/:queryId"
              element={
                <PrivateRoute>
                  <NotifyBuyer />
                </PrivateRoute>
              }
            />
            {/* 404 Route */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </div>
    </div>
  );
};

export default App;
