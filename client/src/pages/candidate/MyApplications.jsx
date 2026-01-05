import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { applicationService } from '../../services/applicationService';
import Navbar from '../../components/common/Navbar';
import StatusBadge from '../../components/common/StatusBadge';
import { formatDate } from '../../utils/formatters';
import { JOB_TYPES } from '../../utils/constants';
import { Briefcase } from 'lucide-react';
import toast from 'react-hot-toast';
import { Button } from '../../components/ui/button';
import ErrorState from '../../components/common/ErrorState'
import PaginationComponent from '../../components/common/Pagination';

const MyApplications = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);

  const myApplicationsQuery = applicationService.useMyApplicationsQuery(page);

  if (myApplicationsQuery.isLoading || myApplicationsQuery.isPaused) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex min-h-[calc(100vh-80px)] items-center justify-center">
          <div className="text-xl">Loading...</div>
        </div>
      </div>
    );
  }
  
  if(myApplicationsQuery.isError){
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex min-h-[calc(100vh-80px)] items-center justify-center">
          <ErrorState
            title="Something went wrong"
            description="We couldn't load your applications. Please try again."
            onRetry={
              async () => {
                toast.loading("Reconnecting...", { id: "reconnecting" })
                const result = await myApplicationsQuery.refetch();

                if (result.isError && result.error) {
                  toast.dismiss("reconnecting")
                } else {
                  toast.success("We're back again!!", { id: "reconnecting" })
                }
              }
            }
            retrying={myApplicationsQuery.isFetching}
          />
        </div>
      </div>
    );
  }
  
  const applications = myApplicationsQuery.data.content;

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <div className="container mx-auto px-6 py-8">
        <div>
        <Button
          variant="outline"
          onClick={() => navigate(`/candidate/dashboard`)}
          className="mb-4"
        >
          ← Back to Dashboard
        </Button>
        </div>
      </div>
      
      <div className="container mx-auto px-6 ">
        <h1 className="mb-8 text-3xl font-bold text-foreground">My Applications</h1>

        {applications.length === 0 ? (
          <div className="rounded-lg border bg-card p-12 text-center">
            <Briefcase className="mx-auto h-12 w-12 text-muted-foreground" />
            <p className="mt-4 text-lg font-semibold text-foreground">No applications yet</p>
            <p className="text-muted-foreground">Start applying to jobs to see them here</p>
            <Link to="/jobs">
              <button className="mt-4 rounded-lg bg-blue-600 px-6 py-2 font-semibold text-white hover:bg-blue-700">
                Browse Jobs
              </button>
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {applications.map((app) => (
              <div key={app.id} className="rounded-lg border bg-card p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <Link
                      to={`/jobs/${app.job.jobId}`}
                      className="text-xl font-semibold text-foreground hover:text-primary"
                    >
                      {app.job.title}
                    </Link>
                    <p className="mt-1 text-sm text-muted-foreground">
                      {app.job.companyName} • {app.job.location} • {JOB_TYPES[app.job.jobType]}
                    </p>
                    <div className="mt-4 grid grid-cols-3 gap-4 text-sm">
                      <div>
                        <span className="text-muted-foreground">Applied:</span>{' '}
                        <span className="font-medium">{formatDate(app.appliedDate)}</span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Last Updated:</span>{' '}
                        <span className="font-medium">{formatDate(app.lastUpdated)}</span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Status:</span>{' '}
                        <StatusBadge status={app.status} />
                      </div>
                    </div>
                    {app.coverLetter && (
                      <div className="mt-4">
                        <p className="text-sm font-medium text-foreground">Cover Letter:</p>
                        <p className="mt-1 text-sm text-muted-foreground">{app.coverLetter}</p>
                      </div>
                    )}
                    {app.recruiterNotes && (
                      <div className="mt-4 rounded-lg bg-blue-50 p-3">
                        <p className="text-sm font-medium text-gray-900">Recruiter Notes:</p>
                        <p className="mt-1 text-sm text-gray-700">{app.recruiterNotes}</p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
            <div className="py-6">
              <PaginationComponent 
                page={page}
                totalPages={myApplicationsQuery.data.totalPages ?? 1}
                onPageChange={setPage}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MyApplications;
