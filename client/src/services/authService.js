import api from './api';

export const authService = {
  register: async (email, password, role) => {
    const response = await api.post('/api/v1/auth/register', {
      email,
      password,
      role,
    });
    // Response format: { success, email, message, otpSent, error }
    return response.data;
  },

  login: async (email, password) => {
    const response = await api.post('/api/v1/auth/login', {
      email,
      password,
    });
    // Response format: { success, email, message, otpSent, error }
    return response.data;
  },

  verifyRegistrationOTP: async (email, otpCode) => {
    const response = await api.post('/api/v1/auth/otp/verify-registration', {
      email,
      otpCode,
      otpPurpose: 'REGISTRATION',
    });
    return response.data;
  },

  verifyLoginOTP: async (email, otpCode) => {
    const response = await api.post('/api/v1/auth/otp/verify-login', {
      email,
      otpCode,
      otpPurpose: 'LOGIN',
    });
    return response.data;
  },

  resendOTP: async (email, purpose) => {
    const response = await api.post('/api/v1/auth/otp/resend', {
      email,
      otpPurpose: purpose,
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
