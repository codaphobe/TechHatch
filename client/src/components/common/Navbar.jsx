import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../ui/button';
import Logo from '/techhatch_logo_lighter_brackets.png'

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="border-b bg-card">
      <div className="container mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          <Link to="/" className="flex items-center space-x-2">
            <img src={Logo} alt="TechHatch Home" className="h-10 scale-150" />
            <span className="text-2xl font-bold text-foreground tracking-wide">tech
              <span className="text-primary">
                Hatch
              </span>
              </span>
          </Link>

          <div className="flex items-center space-x-6">
            {!user ? (
              <>
                <Link to="/jobs" className="text-sm font-medium text-foreground hover:text-primary">
                  Browse Jobs
                </Link>
                <Link to="/login">
                  <Button variant="outline">Login</Button>
                </Link>
                <Link to="/register">
                  <Button>Sign Up</Button>
                </Link>
              </>
            ) : (
              <>
                <Link
                  to={user.role === 'CANDIDATE' ? '/candidate/dashboard' : '/recruiter/dashboard'}
                  className="text-sm font-medium text-foreground hover:text-primary"
                >
                  Dashboard
                </Link>
                {user.role === 'CANDIDATE' ? (
                  <>
                    <Link to="/jobs" className="text-sm font-medium text-foreground hover:text-primary">
                      Browse Jobs
                    </Link>
                    <Link to="/candidate/applications" className="text-sm font-medium text-foreground hover:text-primary">
                      My Applications
                    </Link>
                    <Link to="/candidate/profile" className="text-sm font-medium text-foreground hover:text-primary">
                      Profile
                    </Link>
                  </>
                ) : (
                  <>
                    <Link to="/recruiter/profile" className="text-sm font-medium text-foreground hover:text-primary">
                      Profile
                    </Link>
                    <Link to="/recruiter/jobs" className="text-sm font-medium text-foreground hover:text-primary">
                      My Jobs
                    </Link>
                    <Link to="/recruiter/post-job" className="text-sm font-medium text-foreground hover:text-primary">
                      Post Job
                    </Link>
                  </>
                )}
                <span className="text-sm text-muted-foreground">{user.email}</span>
                <Button variant="outline" onClick={handleLogout}>
                  Logout
                </Button>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
