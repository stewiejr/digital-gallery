import React, { useState, useEffect } from 'react';
import { api } from './api';

interface PaymentMethod {
  id: string;
  cardholderName: string;
  cardNumber: string;
  expiryDate: string;
  isDefault: boolean;
}

interface PaymentFormProps {
  onPaymentSuccess: (paymentId: string) => void;
  userId: string;
  totalPrice: number;
  cartItems: string[];
}

const PaymentForm: React.FC<PaymentFormProps> = ({ onPaymentSuccess, userId, totalPrice, cartItems }) => {
  const [savedMethods, setSavedMethods] = useState<PaymentMethod[]>([]);
  const [selectedMethod, setSelectedMethod] = useState<string>('');
  const [useNewCard, setUseNewCard] = useState(true);
  
  // New card form
  const [cardholderName, setCardholderName] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [expiryDate, setExpiryDate] = useState('');
  const [cvv, setCvv] = useState('');
  const [saveCard, setSaveCard] = useState(false);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    // Fetch saved payment methods
    const fetchMethods = async () => {
      try {
        const response = await api.get(`/payment-methods/users/${userId}`);
        setSavedMethods(response.data);
        if (response.data.length > 0) {
          setUseNewCard(false);
          setSelectedMethod(response.data[0].id);
        }
      } catch (err) {
        console.error('Error fetching payment methods:', err);
      }
    };
    
    fetchMethods();
  }, [userId]);

  const validateCard = () => {
    if (!cardNumber.replace(/\s/g, '').match(/^\d{13,19}$/)) {
      setError('Invalid card number');
      return false;
    }
    if (!expiryDate.match(/^\d{2}\/\d{2}$/)) {
      setError('Expiry date must be MM/YY format');
      return false;
    }
    if (!cvv.match(/^\d{3,4}$/)) {
      setError('CVV must be 3-4 digits');
      return false;
    }
    if (!cardholderName.trim()) {
      setError('Cardholder name is required');
      return false;
    }
    return true;
  };

  const handlePayment = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let paymentMethodId = selectedMethod;

      // If using new card, save it first
      if (useNewCard) {
        if (!validateCard()) {
          setLoading(false);
          return;
        }

        const methodResponse = await api.post(`/payment-methods/users/${userId}`, {
          cardholderName,
          cardNumber: cardNumber.replace(/\s/g, ''),
          expiryDate,
          cvv,
          isDefault: saveCard && savedMethods.length === 0,
        });

        paymentMethodId = methodResponse.data.id;

        if (saveCard) {
          setSavedMethods([...savedMethods, methodResponse.data]);
        }
      }

      // Create payment
      const paymentResponse = await api.post(`/payments/users/${userId}`, {
        price: totalPrice,
        purchaseArtworkIds: cartItems,
        paymentMethodId,
      });

      if (paymentResponse.data && paymentResponse.data.id) {
        // Reset form
        setCardholderName('');
        setCardNumber('');
        setExpiryDate('');
        setCvv('');
        setSaveCard(false);

        // Call success handler with payment ID
        onPaymentSuccess(paymentResponse.data.id);
      } else {
        throw new Error('Invalid payment response - missing payment ID');
      }
    } catch (err: any) {
      console.error('Payment error:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Payment failed. Please try again.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

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

  return (
    <div className="payment-form" style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
      <h3>Payment Information</h3>
      
      {error && <p style={{ color: 'red', marginBottom: '15px' }}>{error}</p>}

      {/* Saved methods option */}
      {savedMethods.length > 0 && (
        <div style={{ marginBottom: '20px' }}>
          <label>
            <input
              type="radio"
              checked={!useNewCard}
              onChange={() => setUseNewCard(false)}
            />
            {' '}Use saved card
          </label>
          
          {!useNewCard && (
            <select
              value={selectedMethod}
              onChange={(e) => setSelectedMethod(e.target.value)}
              style={{ display: 'block', marginTop: '10px', padding: '8px', width: '100%' }}
            >
              {savedMethods.map((method) => (
                <option key={method.id} value={method.id}>
                  {method.cardNumber} - {method.cardholderName}
                </option>
              ))}
            </select>
          )}
        </div>
      )}

      {/* New card option */}
      {savedMethods.length > 0 && (
        <label style={{ marginBottom: '20px', display: 'block' }}>
          <input
            type="radio"
            checked={useNewCard}
            onChange={() => setUseNewCard(true)}
          />
          {' '}Use new card
        </label>
      )}

      {/* Card form */}
      {useNewCard && (
        <form onSubmit={handlePayment}>
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
                checked={saveCard}
                onChange={(e) => setSaveCard(e.target.checked)}
                disabled={loading}
              />
              {' '}Save this card for future purchases
            </label>
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{
              width: '100%',
              padding: '12px',
              backgroundColor: loading ? '#ccc' : '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontSize: '16px',
              fontWeight: 'bold',
            }}
          >
            {loading ? 'Processing...' : `Pay $${totalPrice.toFixed(2)}`}
          </button>
        </form>
      )}

      {/* Saved card checkout */}
      {!useNewCard && savedMethods.length > 0 && (
        <button
          onClick={handlePayment}
          disabled={loading}
          style={{
            width: '100%',
            padding: '12px',
            backgroundColor: loading ? '#ccc' : '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
            fontSize: '16px',
            fontWeight: 'bold',
          }}
        >
          {loading ? 'Processing...' : `Pay with saved card - $${totalPrice.toFixed(2)}`}
        </button>
      )}

      <p style={{ marginTop: '20px', fontSize: '12px', color: '#666' }}>
        Test card: 4242 4242 4242 4242 | Any future date | Any 3-digit CVV
      </p>
    </div>
  );
};

export default PaymentForm;
