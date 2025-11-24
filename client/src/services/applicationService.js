import api from './api';

export const applicationService = {
  applyToJob: async (jobId, coverLetter) => {
    const response = await api.post('/api/v1/applications', {
      jobId,
      coverLetter,
    });
    return response.data;
  },

  getMyApplications: async () => {
    const response = await api.get('/api/v1/applications/my-applications'
    );
    return response.data.content;
  },

  getApplicationsForJob: async (jobId, params) => {
    const response = await api.get(`/api/v1/applications/job/${jobId}`,{params});
    return response.data;
  },

  updateApplicationStatus: async (id, status, recruiterNotes) => {
    const response = await api.patch(`/api/v1/applications/${id}/status`, {
      status,
      recruiterNotes,
    });
    return response.data;
  },
};
