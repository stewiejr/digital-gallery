import React, { useState } from 'react';
import './Exhibitions.css';
import { api } from './api';
import { useNavigate } from 'react-router-dom';
import ArtworkSelection from './ArtworkSelection';
import { useAuth } from './AuthContext';

function CreateExhibition () {
    const { user } = useAuth();
    const [exhibitionName, setExhibitionName] = useState('');
    const [exhibitionDescription, setExhibitionDescription] = useState('');
    const [selectedArtworks, setSelectedArtworks] = useState<string[]>([]);
    const navigate = useNavigate();

    const handleArtworkSelectionChange = (selected: string[]) => {
        setSelectedArtworks(selected);
        console.log("Selected artworks for the exhibition:", selected);
    };

    const createExhibition = async () => {
        if (!user) return;
        const response = await api.post('/exhibitions', {
            exhibitionName,
            description: exhibitionDescription,
            artworkIds: selectedArtworks,
        });
        navigate(`/exhibitions/${response.data.id}`);
    };

    return (
        <div className='exhibitions-container'>
            <h1>Create Exhibition</h1>
            <h3>Exhibition Name:</h3>
            <input
                type="text"
                placeholder="Exhibition Name"
                value={exhibitionName}
                onChange={(e) => setExhibitionName(e.target.value)}
                required
            /> <br /> <br />
            <h3>Exhibition Description</h3>
            <textarea
                placeholder="Add a Description"
                value={exhibitionDescription}
                onChange={(e) => setExhibitionDescription(e.target.value)}
                required
            /> <br /> <br />
            <ArtworkSelection onSelectionChange={handleArtworkSelectionChange} />
            <p>Selected Artworks: {selectedArtworks.join(', ')}</p>
            <button type="button" className="button" onClick={createExhibition}>Create Exhibition</button>
        </div>
    )
}

export default CreateExhibition;

