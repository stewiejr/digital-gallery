import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { api } from './api';
import { useCart } from './CartContent';
import './ArtworkDetail.css';
import { useAuth } from './AuthContext';
import axios from 'axios';

function ArtworkDetail() {
    const { id } = useParams<{ id: string }>();
    const [artwork, setArtwork] = useState<any>(null);
    const [profilePicture, setProfilePicture] = useState('');
    const [comments, setComments] = useState<any[]>([]);
    const [commentText, setCommentText] = useState('');
    const [error, setError] = useState('');
    const [deleteStatus, setDeleteStatus] = useState('');
    const [isDeleting, setIsDeleting] = useState(false);
    const { addToCart } = useCart();
    const { user } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!id) {
            console.log('No artwork ID provided.');
            return;
        }    

        const fetchArtwork = async () => {
            const response = await api.get(`/artworks/${id}`);
            const artworkData = response.data;
            setArtwork(artworkData);
            if (artworkData.artistId) {
                const userResponse = await api.get(`/users/${artworkData.artistId}`);
                setProfilePicture(userResponse.data.profilePicture);
            }
        };

        const fetchComments = async () => {
            const response = await api.get(`/comments/artworks/${id}`);
            setComments(response.data);
        };

        fetchArtwork();
        fetchComments();
    }, [id]);

    const handleCommentSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!id) {
            console.log('No artwork ID provided.');
            return;
        }    

        try {
            if (!user) {
                setError('You must be logged in to comment.');
                return;
            }

            await api.post(`/comments/artworks/${id}`, {
                text: commentText,
            });
            // Reset form fields
            setCommentText('');

            // Refresh comments
            const response = await api.get(`/comments/artworks/${id}`);
            setComments(response.data);
        } catch (error) {
            console.error('Error adding comment:', error);
        }
    };

    const handleDeleteArtwork = async () => {
        if (!id || !user) {
            return;
        }

        setError('');
        setDeleteStatus('');

        const confirmed = window.confirm('Delete this artwork? This cannot be undone.');
        if (!confirmed) {
            return;
        }

        try {
            setIsDeleting(true);
            setDeleteStatus('Deleting artwork...');
            await api.delete(`/artworks/${id}`);
            setDeleteStatus('Artwork deleted. Redirecting...');
            navigate('/dashboard/gallery');
        } catch (deleteError) {
            console.error('Error deleting artwork:', deleteError);
            if (axios.isAxiosError(deleteError)) {
                const status = deleteError.response?.status;
                const serverMessage = deleteError.response?.data?.message || deleteError.response?.data;
                const fallback = status ? `Failed to delete artwork (status: ${status}).` : 'Failed to delete artwork.';
                setError(typeof serverMessage === 'string' ? serverMessage : fallback);
            } else {
                setError('Failed to delete artwork.');
            }
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <div className="artwork-detail">
            {artwork && (
                <>
                    <h1>{artwork.title}</h1>
                    <img src={artwork.imageUrl} alt={artwork.title} />
                    <p>{artwork.description}</p>
                    <div className="artist-detail">
                        <h4>Artist: <br /> <img src={profilePicture} className="profile-artist" />  
                            <Link to={`/profile/${artwork.artistId}`}> 
                                {artwork.artistName} 
                            </Link>
                        </h4>
                    </div>

                    <br></br>
                    <hr></hr>
                    <h3>${Number(artwork.price).toFixed(2)}</h3>
                    {artwork.isSold ? (
                        <div style={{ padding: '12px', backgroundColor: '#dc3545', color: 'white', borderRadius: '4px', fontWeight: 'bold', marginTop: '10px' }}>
                            This artwork has been sold
                        </div>
                    ) : (
                        id ? (<button onClick={() => addToCart(id)}>Send to Cart</button>) : (<></>)
                    )}

                    {error && (
                        <div style={{ marginTop: '10px', color: '#dc3545', fontWeight: 'bold' }}>
                            {error}
                        </div>
                    )}

                    {deleteStatus && (
                        <div style={{ marginTop: '10px', color: '#0f5132', fontWeight: 'bold' }}>
                            {deleteStatus}
                        </div>
                    )}

                    {user && artwork.artistId === user.id && (
                        <button
                            className="delete-artwork-button"
                            onClick={handleDeleteArtwork}
                            type="button"
                            disabled={isDeleting}
                        >
                            {isDeleting ? 'Deleting...' : 'Delete Artwork'}
                        </button>
                    )}

                    <br></br>
                    <hr></hr>
                    <h2>Comments</h2>
                    <div className="comments-section">
                        {comments.map((comment) => (
                            <div key={comment.id} className="comment">
                                <Link to={`/profile/${comment.authorId}`}> 
                                    <span>
                                        <strong>{comment.authorName}@{comment.authorUsername}:</strong> 
                                    </span>
                                </Link>
                                <p>{comment.text}</p>
                            </div>
                        ))}
                    </div>

                    <form onSubmit={handleCommentSubmit}>
                        <textarea
                            placeholder="Add a comment"
                            value={commentText}
                            onChange={(e) => setCommentText(e.target.value)}
                            required
                        />
                        <button type="submit">Submit Comment</button>
                    </form>
                </>
            )}
        </div>
    );
}

export default ArtworkDetail;
