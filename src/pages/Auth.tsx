import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { supabase } from '@/integrations/supabase/client';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Reveal } from '@/components/motion/Reveal';
import { ArrowLeft } from 'lucide-react';
import logo from '@/assets/logo.png';
import { toast } from 'sonner';
import { z } from 'zod';

const emailSchema = z.string().trim().email().max(200);
const passwordSchema = z.string().min(6).max(100);

const Auth = () => {
  const { t } = useTranslation();
  const nav = useNavigate();
  const [params] = useSearchParams();
  const redirect = params.get('redirect') || '/';
  const [busy, setBusy] = useState(false);
  const [login, setLogin] = useState({ email: '', password: '' });
  const [signup, setSignup] = useState({ email: '', password: '', display_name: '' });

  const doLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!emailSchema.safeParse(login.email).success) return toast.error('Invalid email');
    if (!passwordSchema.safeParse(login.password).success) return toast.error('Password ≥ 6 chars');
    setBusy(true);
    const { error } = await supabase.auth.signInWithPassword({ email: login.email, password: login.password });
    setBusy(false);
    if (error) return toast.error(error.message);
    toast.success(t('auth.loginSuccess'));
    nav(redirect);
  };

  const doSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!emailSchema.safeParse(signup.email).success) return toast.error('Invalid email');
    if (!passwordSchema.safeParse(signup.password).success) return toast.error('Password ≥ 6 chars');
    setBusy(true);
    const { error } = await supabase.auth.signUp({
      email: signup.email, password: signup.password,
      options: { emailRedirectTo: window.location.origin, data: { display_name: signup.display_name } },
    });
    setBusy(false);
    if (error) return toast.error(error.message);
    toast.success(t('auth.signupSuccess'));
    nav(redirect);
  };

  return (
    <div className="min-h-[calc(100vh-5rem)] grid place-items-center bg-hero py-12 px-4">
      <Reveal>
        <div className="w-full max-w-md p-8 rounded-3xl bg-card shadow-elegant border border-border relative">
          <div className="flex justify-start mb-4">
            <Button asChild variant="outline" size="sm" className="text-muted-foreground hover:text-foreground rounded-full px-4">
              <Link to="/"><ArrowLeft className="h-4 w-4 mr-1.5" />{t('common.back') || 'Quay lại'}</Link>
            </Button>
          </div>
          <Link to="/" className="flex items-center justify-center gap-2 mb-8">
            <img src={logo} alt="Merryblue" className="h-14 w-auto object-contain shrink-0 p-2 bg-orange-500/5 border border-orange-500/20 rounded-xl shadow-sm" />
          </Link>
          <Tabs defaultValue="login">
            <TabsList className="grid grid-cols-2 w-full">
              <TabsTrigger value="login">{t('auth.loginTitle')}</TabsTrigger>
              <TabsTrigger value="signup">{t('auth.signupTitle')}</TabsTrigger>
            </TabsList>
            <TabsContent value="login" className="space-y-4 pt-6">
              <form onSubmit={doLogin} className="space-y-4">
                <div><Label>{t('auth.email')}</Label><Input type="email" value={login.email} onChange={(e) => setLogin({ ...login, email: e.target.value })} required maxLength={200} /></div>
                <div><Label>{t('auth.password')}</Label><Input type="password" value={login.password} onChange={(e) => setLogin({ ...login, password: e.target.value })} required /></div>
                <Button disabled={busy} className="w-full bg-gradient-primary text-primary-foreground h-11 rounded-full">{busy ? '...' : t('auth.loginTitle')}</Button>
              </form>
            </TabsContent>
            <TabsContent value="signup" className="space-y-4 pt-6">
              <form onSubmit={doSignup} className="space-y-4">
                <div><Label>{t('auth.displayName')}</Label><Input value={signup.display_name} onChange={(e) => setSignup({ ...signup, display_name: e.target.value })} maxLength={100} /></div>
                <div><Label>{t('auth.email')}</Label><Input type="email" value={signup.email} onChange={(e) => setSignup({ ...signup, email: e.target.value })} required maxLength={200} /></div>
                <div><Label>{t('auth.password')}</Label><Input type="password" value={signup.password} onChange={(e) => setSignup({ ...signup, password: e.target.value })} required minLength={6} /></div>
                <Button disabled={busy} className="w-full bg-gradient-primary text-primary-foreground h-11 rounded-full">{busy ? '...' : t('auth.signupTitle')}</Button>
              </form>
            </TabsContent>
          </Tabs>
        </div>
      </Reveal>
    </div>
  );
};
export default Auth;
