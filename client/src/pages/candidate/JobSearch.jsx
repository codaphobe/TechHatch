import { useState, useEffect } from 'react';
import { Link, replace, useSearchParams } from 'react-router-dom';
import { jobService } from '../../services/jobService';
import Navbar from '../../components/common/Navbar';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select';
import { useDebounce } from '../../hooks/useDebounce';
import { formatDateRelative, formatSalary } from '../../utils/formatters';
import { JOB_TYPES, EXPERIENCE_LEVELS } from '../../utils/constants';
import { Briefcase, MapPin } from 'lucide-react';
import toast from 'react-hot-toast';

const JobSearch = () => {
  const [searchParams , setSearchParams] = useSearchParams();
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    keyword: searchParams.get('keyword') || '',
    location: searchParams.get('location') || '',
    jobType: searchParams.get('jobType') || '',
    experienceLevel: searchParams.get('experienceLevel') || '',
    datePosted: searchParams.get('datePosted') || '',
  });
  const [pagination, setPagination] = useState({
    page: 0,
    totalPages: 0,
    size: 25,
  });

  const debouncedKeyword = useDebounce(filters.keyword, 1000);

  useEffect(() => {
    const params = new URLSearchParams();
    
    if (filters.keyword) params.set('keyword', filters.keyword);
    if (filters.location) params.set('location', filters.location);
    if (filters.jobType) params.set('jobType', filters.jobType);
    if (filters.experienceLevel) params.set('experienceLevel', filters.experienceLevel);
    if (filters.datePosted) params.set('datePosted', filters.datePosted);
    if (pagination.page > 0) params.set('page', pagination.page.toString());
    
    setSearchParams(params, {replace: true});
  },[filters, pagination.page, setSearchParams]);
  
  useEffect(() => {
    
    if(debouncedKeyword && debouncedKeyword.trim()){
      fetchJobs();
    }else{
      setJobs([]);
      setLoading(false);
    }
    
  }, [debouncedKeyword, filters.location, filters.jobType, filters.experienceLevel, filters.datePosted, pagination.page]);

  const fetchJobs = async () => {
    setLoading(true);
    try {
      const params = {
        keyword: debouncedKeyword,
        location: filters.location,
        jobType: filters.jobType,
        experienceLevel: filters.experienceLevel,
        datePosted: filters.datePosted,
        page: pagination.page
      };
      
      // Remove empty params
      Object.keys(params).forEach((key) => {
        if (params[key] == null || params[key] === "") delete params[key];
      });
        

      const data = await jobService.searchJobs(params);
      setJobs(data.content);
      setPagination((prev) => ({
        ...prev,
        totalPages: data.totalPages,
      }));
    } catch (error) {
      toast.error('Failed to fetch jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (field, value) => {
    setFilters((prev) => ({ ...prev, [field]: value }));
    setPagination((prev) => ({ ...prev, page: 0 })); // Reset to first page
  };

  const clearFilters = () => {
    setFilters({
      keyword:filters.keyword,
      location: '',
      jobType: '',
      experienceLevel: '',
      datePosted: '',
    });
    setPagination((prev) => ({ ...prev, page: 0 }));
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="container mx-auto px-6 py-8">
        <h1 className="mb-8 text-3xl font-bold text-foreground">Find Your Next Job</h1>

        <div className="grid grid-cols-12 gap-6">
          {/* Filters Sidebar */}
          <div className="col-span-3 space-y-6">
            <div className="rounded-lg border bg-card p-6">
              <h2 className="mb-4 text-lg font-semibold">Filters</h2>

              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="keyword">Keyword</Label>
                  <Input
                    id="keyword"
                    value={filters.keyword}
                    onChange={(e) => handleFilterChange('keyword', e.target.value)}
                    placeholder="Job title, skills..."
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="location">Location</Label>
                  <Input
                    id="location"
                    value={filters.location}
                    onChange={(e) => handleFilterChange('location', e.target.value)}
                    placeholder="City, state..."
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="jobType">Job Type</Label>
                  <Select value={filters.jobType} onValueChange={(value) => handleFilterChange('jobType', value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="All types" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value=" ">All types</SelectItem>
                      {Object.entries(JOB_TYPES).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="experienceLevel">Experience Level</Label>
                  <Select
                    value={filters.experienceLevel}
                    onValueChange={(value) => handleFilterChange('experienceLevel', value)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="All levels" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value=" ">All levels</SelectItem>
                      {Object.entries(EXPERIENCE_LEVELS).map(([key, value]) => (
                        <SelectItem key={key} value={key}>
                          {value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="datePosted">Date Posted</Label>
                  <Select value={filters.datePosted} onValueChange={(value) => handleFilterChange('datePosted', value)}>
                    <SelectTrigger>
                      <SelectValue placeholder="Any time" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value=" ">Any time</SelectItem>
                      <SelectItem value="7">Last 7 days</SelectItem>
                      <SelectItem value="30">Last 30 days</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <Button onClick={clearFilters} variant="outline" className="w-full">
                  Clear Filters
                </Button>
              </div>
            </div>
          </div>

          {/* Job Listings */}
          <div className="col-span-9">
            {loading ? (
              <div className="flex items-center justify-center py-12">
                <div className="text-xl">Loading jobs...</div>
              </div>
            ) : jobs.length === 0 ? (
              <div className="rounded-lg border bg-card p-12 text-center">
                <Briefcase className="mx-auto h-12 w-12 text-muted-foreground" />
                <p className="mt-4 text-lg font-semibold text-foreground">No jobs found</p>
                <p className="text-muted-foreground">Try adjusting your filters</p>
              </div>
            ) : (
              <>
                <div className="mb-4 text-sm text-muted-foreground">
                  Showing {jobs.length} jobs
                </div>

                <div className="space-y-4">
                  {jobs.map((job) => (
                    <Link
                      key={job.id}
                      to={`/jobs/${job.id}?${searchParams.toString()}`}
                      className="block rounded-lg border bg-card p-6 transition hover:border-primary"
                    >
                      <div className="flex justify-between">
                        <div className="flex-1">
                          <h3 className="text-xl font-semibold text-foreground hover:text-primary">
                            {job.title}
                          </h3>
                          <p className="mt-1 text-sm font-medium text-foreground">
                            {job.company.companyName}
                          </p>
                          <div className="mt-2 flex items-center gap-4 text-sm text-muted-foreground">
                            <span className="flex items-center gap-1">
                              <MapPin className="h-4 w-4" />
                              {job.location}
                            </span>
                            <span>•</span>
                            <span>{JOB_TYPES[job.jobType]}</span>
                            <span>•</span>
                            <span>{EXPERIENCE_LEVELS[job.experienceLevel]}</span>
                          </div>
                          <div className="mt-3 flex flex-wrap gap-2">
                            {job.requiredSkills.slice(0, 5).map((skill, index) => (
                              <span
                                key={index}
                                className="rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary"
                              >
                                {skill}
                              </span>
                            ))}
                          </div>
                        </div>
                        <div className="text-right">
                          <div className="text-sm font-semibold text-foreground">
                            {formatSalary(job.salaryMin, job.salaryMax, job.currency)}
                          </div>
                          <div className="mt-2 text-xs text-muted-foreground">
                            Posted {formatDateRelative(job.postedDate)}
                          </div>
                        </div>
                      </div>
                    </Link>
                  ))}
                </div>

                {/* Pagination */}
                {pagination.totalPages > 1 && (
                  <div className="mt-6 flex justify-center gap-2">
                    <Button
                      variant="outline"
                      onClick={() => setPagination((prev) => ({ ...prev, page: prev.page - 1 }))}
                      disabled={pagination.page === 0}
                    >
                      Previous
                    </Button>
                    <span className="flex items-center px-4 text-sm text-muted-foreground">
                      Page {pagination.page + 1} of {pagination.totalPages}
                    </span>
                    <Button
                      variant="outline"
                      onClick={() => setPagination((prev) => ({ ...prev, page: prev.page + 1 }))}
                      disabled={pagination.page >= pagination.totalPages - 1}
                    >
                      Next
                    </Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default JobSearch;
