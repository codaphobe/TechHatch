import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { RadioGroup, RadioGroupItem } from '../components/ui/radio-group';
import Navbar from '../components/common/Navbar';
import toast from 'react-hot-toast';

const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [role, setRole] = useState('CANDIDATE');
  const [loading, setLoading] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [otpCode, setOtpCode] = useState('');
  const [verifying, setVerifying] = useState(false);
  const [resendTimer, setResendTimer] = useState(0);
  const { register, verifyRegistrationOTP } = useAuth();
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

    if (password !== confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    if (password.length < 6) {
      toast.error('Password must be at least 6 characters');
      return;
    }

    setLoading(true);

    try {
      const response = await register(email, password, role);
      if (response.otpSent) {
        setOtpSent(true);
        setResendTimer(60); // Start 1-minute timer
        toast.success(response.message || 'OTP sent to your email');
      } else {
        toast.error(response.error || response.message || 'Failed to send OTP');
      }
    } catch (error) {
      toast.error(error.response?.data?.message || error.response?.data?.error || 'Registration failed');
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
      await verifyRegistrationOTP(email, otpCode);
      toast.success('Registration successful! Please login.');
      navigate('/login');
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
      const response = await authService.resendOTP(email, 'REGISTRATION');
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
      <div className="flex items-center justify-center min-h-[calc(100vh-80px)]">
          <div className="w-4/6 flex items-center justify-center bg-card border rounded-lg m-10 p-6 space-y-6">
            <div className="text-center space-y-6 w-4/6">
              <div className="mx-20">
                <h2 className="text-3xl font-bold text-foreground">Create Account</h2>
                <p className="mt-2 text-muted-foreground">Join TechHatch today</p>
              </div>
              
              <div className="text-center text-sm">
                <span className="text-muted-foreground">Already have an account? </span>
                <Link to="/login" className="font-medium text-primary hover:underline">
                  Login
                </Link>
              </div>
            </div>

            {!otpSent ? (
              <form onSubmit={handleSubmit} className="w-4/6 space-y-6">
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
                      className="font-bold tracking-widest placeholder:text-muted pr-10"
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

                <div className="space-y-2">
                  <Label htmlFor="confirmPassword">Confirm Password</Label>
                  <div className="relative">
                    <Input
                      id="confirmPassword"
                      type={showConfirmPassword ? 'text' : 'password'}
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      required
                      placeholder="••••••••"
                      className="font-bold tracking-widest placeholder:text-muted pr-10"
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    >
                      {showConfirmPassword ? (
                        <EyeOff className="h-4 w-4 text-muted-foreground" />
                      ) : (
                        <Eye className="h-4 w-4 text-muted-foreground" />
                      )}
                    </Button>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label>I am a</Label>
                  <RadioGroup value={role} onValueChange={setRole}>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="CANDIDATE" id="candidate" />
                      <Label htmlFor="candidate" className="font-normal">
                        Job Seeker
                      </Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="RECRUITER" id="recruiter" />
                      <Label htmlFor="recruiter" className="font-normal">
                        Recruiter
                      </Label>
                    </div>
                  </RadioGroup>
                </div>

                <Button type="submit" className="w-full" disabled={loading}>
                  {loading ? 'Sending OTP...' : 'Sign Up'}
                </Button>
              </form>
            ) : (
              <form onSubmit={handleOTPVerify} className="w-4/6 space-y-6">
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

            
          </div>
        </div>
      </div>
  );
};

export default Register;
