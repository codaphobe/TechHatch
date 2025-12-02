import api from './api';

export const profileService = {
  getCandidateProfile: async () => {
    const response = await api.get(`/api/v1/profile/candidate/me`);
    return response.data;
  },

  updateCandidateProfile: async (profileData) => {
    const response = await api.post('/api/v1/profile/candidate', profileData);
    return response.data;
  },

  getRecruiterProfile: async () => {
    const response = await api.get(`/api/v1/profile/recruiter/me`);
    return response.data;
  },

  updateRecruiterProfile: async (profileData) => {
    const response = await api.post('/api/v1/profile/recruiter', profileData);
    return response.data;
  },
};
