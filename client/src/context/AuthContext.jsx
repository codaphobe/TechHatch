import { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';
import { isTokenExpired } from '../utils/jwtUtils';
import toast from 'react-hot-toast';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem('token');
      const userId = localStorage.getItem('userId');
      const email = localStorage.getItem('email');
      const role = localStorage.getItem('role');
      // Check if token exists and is not expired
      if (token && !isTokenExpired(token) && userId && email && role) {
        setUser({ userId, email, role, token });
        try {
          const userData = await authService.getCurrentUser();
          setUser({
            userId: userData.userId || userId,
            email: userData.email || email,
            role: userData.role || role,
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
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('email', data.email);
    localStorage.setItem('role', data.role);
    setUser({
      userId: data.userId,
      email: data.email,
      role: data.role,
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
