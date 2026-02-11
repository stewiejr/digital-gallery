import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import './Exhibition.css';
import { api } from './api';
function Exhibition() {
    const { id } = useParams<{ id: string }>();
    const [exhibitionName, setExhibitionName] = useState('');
    const [exhibitionUser, setExhibitionUser] = useState('');
    const [exhibitionUID, setExhibitionUID] = useState('');
    const [exhibitionDescription, setExhibitionDescription] = useState('');
    const [exhibitionProfilePicture, setExhibitionProfilePicture] = useState('');
    const [artworkArray, setArtworkArray] = useState<any[]>([]);
    const [artwork, setArtwork] = useState<any>(null);
    const [error, setError] = useState('');
    const fetchArtwork = async (aid) => {
        const response = await api.get(`/artworks/${aid}`);
        const artworkData = response.data;
        let artistProfilePicture = '';
        if (artworkData.artistId) {
            const artistResponse = await api.get(`/users/${artworkData.artistId}`);
            artistProfilePicture = artistResponse.data.profilePicture;
        }
        return { ...artworkData, artistProfilePicture, id: aid };
    };

    const getExhibition = async () => {
        try {
            if (id) {
                const response = await api.get(`/exhibitions/${id}`);
                const exhibitionData = response.data;
                const creatorResponse = await api.get(`/users/${exhibitionData.creatorId}`);
                setExhibitionUser(creatorResponse.data.displayName);
                setExhibitionProfilePicture(creatorResponse.data.profilePicture);
                setExhibitionUID(exhibitionData.creatorId);
                setExhibitionName(exhibitionData.exhibitionName);
                setExhibitionDescription(exhibitionData.description);
                const artworkPromises = exhibitionData.artworkIds.map((aid) => fetchArtwork(aid));
                const artworks = await Promise.all(artworkPromises);
                setArtworkArray(artworks.filter((artwork) => artwork !== null));
                console.log("Artworks Array:", artworks.filter((artwork) => artwork !== null));
            }
        } catch (error) {
            console.error("Error fetching document:", error);
        }    
    }

    useEffect(() => {
        getExhibition();
    }, [id]);

    return (
        <div>
            <h1>{exhibitionName}</h1>
            <h3>Description: <br /> </h3> <p>{exhibitionDescription}</p>
            <h3>Creator: <br /> </h3> <img src={exhibitionProfilePicture} className="profile-exhibition" />  
                <Link to={`/profile/${exhibitionUID}`}> 
                    {exhibitionUser} 
                </Link> <br />
            <div className="artwork-list">
                {artworkArray.map((artwork) => (
                    <div className="artwork-detail" key={artwork.id}>
                        <h1>{artwork.title}</h1>
                        <Link to={`/artwork/${artwork.id}`}><img src={artwork.imageUrl} alt={artwork.title} /></Link>
                        <p>{artwork.description}</p>
                        <div className="artist-detail">
                            <h4>Artist: <br /> <img src={artwork.artistProfilePicture} className="profile-artist" />  
                                <Link to={`/profile/${artwork.artistId}`}> 
                                    {artwork.artistName} 
                                </Link>
                            </h4>
                        </div>
                        <hr />
                    </div>
                ))}
            </div>
        </div>
    )
}

export default Exhibition;