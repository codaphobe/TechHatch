import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import { jobService } from '../../services/jobService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import StatusBadge from '../../components/common/StatusBadge';
import { formatDateRelative } from '../../utils/formatters';
import toast from 'react-hot-toast';
import { AlertCircle, Briefcase } from 'lucide-react';

const RecruiterDashboard = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [profileData, jobsData] = await Promise.all([
        profileService.getRecruiterProfile(user.userId).catch(() => null),
        jobService.getMyJobs(),
      ]);
      setProfile(profileData);
      setJobs(jobsData.content || []);
    } catch (error) {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const stats = {
    activeJobs: jobs.filter((j) => j.status === 'ACTIVE').length,
    totalApplications: jobs.reduce((sum, j) => sum + (j.applicationCount || 0), 0),
    totalJobs: jobs.length,
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
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">
            Welcome back, {profile?.companyName || user.email}!
          </h1>
          <p className="text-muted-foreground">Manage your job postings and applications</p>
        </div>

        {!profile && (
          <div className="mb-6 flex items-center gap-3 rounded-lg border border-yellow-600 bg-yellow-50 p-4">
            <AlertCircle className="h-5 w-5 text-yellow-600" />
            <div className="flex-1">
              <p className="font-medium text-gray-900">Complete your company profile</p>
              <p className="text-sm text-gray-700">
                Add company details to start posting jobs
              </p>
            </div>
            <Link to="/recruiter/profile">
              <Button>Complete Profile</Button>
            </Link>
          </div>
        )}

        <div className="mb-8 grid grid-cols-1 gap-6 md:grid-cols-3">
          <div className="rounded-lg border bg-card p-6">
            <div className="text-sm font-medium text-muted-foreground">Active Jobs</div>
            <div className="mt-2 text-3xl font-bold text-foreground">{stats.activeJobs}</div>
          </div>
          <div className="rounded-lg border bg-card p-6">
            <div className="text-sm font-medium text-muted-foreground">Total Applications</div>
            <div className="mt-2 text-3xl font-bold text-foreground">{stats.totalApplications}</div>
          </div>
          <div className="rounded-lg border bg-card p-6">
            <div className="text-sm font-medium text-muted-foreground">Total Jobs Posted</div>
            <div className="mt-2 text-3xl font-bold text-foreground">{stats.totalJobs}</div>
          </div>
        </div>

        <div className="rounded-lg border bg-card p-6">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-xl font-bold text-foreground">Recent Jobs</h2>
            <div className="flex gap-2">
              <Link to="/recruiter/jobs">
                <Button variant="outline">View All</Button>
              </Link>
              <Link to="/recruiter/post-job">
                <Button>Post New Job</Button>
              </Link>
            </div>
          </div>

          {jobs.length === 0 ? (
            <div className="py-12 text-center">
              <Briefcase className="mx-auto h-12 w-12 text-muted-foreground" />
              <p className="mt-4 text-lg text-muted-foreground">No jobs posted yet</p>
              <Link to="/recruiter/post-job">
                <Button className="mt-4">Post Your First Job</Button>
              </Link>
            </div>
          ) : (
            <div className="space-y-4">
              {jobs.slice(0, 5).map((job) => (
                <div key={job.id} className="flex items-center justify-between border-b pb-4 last:border-b-0">
                  <div>
                    <Link
                      to={`/recruiter/jobs/${job.id}/applications`}
                      className="text-lg font-semibold text-foreground hover:text-primary"
                    >
                      {job.title}
                    </Link>
                    <p className="text-sm text-muted-foreground">
                      {job.location} • Posted {formatDateRelative(job.postedDate)}
                    </p>
                    <p className="mt-1 text-xs text-muted-foreground">
                      {job.applicationCount || 0} applications
                    </p>
                  </div>
                  <StatusBadge status={job.status} />
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RecruiterDashboard;
