import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { jobService } from '../../services/jobService';
import { applicationService } from '../../services/applicationService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import { Textarea } from '../../components/ui/textarea';
import { Label } from '../../components/ui/label';
import { formatDate, formatSalary } from '../../utils/formatters';
import { JOB_TYPES, WORK_MODES, EXPERIENCE_LEVELS } from '../../utils/constants';
import { MapPin, Briefcase, Clock, DollarSign, Building } from 'lucide-react';
import toast from 'react-hot-toast';

const JobDetails = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const [hasApplied, setHasApplied] = useState(false);

  useEffect(() => {
    fetchJob();
    if (user?.role === 'CANDIDATE') {
      checkIfApplied();
    }
  }, [id]);

  const fetchJob = async () => {
    try {
      const data = await jobService.getJobById(id);
      setJob(data);
    } catch (error) {
      toast.error('Failed to load job details');
      navigate('/jobs');
    } finally {
      setLoading(false);
    }
  };

  const checkIfApplied = async () => {
    try {
      const applications = await applicationService.getMyApplications();
      const applied = applications.some((app) => app.job.jobId === parseInt(id));
      setHasApplied(applied);
    } catch (error) {
      // Ignore error
    }
  };

  const handleApply = async () => {
    if (!user) {
      toast.error('Please login to apply');
      navigate('/login');
      return;
    }

    if (user.role !== 'CANDIDATE') {
      toast.error('Only candidates can apply for jobs');
      return;
    }

    if (!coverLetter.trim()) {
      toast.error('Please write a cover letter');
      return;
    }

    setApplying(true);
    try {
      await applicationService.applyToJob(parseInt(id), coverLetter);
      toast.success('Application submitted successfully!');
      setHasApplied(true);
      setCoverLetter('');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to submit application');
    } finally {
      setApplying(false);
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

  if (!job) return null;

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="container mx-auto px-6 py-8">
      <Button
          variant="outline"
          onClick={() => navigate(`/jobs?${searchParams.toString()}`)}
          className="mb-4"
        >
          ← Back to Search
        </Button>
        <div className="grid grid-cols-12 gap-6">
          <div className="col-span-8">
            <div className="rounded-lg border bg-card p-8">
              <h1 className="text-3xl font-bold text-foreground">{job.title}</h1>
              <div className="mt-2 flex items-center gap-4 text-muted-foreground">
                <span className="flex items-center gap-1">
                  <Building className="h-4 w-4" />
                  {job.company.companyName}
                </span>
                <span>•</span>
                <span className="flex items-center gap-1">
                  <MapPin className="h-4 w-4" />
                  {job.location}
                </span>
              </div>

              <div className="mt-6 grid grid-cols-3 gap-4">
                <div className="flex items-center gap-2">
                  <Briefcase className="h-5 w-5 text-primary" />
                  <div>
                    <div className="text-xs text-muted-foreground">Job Type</div>
                    <div className="font-medium">{JOB_TYPES[job.jobType]}</div>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <Clock className="h-5 w-5 text-primary" />
                  <div>
                    <div className="text-xs text-muted-foreground">Work Mode</div>
                    <div className="font-medium">{WORK_MODES[job.workMode]}</div>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <DollarSign className="h-5 w-5 text-primary" />
                  <div>
                    <div className="text-xs text-muted-foreground">Salary</div>
                    <div className="font-medium">{formatSalary(job.salaryMin, job.salaryMax, job.currency)}</div>
                  </div>
                </div>
              </div>

              <div className="mt-6">
                <h2 className="mb-2 text-lg font-semibold">Required Skills</h2>
                <div className="flex flex-wrap gap-2">
                  {job.requiredSkills.map((skill, index) => (
                    <span key={index} className="rounded-full bg-primary/10 px-3 py-1 text-sm font-medium text-primary">
                      {skill}
                    </span>
                  ))}
                </div>
              </div>

              <div className="mt-6">
                <h2 className="mb-2 text-lg font-semibold">Job Description</h2>
                <p className="whitespace-pre-wrap text-muted-foreground">{job.description}</p>
              </div>

              <div className="mt-6">
                <h2 className="mb-2 text-lg font-semibold">Requirements</h2>
                <p className="whitespace-pre-wrap text-muted-foreground">{job.requirements}</p>
              </div>

              <div className="mt-6">
                <h2 className="mb-2 text-lg font-semibold">Responsibilities</h2>
                <p className="whitespace-pre-wrap text-muted-foreground">{job.responsibilities}</p>
              </div>

              <div className="mt-6 border-t pt-6">
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="text-muted-foreground">Experience Level:</span>{' '}
                    <span className="font-medium">{EXPERIENCE_LEVELS[job.experienceLevel]}</span>
                  </div>
                  <div>
                    <span className="text-muted-foreground">Posted:</span>{' '}
                    <span className="font-medium">{formatDate(job.postedDate)}</span>
                  </div>
                  <div>
                    <span className="text-muted-foreground">Expires:</span>{' '}
                    <span className="font-medium">{formatDate(job.expiryDate)}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-span-4">
            <div className="sticky top-6 space-y-6">
              <div className="rounded-lg border bg-card p-6">
                <h3 className="mb-4 text-lg font-semibold">Company Information</h3>
                <div className="space-y-2 text-sm">
                  <div>
                    <span className="font-medium">Company:</span> {job.company.companyName}
                  </div>
                  <div>
                    <span className="font-medium">Location:</span> {job.company.location}
                  </div>
                </div>
              </div>

              {user?.role === 'CANDIDATE' && (
                <div className="rounded-lg border bg-card p-6">
                  <h3 className="mb-4 text-lg font-semibold">Apply for this job</h3>
                  
                  {hasApplied ? (
                    <div className="rounded-lg bg-green-50 p-4 text-center">
                      <p className="font-medium text-green-800">You have already applied for this job</p>
                      <Link to="/candidate/applications">
                        <Button variant="outline" className="mt-3">
                          View Application
                        </Button>
                      </Link>
                    </div>
                  ) : (
                    <div className="space-y-4">
                      <div className="space-y-2">
                        <Label htmlFor="coverLetter">Cover Letter *</Label>
                        <Textarea
                          id="coverLetter"
                          value={coverLetter}
                          onChange={(e) => setCoverLetter(e.target.value)}
                          placeholder="Tell the employer why you're a great fit..."
                          rows={6}
                        />
                      </div>
                      <Button onClick={handleApply} disabled={applying} className="w-full">
                        {applying ? 'Applying...' : 'Apply Now'}
                      </Button>
                    </div>
                  )}
                </div>
              )}

              {!user && (
                <div className="rounded-lg border bg-card p-6 text-center">
                  <p className="mb-4 text-muted-foreground">Sign in to apply for this job</p>
                  <Link to="/login">
                    <Button className="w-full">Login</Button>
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default JobDetails;
