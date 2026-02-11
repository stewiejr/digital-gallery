import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from './CartContent';
import { api } from './api';
import { useAuth } from './AuthContext';
import PaymentForm from './PaymentForm';

const Cart: React.FC = () => {
    const { cartItems, removeFromCart, clearCart } = useCart();
    const navigate = useNavigate();
    const [totalPrice, setTotalPrice] = useState<number>(0);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    const clientId = import.meta.env.VITE_PAYPAL_CLIENT_ID || 'test';
    const { user } = useAuth();

    const handleContinueShopping = () => {
        navigate('/dashboard/gallery');
    };

    const handlePaymentSuccess = (paymentId: string) => {
        clearCart();
        navigate(`/confirmation/${paymentId}`);
    };

    const calculatePrice = async () => {
        let total = 0;
        try {
            const responses = await Promise.all(
                cartItems.map((itemId) => api.get(`/artworks/${itemId}`))
            );
            responses.forEach((response) => {
                const price = response.data?.price || 0;
                total += Number(price);
            });

            setTotalPrice(total);
        } catch (err) {
            console.error('Error fetching artwork prices:', err);
            setError('Error calculating price');
        }
    };

    useEffect(() => {
        calculatePrice();
    }, [cartItems]);

    return (
        <div className="cart">
            <h1>Your Cart</h1>
            {error && <div style={{ color: 'red', marginBottom: '15px' }}>{error}</div>}
                
                {cartItems.length === 0 ? (
                    <p>Your cart is empty</p>
                ) : (
                    <>
                        <ul>
                            {cartItems.map((itemId) => (
                                <li key={itemId}>
                                    <Link to={`/artwork/${itemId}`}>Artwork ID: {itemId}</Link>
                                    <button onClick={() => removeFromCart(itemId)}>Remove</button>
                                </li>
                            ))}
                        </ul>
                        <h4>Total Price: ${totalPrice.toFixed(2)}</h4>
                    </>
                )}
                
                <button onClick={handleContinueShopping} style={{ marginBottom: '20px' }}>
                    Continue Shopping
                </button>

                {cartItems.length > 0 && !showPaymentForm && (
                    <>
                        <h4>Checkout with Card:</h4>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '15px', marginBottom: '20px' }}>
                            <button 
                                onClick={() => setShowPaymentForm(true)}
                                style={{
                                    padding: '12px',
                                    fontSize: '16px',
                                    backgroundColor: '#28a745',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    cursor: 'pointer',
                                    fontWeight: 'bold',
                                }}
                            >
                                💳 Proceed to Checkout
                            </button>
                        </div>
                    </>
                )}

                {showPaymentForm && user && (
                    <div style={{ marginTop: '30px', marginBottom: '20px' }}>
                        <PaymentForm 
                            onPaymentSuccess={handlePaymentSuccess}
                            userId={user.id}
                            totalPrice={totalPrice}
                            cartItems={cartItems}
                        />
                        <button
                            onClick={() => setShowPaymentForm(false)}
                            style={{
                                marginTop: '15px',
                                padding: '8px 15px',
                                backgroundColor: '#6c757d',
                                color: 'white',
                                border: 'none',
                                borderRadius: '4px',
                                cursor: 'pointer',
                            }}
                        >
                            ← Back to Checkout Options
                        </button>
                    </div>
                )}
            </div>
        );
    };

    export default Cart;