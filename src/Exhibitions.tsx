import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Exhibitions.css';
import ArtworkSelection from './ArtworkSelection';
import { api } from './api';

const Exhibitions: React.FC = () => {
    const [exhibitions, setExhibitions] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string>('');

    interface ExhibitionData {
        id: string;
        exhibitionName: string;
        creatorName: string;
        description?: string;
        artworkIds?: string[];
        firstArtwork?: {
            title: string;
            imageUrl: string;
        };
    }

    // Fetch exhibitions from Firestore
    const fetchExhibitions = async () => {
        try {
            const exhibitionResponse = await api.get('/exhibitions');
            const exhibitionList = await Promise.all(
                (exhibitionResponse.data as ExhibitionData[]).map(async (exhibitionData) => {
                    const firstArtworkId = exhibitionData.artworkIds?.[0];
                    if (firstArtworkId) {
                        const artworkResponse = await api.get(`/artworks/${firstArtworkId}`);
                        exhibitionData.firstArtwork = {
                            title: artworkResponse.data.title,
                            imageUrl: artworkResponse.data.imageUrl,
                        };
                    }
                    return exhibitionData;
                })
            );
            setExhibitions(exhibitionList);
        } catch (err) {
            setError('Error fetching exhibitions');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchExhibitions();
    }, []);

    if (loading) {
        return <div>Loading exhibitions...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    return (
        <div className='exhibitions-container'>
            <h1>Exhibitions</h1>
            <Link to="create" className="exhibition-link"><button type="button" className="button">Create Exhibition</button></Link> <br /> <br />
            <div className="exhibition-list">
                {exhibitions.map((exhibition) => (
                    <Link to={`/exhibitions/${exhibition.id}`}>
                        <div className="exhibition-item" key={exhibition.id}>
                            <h4>{exhibition.exhibitionName}</h4>
                            <p>Creator: {exhibition.creatorName}</p>
                            <p>Description: {exhibition.description}</p>     
                                {exhibition.firstArtwork && (
                                    <img src={exhibition.firstArtwork.imageUrl} alt={exhibition.firstArtwork.title} className="first-artwork" />
                                )}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    )
}

export default Exhibitions;



