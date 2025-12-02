import { useAuth } from '../../context/AuthContext';
import { Navigate } from 'react-router-dom';
import toast from 'react-hot-toast';

const PublicRoute = ({ children, type}) => {
    const { user, loading} = useAuth();
    
    if (loading) {
        return (
          <div className="flex min-h-screen items-center justify-center">
            <div className="text-xl">Loading...</div>
          </div>
        );
      }
    
    if (!user) {
        return children;
    };
    
    if(user && type === 'landing'){
        if (user.role === 'CANDIDATE') {
            return <Navigate to="/candidate/dashboard" replace/>;
          } else if (user.role === 'RECRUITER') {
            return <Navigate to="/recruiter/dashboard" replace/>;
          }
    }
    
    if(user && (type === 'login' || type === 'register')){
        toast.error("Already logged in");
        if (user.role === 'CANDIDATE') {
            return <Navigate to="/candidate/dashboard" replace/>;
        } else if (user.role === 'RECRUITER') {
            return <Navigate to="/recruiter/dashboard" replace/>;
        }
    }
};

export default PublicRoute