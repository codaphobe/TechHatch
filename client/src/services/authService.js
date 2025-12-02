import api from './api';

export const authService = {
  register: async (email, password, role) => {
    const response = await api.post('/api/v1/auth/register', {
      email,
      password,
      role,
    });
    return response.data;
  },

  login: async (email, password) => {
    const response = await api.post('/api/v1/auth/login', {
      email,
      password,
    });
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await api.get('/api/v1/auth/me');
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
  },
};
