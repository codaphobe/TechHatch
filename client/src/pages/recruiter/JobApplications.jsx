import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { applicationService } from '../../services/applicationService';
import { jobService } from '../../services/jobService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import { Textarea } from '../../components/ui/textarea';
import { Label } from '../../components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import StatusBadge from '../../components/common/StatusBadge';
import { formatDate } from '../../utils/formatters';
import { APPLICATION_STATUS } from '../../utils/constants';
import { UserCircle, Mail, Phone, Briefcase, Calendar } from 'lucide-react';
import toast from 'react-hot-toast';

const JobApplications = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedApp, setExpandedApp] = useState(null);
  const [updating, setUpdating] = useState(false);
  const [statusUpdate, setStatusUpdate] = useState({});
  const [statusFilter, setStatusFilter] = useState(' ');
  const [pagination, setPagination] = useState({
    page: 0,
    totalPages: 0,
    size: 25,
  });
  
  useEffect(() => {
    fetchData();
  }, [id, statusFilter]);

  const fetchData = async () => {
    try {
      
      const params = {
        status: statusFilter,
        page : pagination.page
      }
      
      const [jobData, appsData] = await Promise.all([
        jobService.getJobById(id),
        applicationService.getApplicationsForJob(id, params),
      ]);
      setJob(jobData);
      setApplications(appsData.content);
      // Initialize status update state
      const initialStatus = {};
      appsData.content.forEach(app => {
        initialStatus[app.id] = {
          status: app.status,
          recruiterNotes: app.recruiterNotes || '',
        };
      });
      setStatusUpdate(initialStatus);
      setPagination((prev) => ({
        ...prev, totalPages:appsData.totalPages
      }));
    } catch (error) {
      toast.error('Failed to load applications');
      console.log(error)
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = (appId, field, value) => {
    setStatusUpdate(prev => ({
      ...prev,
      [appId]: {
        ...prev[appId],
        [field]: value,
      },
    }));
    setPagination((prev) => ({
      ...prev, 
      page:0
    }))
  };
  
  const handleUpdateStatus = async (appId) => {
    setUpdating(true);
    try {
      const { status, recruiterNotes } = statusUpdate[appId];
      await applicationService.updateApplicationStatus(appId, status, recruiterNotes);
      toast.success('Application status updated successfully');
      fetchData();
    } catch (error) {
      toast.error('Failed to update status');
    } finally {
      setUpdating(false);
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
        <div className="mb-6">
          <Button
              variant="outline"
              onClick={() => navigate("/recruiter/jobs")} 
              className="text-sm">
            ← Back to My Jobs
          </Button>
        </div>
    
        <div className="flex items-center justify-between p-4">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-foreground">{job?.title}</h1>
            <p className="text-muted-foreground">
              {applications.length} application{applications.length !== 1 ? 's' : ''}
            </p>
          </div>
          
          {/* Status Filter */}
          <div className="flex items-center ">
            <Label htmlFor='status-filter' className="text-md">Sort by : </Label>
            <div className="flex px-3">
              <Select 
                  value = {statusFilter}
                  onValueChange = {(value) => setStatusFilter(value)}
              >
                <SelectTrigger id='status-filter'> 
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value=" ">View all</SelectItem>
                  {Object.entries(APPLICATION_STATUS).map(([key, value]) => (
                                    <SelectItem key={key} value={key}>
                                      {value}
                                    </SelectItem>
                                  ))}
                </SelectContent>
              </Select>
            </div>
            
          </div>
        </div>


        {applications.length === 0 ? (
          <div className="rounded-lg border bg-card p-12 text-center">
            <UserCircle className="mx-auto h-12 w-12 text-muted-foreground" />
            <p className="mt-4 text-lg font-semibold text-foreground">No applications yet</p>
            <p className="text-muted-foreground">Check back later for new applications</p>
          </div>
        ) : (
          <div className="space-y-6">
            {applications.map((app) => (
              <div key={app.id} className="rounded-lg border bg-card p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3">
                      <h3 className="text-xl font-semibold text-foreground">
                        {app.candidate.fullName}
                      </h3>
                      <StatusBadge status={app.status} />
                    </div>
                    
                    <div className="mt-3 grid grid-cols-2 gap-3 text-sm">
                      <div className="flex items-center gap-2 text-muted-foreground">
                        <Mail className="h-4 w-4" />
                        {app.candidate.email}
                      </div>
                      <div className="flex items-center gap-2 text-muted-foreground">
                        <Phone className="h-4 w-4" />
                        {app.candidate.phone}
                      </div>
                      <div className="flex items-center gap-2 text-muted-foreground">
                        <Briefcase className="h-4 w-4" />
                        {app.candidate.experienceYears} years experience
                      </div>
                      <div className="flex items-center gap-2 text-muted-foreground">
                        <Calendar className="h-4 w-4" />
                        Applied {formatDate(app.appliedDate)}
                      </div>
                    </div>

                    {/* <div className="mt-3">
                      <span className="text-sm font-medium text-foreground">Skills: </span>
                      <div className="mt-1 flex flex-wrap gap-2">
                        {app.candidate.skills.map((skill, index) => (
                          <span
                            key={index}
                            className="rounded-full bg-primary/10 px-2 py-1 text-xs font-medium text-primary"
                          >
                            {skill}
                          </span>
                        ))}
                      </div>
                    </div> */}

                    {app.coverLetter && (
                      <div className="mt-4">
                        <button
                          onClick={() => setExpandedApp(expandedApp === app.id ? null : app.id)}
                          className="text-sm font-medium text-primary hover:underline"
                        >
                          {expandedApp === app.id ? 'Hide' : 'Show'} Cover Letter
                        </button>
                        {expandedApp === app.id && (
                          <p className="mt-2 rounded-lg bg-muted p-3 text-sm text-foreground">
                            {app.coverLetter}
                          </p>
                        )}
                      </div>
                    )}

                    <div className="mt-4 space-y-3 border-t pt-4">
                      <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor={`status-${app.id}`}>Update Status</Label>
                          <Select
                            value={statusUpdate[app.id]?.status}
                            onValueChange={(value) => handleStatusChange(app.id, 'status', value)}
                          >
                            <SelectTrigger id={`status-${app.id}`}>
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                              {Object.entries(APPLICATION_STATUS).map(([key, value]) => (
                                <SelectItem key={key} value={key}>
                                  {value}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor={`notes-${app.id}`}>Recruiter Notes</Label>
                        <Textarea
                          id={`notes-${app.id}`}
                          value={statusUpdate[app.id]?.recruiterNotes}
                          onChange={(e) => handleStatusChange(app.id, 'recruiterNotes', e.target.value)}
                          placeholder="Add notes about this candidate..."
                          rows={3}
                        />
                      </div>

                      <Button
                        onClick={() => handleUpdateStatus(app.id)}
                        disabled={updating}
                      >
                        {updating ? 'Updating...' : 'Update Status'}
                      </Button>
                    </div>
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

export default JobApplications;
