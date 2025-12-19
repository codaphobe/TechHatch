import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import Navbar from '../components/common/Navbar';
import toast from 'react-hot-toast';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [otpCode, setOtpCode] = useState('');
  const [verifying, setVerifying] = useState(false);
  const [resendTimer, setResendTimer] = useState(0);
  const { login, verifyLoginOTP } = useAuth();
  const navigate = useNavigate();

  // Timer effect for resend OTP
  useEffect(() => {
    let interval = null;
    if (resendTimer > 0) {
      interval = setInterval(() => {
        setResendTimer((prev) => prev - 1);
      }, 1000);
    } else if (resendTimer === 0 && interval) {
      clearInterval(interval);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [resendTimer]);

  const formatTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await login(email, password);
      if (response.otpSent) {
        setOtpSent(true);
        setResendTimer(60); // Start 1-minute timer
        toast.success(response.message || 'OTP sent to your email');
      } else {
        toast.error(response.error || response.message || 'Failed to send OTP');
      }
    } catch (error) {
      toast.error(error.response?.data?.message || error.response?.data?.error || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleOTPVerify = async (e) => {
    e.preventDefault();
    
    if (!otpCode || otpCode.length !== 6) {
      toast.error('Please enter a 6-digit OTP');
      return;
    }

    setVerifying(true);

    try {
      const data = await verifyLoginOTP(email, otpCode);
      toast.success('Login successful!');
      
      // Navigate based on role
      if (data.role === 'CANDIDATE') {
        navigate('/candidate/dashboard');
      } else if (data.role === 'RECRUITER') {
        navigate('/recruiter/dashboard');
      }
    } catch (error) {
      toast.error(error.response?.data?.message || error.response?.data?.error || 'OTP verification failed');
    } finally {
      setVerifying(false);
    }
  };

  const handleResendOTP = async () => {
    if (resendTimer > 0) {
      return;
    }

    setLoading(true);

    try {
      const response = await authService.resendOTP(email, 'LOGIN');
      if (response.otpSent || response.success) {
        setResendTimer(60); // Reset timer to 1 minute
        toast.success(response.message || 'OTP resent to your email');
      } else {
        toast.error(response.error || response.message || 'Failed to resend OTP');
      }
    } catch (error) {
      toast.error(error.response?.data?.message || error.response?.data?.error || 'Failed to resend OTP');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="flex min-h-[calc(100vh-80px)] items-center justify-center px-6">
        <div className="w-full max-w-md space-y-8 rounded-lg border bg-card p-8 shadow-lg">
          <div className="text-center">
            <h2 className="text-3xl font-bold text-foreground">Welcome Back</h2>
            <p className="mt-2 text-muted-foreground">Login to your account</p>
          </div>

          {!otpSent ? (
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="email">Email Address</Label>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="you@example.com"
                  className="tracking-wider placeholder:text-muted"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <div className="relative">
                  <Input
                    id="password"
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    placeholder="••••••••"
                    className="font-extrabold text-lg tracking-widest placeholder:text-muted placeholder:font-extrabold placeholder:text-lg pr-10"
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4 text-muted-foreground" />
                    ) : (
                      <Eye className="h-4 w-4 text-muted-foreground" />
                    )}
                  </Button>
                </div>
              </div>

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? 'Sending OTP...' : 'Login'}
              </Button>
            </form>
          ) : (
            <form onSubmit={handleOTPVerify} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="otp">Enter OTP</Label>
                <p className="text-sm text-muted-foreground">
                  We've sent a verification code to {email}
                </p>
                <Input
                  id="otp"
                  type="text"
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                  required
                  placeholder="000000"
                  className="text-center text-2xl font-bold tracking-widest placeholder:text-muted"
                  maxLength={6}
                />
              </div>

              <Button type="submit" className="w-full" disabled={verifying}>
                {verifying ? 'Verifying...' : 'Verify'}
              </Button>

              <div className="text-center">
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleResendOTP}
                  disabled={resendTimer > 0 || loading}
                  className="w-full"
                >
                  {resendTimer > 0 ? `Resend OTP (${formatTimer(resendTimer)})` : 'Resend OTP'}
                </Button>
              </div>
            </form>
          )}

          <div className="text-center text-sm">
            <span className="text-muted-foreground">Don't have an account? </span>
            <Link to="/register" className="font-medium text-primary hover:underline">
              Sign up
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
