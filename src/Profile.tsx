import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from './api';
import './Profile.css';
import { useAuth } from './AuthContext';

const Profile: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [name, setName] = useState("");
    const [username, setUsername] = useState("");
    const [profilePicture, setProfilePicture] = useState("");
    const [artwork, setArtwork] = useState<any[]>([]);
    const [comments, setComments] = useState<any[]>([]);
    const [commentText, setCommentText] = useState("");
    const [loading, setLoading] = useState(true);
    const [notNull, setNotNull] = useState(true);
    const { user } = useAuth();

    const fetchUserData = async () => {
        try {
            const response = await api.get(`/users/${id}`);
            const userData = response.data;
            setName(userData.displayName);
            setUsername(userData.username);
            setProfilePicture(userData.profilePicture);
            fetchArtwork();
            fetchComments();
            return userData;
        } catch (error) {
            console.error("Error fetching document:", error);
        } finally {
            setLoading(false);
        }
    }
    const fetchArtwork = async () => {
        const userId = id;
        if (!userId) { // Check for undefined userId before fetching artwork
            console.error('No user ID provided for artwork');
            return;
        }
        const response = await api.get(`/artworks/by-user/${userId}`);
        setArtwork(response.data);
    };

    const fetchComments = async () => {
        const userId = id;
        if (!userId) { // Check for undefined userId before fetching artwork
            console.error('No user ID provided for artwork');
            return;
        }
        const response = await api.get(`/comments/users/${userId}`);
        setComments(response.data);
    };

    const handleCommentSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!id) {
            console.log('No user ID provided.');
            return;
        }    

        try {
            if (!user) {
                console.error('Not authenticated');
                return;
            }

            await api.post(`/comments/users/${id}`, {
                text: commentText,
            });
            // Reset form fields
            setCommentText('');

            // Refresh comments
            const response = await api.get(`/comments/users/${id}`);
            setComments(response.data);
        } catch (error) {
            console.error('Error adding comment:', error);
        }
    };

    useEffect(() => {
        fetchUserData();
    }, [id]);

    return (
        <div className="profile-container">
        <h1>Profile</h1>
        {loading ? (
        <div className="loading">Loading...</div> // Show loading message
        ) : (
            <>
                {notNull ? (
                <>
                <img src={profilePicture} alt="Profile" className="profile-img" />
                <h3>{name}</h3>
                <p>@{username}</p>
                {user && user.id == id ? (
                    <>
                        <Link to="./profile-management" ><button type="button" className="button">Edit Profile</button></Link> <br /><br />
                    </>) : 
                    (<></>)}
                <h2>Artwork</h2>
                <hr />
                <div className="profile-artwork-list">
                    {artwork.map((artworkItem) => (
                        <div key={artworkItem.id} className="profile-artwork">
                            <Link to={`/artwork/${artworkItem.id}`}>
                                <img
                                    src={artworkItem.imageUrl}
                                    alt={artworkItem.title}
                                    className="profile-artwork-image"
                                />
                            </Link>
                        </div>
                    ))}
                </div> <br />
                <h2>Comments</h2>
                <hr />
                <div className="comments">
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
                </>) :
                (<><p>Sorry, the user you're searching for cannot be found.</p></>)}
            </>
        )}
        </div>
    );
};

export default Profile;
