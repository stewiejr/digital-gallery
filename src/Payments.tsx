import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Payments.css';
import { api } from './api';
import { useAuth } from './AuthContext';

interface PaymentMethod {
  id: string;
  cardholderName: string;
  cardNumber: string;
  expiryDate: string;
  isDefault: boolean;
}

interface Payment {
  id: string;
  createdAt: string;
  price: number;
}

const Payments: React.FC = () => {
  const [payments, setPayments] = useState<Payment[]>([]);
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [showAddCard, setShowAddCard] = useState(false);
  
  // Card form
  const [cardholderName, setCardholderName] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [expiryDate, setExpiryDate] = useState('');
  const [cvv, setCvv] = useState('');
  const [isDefault, setIsDefault] = useState(false);
  
  const { user } = useAuth();

  // Fetch both payments and saved cards
  useEffect(() => {
    const fetchData = async () => {
      if (!user) {
        setError('User is not authenticated');
        return;
      }
      console.log('User data:', user);
      console.log('User ID:', user.id);
      try {
        const [paymentsResponse, methodsResponse] = await Promise.all([
          api.get(`/payments/users/${user.id}`),
          api.get(`/payment-methods/users/${user.id}`),
        ]);
        setPayments(paymentsResponse.data);
        setPaymentMethods(methodsResponse.data);
        setError('');
      } catch (err: any) {
        setError('Failed to load payments');
        console.error('Error fetching data:', err);
      }
    };

    fetchData();
  }, [user]);

  const formatCardNumber = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    const matches = v.match(/\d{4,16}/g);
    const match = (matches && matches[0]) || '';
    const parts = [];

    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4));
    }

    if (parts.length) {
      return parts.join(' ');
    } else {
      return value;
    }
  };

  const formatExpiryDate = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (v.length >= 2) {
      return v.slice(0, 2) + '/' + v.slice(2, 4);
    }
    return v;
  };

  const handleAddCard = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!cardNumber.replace(/\s/g, '').match(/^\d{13,19}$/)) {
        setError('Invalid card number');
        setLoading(false);
        return;
      }
      if (!expiryDate.match(/^\d{2}\/\d{2}$/)) {
        setError('Expiry date must be MM/YY format');
        setLoading(false);
        return;
      }
      if (!cvv.match(/^\d{3,4}$/)) {
        setError('CVV must be 3-4 digits');
        setLoading(false);
        return;
      }
      if (!cardholderName.trim()) {
        setError('Cardholder name is required');
        setLoading(false);
        return;
      }

      const response = await api.post(`/payment-methods/users/${user!.id}`, {
        cardholderName,
        cardNumber: cardNumber.replace(/\s/g, ''),
        expiryDate,
        cvv,
        isDefault: isDefault || paymentMethods.length === 0,
      });

      console.log('Card added successfully:', response.data);

      // Reset form
      setCardholderName('');
      setCardNumber('');
      setExpiryDate('');
      setCvv('');
      setIsDefault(false);
      setShowAddCard(false);

      alert('Card added successfully!');
    } catch (err: any) {
      console.error('Card add error:', err);
      console.error('Response data:', err.response?.data);
      console.error('Response status:', err.response?.status);
      const errorMsg = err.response?.data?.message || err.message || 'Failed to add card';
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCard = async (methodId: string) => {
    if (!window.confirm('Are you sure you want to delete this card?')) {
      return;
    }

    try {
      await api.delete(`/payment-methods/${methodId}`);
      setPaymentMethods(paymentMethods.filter(m => m.id !== methodId));
      alert('Card deleted successfully!');
    } catch (err: any) {
      setError('Failed to delete card');
    }
  };

  return (
    <div className="payments-container">
      <h1>Payments & Cards</h1>
      {error && <p style={{ color: 'red', marginBottom: '15px' }}>{error}</p>}

      {/* Saved Payment Methods Section */}
      <div style={{ marginBottom: '40px' }}>
        <h2>Saved Cards</h2>
        {paymentMethods.length === 0 ? (
          <p>No saved cards yet.</p>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '15px' }}>
            {paymentMethods.map((method) => (
              <div
                key={method.id}
                style={{
                  border: '1px solid #ccc',
                  padding: '15px',
                  borderRadius: '8px',
                  backgroundColor: method.isDefault ? '#e8f5e9' : '#f5f5f5',
                }}
              >
                <p style={{ fontWeight: 'bold' }}>{method.cardholderName}</p>
                <p style={{ fontSize: '18px', letterSpacing: '2px' }}>{method.cardNumber}</p>
                <p>Expires: {method.expiryDate}</p>
                {method.isDefault && (
                  <p style={{ color: '#4caf50', fontWeight: 'bold' }}>✓ Default Card</p>
                )}
                <button
                  onClick={() => handleDeleteCard(method.id)}
                  style={{
                    marginTop: '10px',
                    padding: '8px 12px',
                    backgroundColor: '#f44336',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '14px',
                  }}
                >
                  Delete
                </button>
              </div>
            ))}
          </div>
        )}

        {!showAddCard && (
          <button
            onClick={() => setShowAddCard(true)}
            style={{
              marginTop: '20px',
              padding: '10px 20px',
              backgroundColor: '#2196f3',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: 'bold',
            }}
          >
            + Add New Card
          </button>
        )}

        {/* Add Card Form */}
        {showAddCard && (
          <form onSubmit={handleAddCard} style={{ marginTop: '30px', padding: '20px', border: '1px solid #ddd', borderRadius: '8px', maxWidth: '400px' }}>
            <h3>Add New Card</h3>

            <div style={{ marginBottom: '15px' }}>
              <label>Cardholder Name</label>
              <input
                type="text"
                value={cardholderName}
                onChange={(e) => setCardholderName(e.target.value)}
                placeholder="John Doe"
                required
                disabled={loading}
                style={{ width: '100%', padding: '8px', marginTop: '5px' }}
              />
            </div>

            <div style={{ marginBottom: '15px' }}>
              <label>Card Number</label>
              <input
                type="text"
                value={cardNumber}
                onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                placeholder="4242 4242 4242 4242"
                required
                disabled={loading}
                style={{ width: '100%', padding: '8px', marginTop: '5px' }}
              />
              <small style={{ color: '#666' }}>Test: 4242 4242 4242 4242</small>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '15px' }}>
              <div>
                <label>Expiry Date</label>
                <input
                  type="text"
                  value={expiryDate}
                  onChange={(e) => setExpiryDate(formatExpiryDate(e.target.value))}
                  placeholder="MM/YY"
                  required
                  disabled={loading}
                  style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                />
              </div>

              <div>
                <label>CVV</label>
                <input
                  type="text"
                  value={cvv}
                  onChange={(e) => setCvv(e.target.value.replace(/\D/g, '').slice(0, 4))}
                  placeholder="123"
                  required
                  disabled={loading}
                  style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                />
              </div>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label>
                <input
                  type="checkbox"
                  checked={isDefault || paymentMethods.length === 0}
                  onChange={(e) => setIsDefault(e.target.checked)}
                  disabled={loading || paymentMethods.length === 0}
                />
                {' '}Set as default card {paymentMethods.length === 0 && '(automatically set)'}
              </label>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                type="submit"
                disabled={loading}
                style={{
                  flex: 1,
                  padding: '10px',
                  backgroundColor: loading ? '#ccc' : '#4caf50',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  fontWeight: 'bold',
                }}
              >
                {loading ? 'Adding...' : 'Add Card'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowAddCard(false);
                  setCardholderName('');
                  setCardNumber('');
                  setExpiryDate('');
                  setCvv('');
                  setIsDefault(false);
                }}
                disabled={loading}
                style={{
                  flex: 1,
                  padding: '10px',
                  backgroundColor: '#757575',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>

      {/* Payment History Section */}
      <div>
        <h2>Payment History</h2>
        {payments.length === 0 ? (
          <p>No payments yet.</p>
        ) : (
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {payments.map((payment) => (
              <li
                key={payment.id}
                style={{
                  padding: '10px',
                  marginBottom: '10px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  backgroundColor: '#f9f9f9',
                }}
              >
                <Link to={`/confirmation/${payment.id}`}>
                  <strong>Payment ID:</strong> {payment.id.substring(0, 8)}...
                  {' | '}
                  <strong>Amount:</strong> ${parseFloat(payment.price).toFixed(2)}
                  {' | '}
                  <strong>Date:</strong> {new Date(payment.createdAt).toLocaleDateString()}
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default Payments;
