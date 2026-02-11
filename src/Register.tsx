import React, { useState } from 'react';
import { api, setAuth } from './api';
import { useAuth } from './AuthContext';
import { useNavigate } from 'react-router-dom';
import './Register.css';  // Import the CSS file

function Register() {
  const [email, setEmail] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [successMessage, setSuccessMessage] = useState(''); // State for success message
  const [errorMessage, setErrorMessage] = useState(''); // State for error message
  const navigate = useNavigate();
  const { setUser } = useAuth();

  // Function to handle user registration
  const handleRegister = async () => {
    try {
      const response = await api.post('/auth/register', {
        email,
        displayName,
        username,
        password,
      });

      const { token, user } = response.data;
      setAuth(token, user);
      setUser(user);

      setSuccessMessage('User account successfully created!'); // Set success message
      setErrorMessage(''); // Clear error message
      setEmail('');
      setDisplayName('');
      setUsername(''); // Clear input fields
      setPassword(''); // Clear input fields

      navigate('/dashboard');
    } catch (error: any) {
      console.error('Error saving user data:', error);
      if (error?.response?.status === 409) {
        setErrorMessage(error.response?.data?.message || 'Email or username already exists.');
      } else {
        setErrorMessage('An unexpected error occurred. Please try again.');
      }
    }
  };

  return (
    <div className="register-background">
      <div className="register-container">
        <h1>Register</h1>
        <form>
          <input
            type="text"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Name"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="button" className="button" onClick={handleRegister}>Register</button>
        </form>
        {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
        {successMessage && <p>{successMessage}</p>}
      </div>
    </div>
  );
}

export default Register;