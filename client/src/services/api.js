import axios from 'axios';
import { isTokenExpired } from '../utils/jwtUtils';

const API_BASE_URL = import.meta.env.VITE_API_URL;
const RETRY_LIMIT = 3

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const clearAuthStorage = () => {
  localStorage.removeItem('token');
};

const redirectToLogin = () => {
  if (window.location.pathname !== '/login') {
    window.location.href = '/login';
  }
};

// Request interceptor to add auth token and check expiration
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (!token) {
      return config;
    }

    if (isTokenExpired(token)) {
      clearAuthStorage();
      redirectToLogin();
      return Promise.reject(new Error('Token expired'));
    }

    config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    // Handle 401 Unauthorized - clear auth and redirect
    if (error.response?.status === 401) {
      clearAuthStorage();
      redirectToLogin();
      return Promise.reject(error);
    }
    
    // Retry logic for server errors (500+) or network errors
    if (!error.response || error.response.status >= 500){
    
      const config = error.config;
      
      if(!config._retryCount) {
        config._retryCount=0;
      }
      
      if(config._retryCount < RETRY_LIMIT){
        config._retryCount+=1;
        
        const delay = (retryCount) => 1000 * Math.pow(2,retryCount);
        
        console.warn(
          `Retrying ${config._retryCount}/${RETRY_LIMIT} in ${delay(config._retryCount) / 1000}s...`
        );
        
        await new Promise((resolve) => setTimeout(resolve,delay(config._retryCount)))
        
        return api(config);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
