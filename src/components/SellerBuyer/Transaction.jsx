import React from 'react';
import defaultProfilePic from '../../assets/Profile-pic.png'; // Update the path if needed

const transactions = [
  {
    id: '4A222',
    name: 'Ashish Kumar',
    date: '26th Aug 2024',
    orderId: '0002',
    paymentMethod: 'UPI',
    upiId: 'AshishKumar@okicici',
    email: 'AshishKumar@gmail.com',
    phone: '92873645434',
    amount: 15000,
    status: 'Paid',
  },
  // Add more transactions as needed
];

const Transaction = () => {
  if (!transactions.length) {
    return <p>No transactions available.</p>;
  }

  return (
    <div className="container mt-4">
      
      <div className="row">
        {transactions.map((transaction) => (
          <div key={transaction.id} className="col-md-6 mb-4">
            <div className="card p-3 shadow-sm" style={{ border: '1px solid #e0e0e0', borderRadius: '10px' }}>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Transaction ID: {transaction.id}</span>
                <span className={`badge ${transaction.status.toLowerCase() === 'paid' ? 'badge-success' : 'bg-warning'}`}>
                  {transaction.status}
                </span>
              </div>
              <hr style={{ borderTop: '2px dashed #BABABA' }} />
              <div className="d-flex align-items-start mt-3">
                <img
                  src={defaultProfilePic} // Use a default profile picture
                  alt="Profile"
                  className="rounded-circle"
                  style={{ width: '60px', height: '60px', marginRight: '15px' }}
                />
                <div className="flex-grow-1">
                  <h6 className="mb-1">{transaction.name}</h6>
                  <p className="mb-0"><strong>Order ID:</strong> {transaction.orderId}</p>
                  <p className="mb-0"><strong>Payment Method:</strong> {transaction.paymentMethod}</p>
                  <p className="mb-0"><strong>UPI ID:</strong> {transaction.upiId}</p>
                  <p className="mb-0"><strong>Email:</strong> {transaction.email}</p>
                  <p className="mb-0"><strong>Phone:</strong> {transaction.phone}</p>
                </div>
                <div className="text-end">
                  <div className="text-muted">Date</div>
                  <p className="mb-0">{transaction.date}</p>
                  <div className="text-muted mt-2">Amount</div>
                  <p className="mb-0"><strong>₹{transaction.amount}</strong></p>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Transaction;
