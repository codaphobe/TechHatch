import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Textarea } from '../../components/ui/textarea';
import toast from 'react-hot-toast';
import ErrorState from '../../components/common/ErrorState'
import { AlertCircleIcon } from 'lucide-react';
import { Alert, AlertDescription, AlertTitle } from '../../components/ui/alert'

const CandidateProfile = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    skills: '',
    experienceYears: '',
    education: '',
    bio: '',
    linkedinUrl: '',
    githubUrl: '',
    portfolioUrl: '',
    location: '',
  });
  const [formInitialized, setFormInitialized] = useState(false);
  const [formErrors, setFormErrors] = useState([])

  const candidateProfile = profileService.useProfileQuery();
  const updateCandidateProfile = profileService.useUpdateProfileMutation();
  
  //Setting the form for the first time only
  //This prevents any background query refetch from undoing
  //  users's unsaved edits
  useEffect(() => {
    if(candidateProfile.data && !formInitialized){
      const data = candidateProfile.data;
      setFormData({
        fullName: data.fullName || '',
        phone: data.phone || '',
        skills: data.skills?.join(', ') || '',
        experienceYears: data.experienceYears || '',
        education: data.education || '',
        bio: data.bio || '',
        linkedinUrl: data.linkedinUrl || '',
        githubUrl: data.githubUrl || '',
        portfolioUrl: data.portfolioUrl || '',
        location: data.location || '',
      });
      setFormInitialized(true);
    }
  }, [candidateProfile.data]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormErrors([]);
      const profileData = {
        ...formData,
        skills: formData.skills.split(',').map((s) => s.trim()).filter((s) => s),
        experienceYears: parseInt(formData.experienceYears) || 0,
      };

      updateCandidateProfile.mutate(profileData,
        {
          onSuccess: () => {
            toast.success('Profile updated successfully!');
            setSaving(false);
          },
          
          onError: (error) => {
            const status = error?.response?.status
            
            if(status === 422){
              const details = Array.isArray(error.response?.data?.details)
                ? error.response.data.details
                : [];
              toast.error("Validation failed. Please review the form.", {id:"Validation errors", duration:5000})
              setFormErrors(details);
              setSaving(false);
              return;
            }
            
            toast.error("Failed to update profile")
            console.log(error)
            setSaving(false);
          }
        }
      );
  };

  if (candidateProfile.isLoading || candidateProfile.isPaused) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex min-h-[calc(100vh-80px)] items-center justify-center">
          <div className="text-xl">Loading...</div>
        </div>
      </div>
    );
  }
  
  if(candidateProfile.isError && !isProfileMissing){
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex min-h-[calc(100vh-80px)] items-center justify-center">
          <ErrorState
            title="Something went wrong"
            description="We couldn't load your profile. Please try again."
            onRetry={() => candidateProfile.refetch()}
            retrying={candidateProfile.isFetching}
          />
        </div>
      </div>
    );
  }
  
  const isProfileMissing = 
    (candidateProfile.isError &&
    candidateProfile.error?.response?.status === 404);

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="container mx-auto px-6 py-8">
        <>
          {isProfileMissing && (
            <div className="mx-auto p-6 max-w-4xl">
              <Alert className="bg-card text-card-foreground border-yellow-600 bg-yellow-50">
                <AlertCircleIcon className="h-5 w-5 !text-yellow-600 "/>
                <AlertTitle className="col-start-2 font-medium text-gray-900">
                  Complete your profile
                </AlertTitle>
                <AlertDescription className="text-sm text-gray-700 col-start-2">
                  Add your details to start applying for jobs
                </AlertDescription>
              </Alert>
            </div>
          )}
        </>
        {/* Alert for validation errors */}
        {formErrors.length > 0 && (
          <div className="container mx-auto px-6 py-6 max-w-lg mb-4 ">
            <Alert variant="destructive" className="bg-card text-sm px-4 py-3">
              <AlertCircleIcon className="h-4 w-4" />
              <AlertTitle className="font-semibold">
                Some fields have errors.
              </AlertTitle>
              <AlertDescription >
                <p >
                  Please input proper values and try again.
                </p>
                <ul className="list-inside list-disc text-sm mt-2">
                  {formErrors.map((msg, index) => (
                    <li key={index}>{msg}</li>
                  ))}
                </ul>
              </AlertDescription>
            </Alert>
          </div>
        )}
        <div className="mx-auto max-w-3xl">
          <h1 className="mb-2 text-3xl font-bold text-foreground">Your Profile</h1>
          <p className="mb-8 text-muted-foreground">
            Complete your profile to start applying for jobs
          </p>

          <form onSubmit={handleSubmit} className="space-y-6 rounded-lg border bg-card p-8">
            <div className="space-y-2">
              <Label htmlFor="fullName">Full Name *</Label>
              <Input
                id="fullName"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                required
                placeholder="John Doe"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="phone">Phone Number</Label>
              <Input
                id="phone"
                name="phone"
                value={formData.phone}
                type='number'
                onChange={handleChange}
                placeholder="9876543210"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="location">Location *</Label>
              <Input
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                required
                placeholder="Bangalore, India"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="skills">Skills * (comma separated)</Label>
              <Input
                id="skills"
                name="skills"
                value={formData.skills}
                onChange={handleChange}
                required
                placeholder="Java, React, Spring Boot, MySQL"
              />
              <p className="text-xs text-muted-foreground">
                Separate skills with commas
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="experienceYears">Years of Experience</Label>
              <Input
                id="experienceYears"
                name="experienceYears"
                type="number"
                value={formData.experienceYears}
                onChange={handleChange}
                min="0"
                placeholder="2"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="education">Education</Label>
              <Textarea
                id="education"
                name="education"
                value={formData.education}
                onChange={handleChange}
                placeholder="B.Tech in Computer Science"
                rows={3}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="bio">Bio</Label>
              <Textarea
                id="bio"
                name="bio"
                value={formData.bio}
                onChange={handleChange}
                placeholder="Tell us about yourself..."
                rows={4}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="linkedinUrl">LinkedIn URL</Label>
              <Input
                id="linkedinUrl"
                name="linkedinUrl"
                value={formData.linkedinUrl}
                onChange={handleChange}
                placeholder="https://linkedin.com/in/johndoe"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="githubUrl">GitHub URL</Label>
              <Input
                id="githubUrl"
                name="githubUrl"
                value={formData.githubUrl}
                onChange={handleChange}
                placeholder="https://github.com/johndoe"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="portfolioUrl">Portfolio URL</Label>
              <Input
                id="portfolioUrl"
                name="portfolioUrl"
                value={formData.portfolioUrl}
                onChange={handleChange}
                placeholder="https://johndoe.dev"
              />
            </div>

            <div className="flex space-x-4">
              <Button type="submit" disabled={saving}>
                {saving ? 'Saving...' : 'Save Profile'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/candidate/dashboard')}
              >
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CandidateProfile;
