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
    const data = await authService.login(email, password);
    const decoded = jwtDecode(data.token);
    localStorage.setItem('token', data.token);
    setUser({
      userId: decoded.userId,
      email: decoded.sub,
      role: decoded.role,
      token: data.token,
    });
    return data;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const register = async (email, password, role) => {
    return authService.register(email, password, role);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, register, loading }}>
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
