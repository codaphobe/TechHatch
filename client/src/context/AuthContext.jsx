import { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';
import { isTokenExpired } from '../utils/jwtUtils';
import toast from 'react-hot-toast';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem('token');
      // Check if token exists and is not expired
      if (token && !isTokenExpired(token)) {
        try {
          const decoded = jwtDecode(token);
          setUser({
            userId: decoded.userId,
            email: decoded.sub,
            role: decoded.role,
            token: token,
          });
        } catch (error) {
          // Token might be invalid on server side, clear everything
          if (error.response?.status === 401){
            authService.logout();
            setUser(null);
          }
          toast.loading("Reconnecting....", {id: "reconnecting"} )
        }
      } else {
        // Token expired or missing, clear everything
        if (token) {
          authService.logout();
        }
        setUser(null);
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (email, password) => {
    // Now only sends OTP, doesn't authenticate
    const data = await authService.login(email, password);
    return data;
  };

  const verifyLoginOTP = async (email, otpCode) => {
    const data = await authService.verifyLoginOTP(email, otpCode);
    const decoded = jwtDecode(data.token);
    localStorage.setItem('token', data.token);
    const userData = {
      userId: decoded.userId,
      email: decoded.sub,
      role: decoded.role,
      token: data.token,
    };
    setUser(userData);
    // Return user data with role for navigation logic
    return {
      ...data,
      role: decoded.role,
    };
  };

  const register = async (email, password, role) => {
    // Now only sends OTP, doesn't create user session
    return authService.register(email, password, role);
  };

  const verifyRegistrationOTP = async (email, otpCode) => {
    // Verifies OTP and returns user data, but doesn't authenticate
    const data = await authService.verifyRegistrationOTP(email, otpCode);
    return data;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      register, 
      verifyLoginOTP,
      verifyRegistrationOTP,
      loading 
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
