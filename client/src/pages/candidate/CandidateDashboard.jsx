import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { profileService } from '../../services/profileService';
import { applicationService } from '../../services/applicationService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import StatusBadge from '../../components/common/StatusBadge';
import { formatDateRelative } from '../../utils/formatters';
import toast from 'react-hot-toast';
import { AlertCircle, Briefcase } from 'lucide-react';

const CandidateDashboard = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [profileData, applicationsData] = await Promise.all([
        profileService.getCandidateProfile().catch(() => null),
        applicationService.getMyApplications(),
      ]);
      setProfile(profileData);
      setApplications(applicationsData);
    } catch (error) {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const stats = {
    total: applications.length,
    pending: applications.filter((a) => a.status === 'APPLIED' || a.status === 'UNDER_REVIEW').length,
    interviews: applications.filter((a) => a.status === 'INTERVIEW').length,
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
            Welcome back, {profile?.fullName || user.email}!
          </h1>
          <p className="text-muted-foreground">Track your job applications and opportunities</p>
        </div>

        {!profile && (
          <div className="mb-6 flex items-center gap-3 rounded-lg border border-yellow-600 bg-yellow-50 p-4">
            <AlertCircle className="h-5 w-5 text-yellow-600" />
            <div className="flex-1">
              <p className="font-medium text-gray-900">Complete your profile</p>
              <p className="text-sm text-gray-700">
                Add your details to start applying for jobs
              </p>
            </div>
            <Link to="/candidate/profile">
              <Button>Complete Profile</Button>
            </Link>
          </div>
        )}

        <div className="mb-8 grid grid-cols-1 gap-6 md:grid-cols-3">
          <div className="rounded-lg border bg-card p-6">
            <div className="text-sm font-medium text-muted-foreground">Total Applications</div>
            <div className="mt-2 text-3xl font-bold text-foreground">{stats.total}</div>
          </div>
          <div className="rounded-lg border bg-card p-6">
            <div className="text-sm font-medium text-muted-foreground">Pending Review</div>
            <div className="mt-2 text-3xl font-bold text-foreground">{stats.pending}</div>
          </div>
          <div className="rounded-lg border bg-card p-6">
            <div className="text-sm font-medium text-muted-foreground">Interviews</div>
            <div className="mt-2 text-3xl font-bold text-foreground">{stats.interviews}</div>
          </div>
        </div>

        <div className="rounded-lg border bg-card p-6">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-xl font-bold text-foreground">Recent Applications</h2>
            <Link to="/candidate/applications">
              <Button variant="outline">View All</Button>
            </Link>
          </div>

          {applications.length === 0 ? (
            <div className="py-12 text-center">
              <Briefcase className="mx-auto h-12 w-12 text-muted-foreground" />
              <p className="mt-4 text-lg text-muted-foreground">No applications yet</p>
              <Link to="/jobs">
                <Button className="mt-4">Browse Jobs</Button>
              </Link>
            </div>
          ) : (
            <div className="space-y-4">
              {applications.slice(0, 5).map((app) => (
                <div key={app.id} className="flex items-center justify-between border-b pb-4 last:border-b-0">
                  <div>
                    <Link
                      to={`/jobs/${app.job.jobId}`}
                      className="text-lg font-semibold text-foreground hover:text-primary"
                    >
                      {app.job.title}
                    </Link>
                    <p className="text-sm text-muted-foreground">
                      {app.job.companyName} • {app.job.location}
                    </p>
                    <p className="mt-1 text-xs text-muted-foreground">
                      Applied {formatDateRelative(app.appliedDate)}
                    </p>
                  </div>
                  <StatusBadge status={app.status} />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="mt-6 text-center">
          <Link to="/jobs">
            <Button size="lg">Browse More Jobs</Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default CandidateDashboard;
