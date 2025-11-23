import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import Navbar from '../components/common/Navbar';
import { Briefcase, Users, TrendingUp, Search } from 'lucide-react';

const Landing = () => {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <section className="py-20">
        <div className="container mx-auto px-6 text-center">
          <h1 className="mb-6 text-5xl font-bold text-foreground">
            Find Your Dream Tech Job
          </h1>
          <p className="mb-8 text-xl text-muted-foreground">
            Connect with top tech companies and talented professionals
          </p>
          <div className="flex justify-center space-x-4">
            <Link to="/register">
              <Button size="lg" className="text-lg">
                Get Started
              </Button>
            </Link>
            <Link to="/jobs">
              <Button size="lg" variant="outline" className="text-lg">
                Browse Jobs
              </Button>
            </Link>
          </div>
        </div>
      </section>

      <section className="bg-card py-16">
        <div className="container mx-auto px-6">
          <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
            <div className="text-center">
              <div className="mb-4 flex justify-center">
                <Briefcase className="h-12 w-12 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-bold">Thousands of Jobs</h3>
              <p className="text-muted-foreground">
                Access a wide range of tech job opportunities from leading companies
              </p>
            </div>
            <div className="text-center">
              <div className="mb-4 flex justify-center">
                <Users className="h-12 w-12 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-bold">Top Talent</h3>
              <p className="text-muted-foreground">
                Recruiters can find qualified candidates with verified skills
              </p>
            </div>
            <div className="text-center">
              <div className="mb-4 flex justify-center">
                <TrendingUp className="h-12 w-12 text-primary" />
              </div>
              <h3 className="mb-2 text-xl font-bold">Career Growth</h3>
              <p className="text-muted-foreground">
                Track applications and grow your career with ease
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="py-16">
        <div className="container mx-auto px-6 text-center">
          <h2 className="mb-8 text-3xl font-bold">Ready to Get Started?</h2>
          <div className="flex justify-center space-x-4">
            <Link to="/register">
              <Button size="lg">Join as Job Seeker</Button>
            </Link>
            <Link to="/register">
              <Button size="lg" variant="outline">Join as Recruiter</Button>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Landing;
