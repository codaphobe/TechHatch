import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { jobService } from '../../services/jobService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import StatusBadge from '../../components/common/StatusBadge';
import { formatDate } from '../../utils/formatters';
import { Briefcase, Edit, Trash2, Eye, XCircle } from 'lucide-react';
import toast from 'react-hot-toast';

const MyJobs = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async () => {
    try {
      const data = await jobService.getMyJobs();
      setJobs(data.content || []);
    } catch (error) {
      toast.error('Failed to load jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseJob = async (id) => {
    if (!window.confirm('Are you sure you want to close this job?')) return;

    try {
      await jobService.closeJob(id);
      toast.success('Job closed successfully');
      fetchJobs();
    } catch (error) {
      toast.error('Failed to close job');
    }
  };

  const handleDeleteJob = async (id) => {
    if (!window.confirm('Are you sure you want to delete this job? This action cannot be undone.')) return;

    try {
      await jobService.deleteJob(id);
      toast.success('Job deleted successfully');
      fetchJobs();
    } catch (error) {
      toast.error('Failed to delete job');
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
        <div className="mb-8 flex items-center justify-between">
          <h1 className="text-3xl font-bold text-foreground">My Job Postings</h1>
          <Link to="/recruiter/post-job">
            <Button>Post New Job</Button>
          </Link>
        </div>

        {jobs.length === 0 ? (
          <div className="rounded-lg border bg-card p-12 text-center">
            <Briefcase className="mx-auto h-12 w-12 text-muted-foreground" />
            <p className="mt-4 text-lg font-semibold text-foreground">No jobs posted yet</p>
            <p className="text-muted-foreground">Create your first job posting</p>
            <Link to="/recruiter/post-job">
              <Button className="mt-4">Post a Job</Button>
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {jobs.map((job) => (
              <div key={job.id} className="rounded-lg border bg-card p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3">
                      <h3 className="text-xl font-semibold text-foreground">{job.title}</h3>
                      <StatusBadge status={job.status} />
                    </div>
                    <p className="mt-1 text-sm text-muted-foreground">{job.location}</p>
                    <div className="mt-3 grid grid-cols-3 gap-4 text-sm">
                      <div>
                        <span className="text-muted-foreground">Posted:</span>{' '}
                        <span className="font-medium">{formatDate(job.postedDate)}</span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Expires:</span>{' '}
                        <span className="font-medium">{formatDate(job.expiryDate)}</span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Applications:</span>{' '}
                        <span className="font-medium">{job.applicationCount || 0}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Link to={`/recruiter/jobs/${job.id}/applications`}>
                      <Button variant="outline" size="sm">
                        <Eye className="mr-1 h-4 w-4" />
                        View Applications
                      </Button>
                    </Link>
                    <Link to={`/recruiter/jobs/${job.id}/edit`}>
                      <Button variant="outline" size="sm">
                        <Edit className="mr-1 h-4 w-4" />
                        Edit
                      </Button>
                    </Link>
                    {job.status === 'ACTIVE' && (
                      <Button variant="outline" size="sm" onClick={() => handleCloseJob(job.id)}>
                        <XCircle className="mr-1 h-4 w-4" />
                        Close
                      </Button>
                    )}
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleDeleteJob(job.id)}
                      className="text-red-600 hover:bg-red-50"
                    >
                      <Trash2 className="mr-1 h-4 w-4" />
                      Delete
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MyJobs;
