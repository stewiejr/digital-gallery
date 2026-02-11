import React, { useState, useEffect } from 'react';
import { api } from './api';
import { Link } from 'react-router-dom';
import './Gallery.css';
import { useAuth } from './AuthContext';

interface Artwork {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  artistName: string;
  artistUsername: string;
  artistId: string;
  createdAt: Date;
  price: number;
  isSold: boolean;
}

const Gallery: React.FC = () => {
  const [artworks, setArtworks] = useState<Artwork[]>([]);
  const [loading, setLoading] = useState(true);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [price, setPrice] = useState(0);
  const [error, setError] = useState('');

  const { user } = useAuth();

  useEffect(() => {
    const fetchArtworks = async () => {
      try {
        const response = await api.get('/artworks');
        const artworksData = response.data as Artwork[];
        setArtworks(artworksData);
      } catch (error) {
        console.error('Error fetching artwork data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchArtworks();
  }, []);

  const handlePriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    // Ensure the value is a number and format it to 2 decimal places
    const formattedValue = parseFloat(value).toFixed(2);
    setPrice(parseFloat(formattedValue));  // Store the price as a number with 2 decimals
  };

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      if (!user) {
        setError('You must be logged in to upload artwork.');
        setLoading(false);
        return;
      }

      if (!file) {
        setError('Please select an image file.');
        setLoading(false);
        return;
      }

      const imageFileUrl = await handleArtworkUpload();
      if (!imageFileUrl) {
        setError('Failed to upload image to Imgur.');
        setLoading(false);
        return;
      }

      const response = await api.post('/artworks', {
        title,
        description,
        imageUrl: imageFileUrl,
        price: price.toString(),
      });

      const savedArtwork = response.data as Artwork;

      setTitle('');
      setDescription('');
      setPrice(0);
      setFile(null);
      setArtworks((prevArtworks) => [
        savedArtwork,
        ...prevArtworks,
      ]);

      alert('Artwork uploaded successfully!');
      setIsSidebarOpen(false);
    } catch (error: any) {
      console.error('Error uploading artwork:', error);
      setError(error.response?.data?.message || 'Error uploading artwork. Please try again.');
    } finally {
      setLoading(false);
    }
  };
  
  const handleArtworkUpload = async (): Promise<string | undefined> => {
    if (!file) {
      setError('No file selected');
      return undefined;
    }

    const formData = new FormData();
    formData.append('image', file);
    
    try {
      // Upload to backend instead of Imgur
      const response = await api.post('/upload/image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      // Backend returns relative URL like "/uploads/filename.png"
      // We'll store the full URL in the database
      const relativeUrl = response.data.url; // "/uploads/filename.png"
      const fullUrl = `http://localhost:8080${relativeUrl}`;
      
      console.log('Image uploaded, URL:', fullUrl);
      return fullUrl;
    } catch (error: any) {
      console.error('Backend upload error:', error.response?.data || error);
      setError('Failed to upload image: ' + (error.response?.data?.error || error.message));
      return undefined;
    }
  };

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setFile(event.target.files[0]);
    }
  };

  return (
    <div className="gallery">
      <h1>Artwork Gallery</h1>

      {loading ? (
        <p>Loading...</p>
      ) : (
        <div className="gallery-grid">
          {artworks.map((artwork) => (
            <div key={artwork.id} className="gallery-item-wrapper">
              <Link to={`/artwork/${artwork.id}`}>
                <div className="gallery-item">
                  <img
                    src={artwork.imageUrl}
                    alt={artwork.title}
                    className="artwork-image"
                  />
                  {artwork.isSold && (
                    <div className="sold-overlay">
                      <span className="sold-badge">SOLD</span>
                    </div>
                  )}
                </div>
                <h2 className="artwork-title">{artwork.title}</h2>
                <p className="artwork-artist">By: {artwork.artistName}</p>
              </Link>
            </div>
          ))}
        </div>
      )}

      {/* Sidebar for the upload form */}
      <div className={`sidebar ${isSidebarOpen ? 'open' : ''}`}>
        <h2>Upload New Artwork</h2>
        {error && <p style={{ color: 'red', marginBottom: '10px' }}>{error}</p>}
        <form onSubmit={handleUpload} className="upload-form">
          <input
            type="text"
            placeholder="Artwork Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            disabled={loading}
          />
          <textarea
            placeholder="Artwork Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
            disabled={loading}
          />
          Price:
          <input
            type="number"
            step="0.01"
            placeholder="Price"
            value={price}
            onChange={handlePriceChange}
            required
            disabled={loading}
          />
          <input 
            type="file" 
            accept="image/*"
            onChange={handleFileChange} 
            required
            disabled={loading}
          />
          {file && <p style={{ fontSize: '12px', marginTop: '5px' }}>Selected: {file.name}</p>}
          <button type="submit" disabled={loading}>
            {loading ? 'Uploading...' : 'Upload Artwork'}
          </button>
        </form>
      </div>

      {/* Button to toggle the sidebar */}
      <button className={`toggle-btn ${isSidebarOpen ? 'transparent' : ''}`} onClick={toggleSidebar}>
        {isSidebarOpen ? 'Close' : 'Upload'}
      </button>
    </div>
  );
}

export default Gallery;
