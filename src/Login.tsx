import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api, setAuth } from './api';
import { useAuth } from './AuthContext';
import './Login.css';  // Import the CSS file

function Login() {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');

  const [errorMessage, setErrorMessage] = useState(''); // State for error message
  const navigate = useNavigate(); // Hook for navigation
  const { setUser } = useAuth();

  // Function to handle user login
  const handleLogin = async () => {
    try {
      const response = await api.post('/auth/login', {
        identifier,
        password,
      });

      const { token, user } = response.data;
      setAuth(token, user);
      setUser(user);
      navigate('/dashboard');
    } catch (error) {
      console.error('Error checking user data:', error);
      setErrorMessage('Error logging in. Please try again.');

    }
  };

  return (
    <div className="login-background">
      <div className="login-container">
        <h1>Login</h1>
        <form>
          <input
            type="text"
            placeholder="Email or Username"
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="button" className="login-button" onClick={handleLogin}>Login</button>
        </form>
        {errorMessage && (
          <div>
            <p style={{ color: 'red' }}>{errorMessage}</p>
            <Link to="/register">
              <button type="button" className="register-button-small">Register</button>
            </Link>
          </div>
        )}
      </div>

    </div>
  );
}

export default Login;