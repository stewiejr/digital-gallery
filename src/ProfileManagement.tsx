import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './ProfileManagement.css'; // Import the CSS file for styling
import { api } from './api';
import { useAuth } from './AuthContext';

const ProfileManagement: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [profilePicture, setProfilePicture] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const clientId = import.meta.env.VITE_IMGUR_CLIENT_ID;
  const { user, setUser } = useAuth();
  const userId = user?.id;
  const fetchUserData = async () => {
    try {
        if (!userId) return;
        const response = await api.get(`/users/${userId}`);
        const userData = response.data;
        setName(userData.displayName);
        setUsername(userData.username);
        setEmail(userData.email);
        setPassword('');
        setProfilePicture(userData.profilePicture);
        return userData;
    } catch (error) {
        console.error("Error fetching document:", error);
    } finally {
      setLoading(false);
    }
}

  const updateUserData = async () => {
    if (file) {
      await handleProfilePictureUpload();
    }

    try {
      if (!userId) return;

      const response = await api.patch(`/users/${userId}`, {
        displayName: name,
        username,
        email,
        profilePicture,
        password: password || undefined,
      });

      setUser(response.data);
      setError('');
    } catch (error) {
      console.error("Error updating document:", error);
      if (error?.response?.status === 409) {
        setError(error.response?.data?.message || 'Username or email already exists.');
      } else {
        setError("An error occurred while updating profile.");
      }
    }
  };

  const handleProfilePictureUpload = async () => {
    if (!file) return;
    const formData = new FormData();
    formData.append('image', file);
      try {
        // Make API call to Imgur
        const response = await axios.post('https://api.imgur.com/3/upload', formData, {
            headers: {
                'Authorization': 'Client-ID ' + clientId,
                'Content-Type': 'multipart/form-data',
            },
        });

        const imageUrl = response.data.data.link; // Get the image URL from the response

        console.log('Uploaded Image URL:', imageUrl);

        setProfilePicture(imageUrl); // Update state with the new profile picture URL
    } catch (error) {
        console.error('An unexpected error occurred:', error);
        setError("An unexpected error occurred.");
      }
};

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  useEffect(() => {
      fetchUserData();
  }, []);


  return (
    <div className="profilemanagement-container">
      <h1>Profile Management</h1>
      {loading ? (
        <div className="loading">Loading...</div> // Show loading message
      ) : (
        <>
          {id == userId ? (
          <>
          <div>
            <label>Profile Picture:</label> <br />
            <img src={profilePicture} alt="Profile" className="profile-img" /> <br /> <br />
            <input type="file" onChange={handleFileChange} />
          </div> <br />
          <div>
            <label>Name: </label> <br />
            <input value={name} onChange={(e) => setName(e.target.value)} />
          </div>
          <div>
            <label>Username: </label> <br />
            @<input value={username} onChange={(e) => setUsername(e.target.value)} />
          </div>
          <div>
            <label>Email: </label> <br />
            <input value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div>
            <label>New Password (optional): </label> <br />
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </div> <br />

          <button type="button" className="button" onClick={updateUserData}>Update Profile</button>
          </> ):
          (<><p>Sorry, You are not allowed to access this page.</p></>)}
        </>
      )}
    </div>
  );
};

export default ProfileManagement;