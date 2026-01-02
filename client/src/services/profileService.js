import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from './api';
import { PROFILE_QUERY_KEYS } from '../utils/constants'

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
  
  useProfileQuery: () => {
    return useQuery({
      queryKey: PROFILE_QUERY_KEYS.candidateProfile,
      queryFn: profileService.getCandidateProfile,
      staleTime: Infinity, //manual inivalidation on profile updation
      retry:0
    })
  },
  
  useUpdateProfileMutation: () => {
    const queryClient = useQueryClient();
    return useMutation({
      mutationFn: async (profileData) => {
          return await profileService.updateCandidateProfile(profileData);
        },
      
      //onSuccess
      onSuccess: () => {
        queryClient.invalidateQueries({queryKey:PROFILE_QUERY_KEYS.candidateProfile})
      }
    })
  }
};
