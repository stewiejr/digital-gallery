import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import { useAuth } from './AuthContext';
import './Dashboard.css'; // Import the CSS file for styling

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  if (!user) {
    return null;
  }
  return (
    <div className="dashboard-container">
      <nav className="dashboard-nav">
        <Link to={`profile/${user.id}`} className="dashboard-tab">Profile</Link>
        <Link to="payments" className="dashboard-tab">Payments</Link>
        <Link to="gallery" className="dashboard-tab">Gallery</Link>
        <Link to="exhibitions" className="dashboard-tab">Exhibitions</Link>
        <Link to="search" className="dashboard-tab">Search</Link>
      </nav>
      <div className="dashboard-content">
        <Outlet />
      </div>
    </div>
  );
};

export default Dashboard;