import {jwtDecode} from 'jwt-decode';

/**
 * Checks if a JWT token is expired
 * @param {string} token - JWT token
 * @returns {boolean} - true if expired or invalid, false if valid
 */
export const isTokenExpired = (token) => {
  if (!token) return true;
  
  const {exp} = jwtDecode(token);
  if (!exp) return true;
  
  // exp is in seconds, Date.now() is in milliseconds
  const currentTime = Date.now() / 1000;
  return exp < currentTime;
};
