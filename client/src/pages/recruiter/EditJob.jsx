import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { jobService } from '../../services/jobService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Textarea } from '../../components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { JOB_TYPES, WORK_MODES, EXPERIENCE_LEVELS } from '../../utils/constants';
import toast from 'react-hot-toast';

const EditJob = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    requirements: '',
    responsibilities: '',
    location: '',
    jobType: 'FULL_TIME',
    workMode: 'HYBRID',
    experienceLevel: 'MID',
    salaryMin: '',
    salaryMax: '',
    currency: 'INR',
    requiredSkills: '',
    expiryDate: '',
  });

  useEffect(() => {
    fetchJob();
  }, [id]);

  const fetchJob = async () => {
    try {
      const data = await jobService.getJobById(id);
      setFormData({
        title: data.title,
        description: data.description,
        requirements: data.requirements,
        responsibilities: data.responsibilities,
        location: data.location,
        jobType: data.jobType,
        workMode: data.workMode,
        experienceLevel: data.experienceLevel,
        salaryMin: data.salaryMin.toString(),
        salaryMax: data.salaryMax.toString(),
        currency: data.currency,
        requiredSkills: data.requiredSkills.join(', '),
        expiryDate: data.expiryDate.split('T')[0],
      });
    } catch (error) {
      toast.error('Failed to load job details');
      navigate('/recruiter/jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);

    try {
      const jobData = {
        ...formData,
        salaryMin: parseFloat(formData.salaryMin) || 0,
        salaryMax: parseFloat(formData.salaryMax) || 0,
        requiredSkills: formData.requiredSkills.split(',').map((s) => s.trim()).filter((s) => s),
        expiryDate: new Date(formData.expiryDate).toISOString(),
      };

      await jobService.updateJob(id, jobData);
      toast.success('Job updated successfully!');
      navigate('/recruiter/jobs');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to update job');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex min-h-[calc(100vh-80px)] items-center justify-center">
          <div className="text-xl">Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="container mx-auto px-6 py-8">
        <div className="mx-auto max-w-4xl">
          <h1 className="mb-2 text-3xl font-bold text-foreground">Edit Job</h1>
          <p className="mb-8 text-muted-foreground">Update the job posting details</p>

          <form onSubmit={handleSubmit} className="space-y-8 rounded-lg border bg-card p-8">
            <div className="space-y-6">
              <h2 className="text-xl font-semibold">Basic Information</h2>
              
              <div className="space-y-2">
                <Label htmlFor="title">Job Title *</Label>
                <Input
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="description">Job Description *</Label>
                <Textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  required
                  rows={5}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="requirements">Requirements *</Label>
                <Textarea
                  id="requirements"
                  name="requirements"
                  value={formData.requirements}
                  onChange={handleChange}
                  required
                  rows={5}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="responsibilities">Responsibilities *</Label>
                <Textarea
                  id="responsibilities"
                  name="responsibilities"
                  value={formData.responsibilities}
                  onChange={handleChange}
                  required
                  rows={5}
                />
              </div>
            </div>

            <div className="space-y-6">
              <h2 className="text-xl font-semibold">Location & Type</h2>
              
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="location">Location *</Label>
                  <Input
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="jobType">Job Type *</Label>
                  <Select value={formData.jobType} onValueChange={(value) => setFormData({ ...formData, jobType: value })}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(JOB_TYPES).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="workMode">Work Mode *</Label>
                  <Select value={formData.workMode} onValueChange={(value) => setFormData({ ...formData, workMode: value })}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(WORK_MODES).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="experienceLevel">Experience Level *</Label>
                  <Select value={formData.experienceLevel} onValueChange={(value) => setFormData({ ...formData, experienceLevel: value })}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(EXPERIENCE_LEVELS).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>

            <div className="space-y-6">
              <h2 className="text-xl font-semibold">Compensation & Skills</h2>
              
              <div className="grid grid-cols-3 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="salaryMin">Minimum Salary *</Label>
                  <Input
                    id="salaryMin"
                    name="salaryMin"
                    type="number"
                    value={formData.salaryMin}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="salaryMax">Maximum Salary *</Label>
                  <Input
                    id="salaryMax"
                    name="salaryMax"
                    type="number"
                    value={formData.salaryMax}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="currency">Currency</Label>
                  <Select value={formData.currency} onValueChange={(value) => setFormData({ ...formData, currency: value })}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="INR">INR</SelectItem>
                      <SelectItem value="USD">USD</SelectItem>
                      <SelectItem value="EUR">EUR</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="requiredSkills">Required Skills * (comma separated)</Label>
                <Input
                  id="requiredSkills"
                  name="requiredSkills"
                  value={formData.requiredSkills}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="expiryDate">Expiry Date *</Label>
                <Input
                  id="expiryDate"
                  name="expiryDate"
                  type="date"
                  value={formData.expiryDate}
                  onChange={handleChange}
                  required
                  min={new Date().toISOString().split('T')[0]}
                />
              </div>
            </div>

            <div className="flex space-x-4">
              <Button type="submit" disabled={saving}>
                {saving ? 'Updating...' : 'Update Job'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/recruiter/jobs')}
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

export default EditJob;
