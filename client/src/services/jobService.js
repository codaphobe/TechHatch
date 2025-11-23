import api from './api';

export const jobService = {
  searchJobs: async (params) => {
    const response = await api.get('/api/v1/jobs', { params });
    return response.data;
  },

  getJobById: async (id) => {
    const response = await api.get(`/api/v1/jobs/${id}`);
    return response.data;
  },

  createJob: async (jobData) => {
    const response = await api.post('/api/v1/jobs', jobData);
    return response.data;
  },

  updateJob: async (id, jobData) => {
    const response = await api.put(`/api/v1/jobs/${id}`, jobData);
    return response.data;
  },

  deleteJob: async (id) => {
    const response = await api.delete(`/api/v1/jobs/${id}`);
    return response.data;
  },

  getMyJobs: async (page = 0) => {
    const response = await api.get('/api/v1/jobs/my-jobs', {
      params: { page },
    });
    return response.data;
  },

  closeJob: async (id) => {
    const response = await api.patch(`/api/v1/jobs/${id}/close`);
    return response.data;
  },
};
