import { useQuery } from '@tanstack/react-query';
import api from './api';
import { APPLICATION_QUERY_KEYS } from '../utils/constants';

export const applicationService = {
  applyToJob: async (jobId, coverLetter) => {
    const response = await api.post('/api/v1/applications', {
      jobId,
      coverLetter,
    });
    return response.data;
  },

  getMyApplications: async (page=0) => {
    const response = await api.get('/api/v1/applications/my-applications',
      {
        params: {
          page: page
        }
      }
    );
    return response.data;
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
  
  useMyApplicationsQuery: (page = 0) => {
    return useQuery({
      queryKey: APPLICATION_QUERY_KEYS.candidateApplications(Math.max(0, page-1)),
      queryFn: () => applicationService.getMyApplications(Math.max(0, page-1)),
      staleTime: 1000 * 60,
      retry: 0,
      placeholderData: (prev) => prev,
    })
  }
};
