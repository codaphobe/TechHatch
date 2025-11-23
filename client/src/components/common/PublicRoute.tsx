import { useAuth } from '../../context/AuthContext';
import { Navigate } from 'react-router-dom';
import toast from 'react-hot-toast';

const PublicRoute = ({ children}) => {
    const { user } = useAuth();
    
    if (!user) {
        return children;
    };
    
    toast.error("Already logged in");
    return <Navigate to="/" replace/>;
};

export default PublicRoute