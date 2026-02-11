import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from './api';
import { useAuth } from './AuthContext';

function Confirmation() {
    const { id } = useParams<{ id: string }>();
    const [payment, setPayment] = useState<any | null>(null);
    const [error, setError] = useState<string | null>(null);
    const { user } = useAuth();

    const fetchPayment = async () => {
        try {
            // Reference to the payments subcollection within the user's document
            if (!id || !user) return;
            const response = await api.get(`/payments/users/${user.id}/${id}`);
            setPayment(response.data);
        } catch (err) {
            console.error("Error fetching payments:", err);
            setError("Failed to fetch payments. Please try again later.");
        }
    };

    useEffect(() => {
        fetchPayment();
    }, [id]);

    return (
        <div>
            <h1>Payment Confirmation</h1>
            <h3>Thank you for your purchase!</h3>
            <p>Payment ID: {id}</p>
            {error && <p>{error}</p>}
            {payment && (
                <div>
                    <p>Date: {payment.createdAt ? new Date(payment.createdAt).toLocaleDateString() : 'Date not available'}</p>
                    <p>Purchase:  
                    {payment.purchaseArtworkIds && payment.purchaseArtworkIds.length > 0 ? (
                        <ul>
                        {payment.purchaseArtworkIds.map((itemId) => (
                            <li key={itemId}>
                                <Link to={`/artwork/${itemId}`}>Artwork ID: {itemId}</Link>
                            </li>
                        ))}
                        </ul>
                    ) : (
                        <span> No items in the purchase.</span>
                    )}
                    </p>
                    <p>Amount: ${Number(payment.price).toFixed(2)}</p>
                </div>
            )}
        </div>
    );
} 

export default Confirmation;