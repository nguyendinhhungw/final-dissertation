import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { useAuth } from '@/contexts/AuthContext';
import { supabase } from '@/integrations/supabase/client';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { ArrowLeft, Briefcase, MapPin, Wallet, Upload } from 'lucide-react';
import { Reveal } from '@/components/motion/Reveal';
import { toast } from 'sonner';
import { z } from 'zod';

const applySchema = z.object({
  full_name: z.string().trim().min(1).max(100),
  email: z.string().trim().email().max(200),
  phone: z.string().trim().max(30).optional().or(z.literal('')),
  cover_letter: z.string().trim().max(5000).optional().or(z.literal('')),
});

const JobDetail = () => {
  const { slug } = useParams();
  const { t } = useTranslation();
  const { lang } = useLang();
  const { user } = useAuth();
  const nav = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({ full_name: '', email: '', phone: '', cover_letter: '' });
  const [cv, setCv] = useState<File | null>(null);

  const { data: jobData } = useRealtimeTable<any>({
    table: 'jobs',
    filters: [{ column: 'slug', value: slug! }],
    single: true,
    enabled: !!slug,
  });
  const job = jobData as any;

  useEffect(() => {
    if (user) setForm((f) => ({ ...f, email: user.email ?? '' }));
  }, [user]);

  if (!job) return <div className="container-tight py-20">{t('common.loading')}</div>;
  const pick = (k: string) => job?.[`${k}_${lang}`] ?? job?.[`${k}_en`] ?? '';

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    const parsed = applySchema.safeParse(form);
    if (!parsed.success) { toast.error(parsed.error.issues[0].message); return; }
    if (!cv) { toast.error('Please upload your CV (PDF)'); return; }
    if (cv.size > 5 * 1024 * 1024) { toast.error('CV must be under 5MB'); return; }

    setSubmitting(true);
    const folder = user ? user.id : 'guest';
    const path = `${folder}/${Date.now()}-${cv.name.replace(/[^\w.-]/g, '_')}`;
    const { error: upErr } = await supabase.storage.from('cv-uploads').upload(path, cv);
    if (upErr) { toast.error(upErr.message); setSubmitting(false); return; }

    const { error: insErr } = await supabase.from('applications').insert({
      user_id: user?.id ?? null,
      job_id: job.id,
      full_name: form.full_name,
      email: form.email,
      phone: form.phone || null,
      cover_letter: form.cover_letter || null,
      cv_path: path,
    });
    setSubmitting(false);
    if (insErr) { toast.error(insErr.message); return; }
    toast.success(t('careers.applySuccess'));
    setForm({ full_name: '', email: user?.email ?? '', phone: '', cover_letter: '' });
    setCv(null);
  };

  return (
    <div className="container-tight py-20 grid lg:grid-cols-3 gap-12">
      <div className="lg:col-span-2">
        <Button asChild variant="ghost" className="mb-8"><Link to="/recruitment"><ArrowLeft className="mr-2 h-4 w-4" />{t('common.back')}</Link></Button>
        <Reveal>
          {job.department && <div className="text-xs uppercase tracking-wider text-primary font-semibold">{job.department}</div>}
          <h1 className="font-display text-4xl lg:text-5xl font-bold mt-3">{pick('title')}</h1>
          <div className="mt-5 flex flex-wrap gap-5 text-sm text-muted-foreground">
            {job.location && <span className="inline-flex items-center gap-1.5"><MapPin className="h-4 w-4 text-primary" />{job.location}</span>}
            {job.employment_type && <span className="inline-flex items-center gap-1.5"><Briefcase className="h-4 w-4 text-primary" />{job.employment_type}</span>}
            {job.salary_range && <span className="inline-flex items-center gap-1.5"><Wallet className="h-4 w-4 text-primary" />{job.salary_range}</span>}
          </div>
          <Section title={t('careers.description')} body={pick('description')} />
          <Section title={t('careers.requirements')} body={pick('requirements')} />
          <Section title={t('careers.benefits')} body={pick('benefits')} />
        </Reveal>
      </div>

      <aside className="lg:col-span-1">
        <div className="lg:sticky lg:top-28 p-6 rounded-2xl border border-border bg-card shadow-soft">
          <h3 className="font-display text-xl font-bold mb-4">{t('careers.applyNow')}</h3>
          {!user && (
            <Link 
              to={`/auth?redirect=/recruitment/${slug}`} 
              className="flex items-center justify-center mb-6 p-3 rounded-lg border border-orange-500/20 bg-orange-500/5 hover:border-orange-600 hover:bg-orange-500/10 transition-all duration-300 text-sm text-primary font-semibold"
            >
              {t('careers.loginToNotify')}
            </Link>
          )}
          <form onSubmit={submit} className="space-y-4">
            <div><Label>{t('careers.fullName')}</Label><Input value={form.full_name} onChange={(e) => setForm({ ...form, full_name: e.target.value })} required maxLength={100} /></div>
            <div><Label>{t('careers.email')}</Label><Input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required maxLength={200} /></div>
            <div><Label>{t('careers.phone')}</Label><Input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} maxLength={30} /></div>
            <div>
              <Label>{t('careers.uploadCV')}</Label>
              <label className="mt-1.5 flex items-center justify-center gap-2 px-4 py-3 rounded-md border border-dashed border-border bg-secondary/40 cursor-pointer hover:bg-accent transition-smooth text-sm">
                <Upload className="h-4 w-4" />
                <span className="truncate">{cv?.name || 'PDF, max 5MB'}</span>
                <input type="file" accept="application/pdf" className="hidden" onChange={(e) => setCv(e.target.files?.[0] ?? null)} />
              </label>
            </div>
            <div><Label>{t('careers.coverLetter')}</Label><Textarea rows={4} value={form.cover_letter} onChange={(e) => setForm({ ...form, cover_letter: e.target.value })} maxLength={5000} /></div>
            <Button type="submit" disabled={submitting} className="w-full bg-gradient-primary text-primary-foreground rounded-full h-12">
              {submitting ? '...' : t('common.submit')}
            </Button>
          </form>
        </div>
      </aside>
    </div>
  );
};

const Section = ({ title, body }: { title: string; body: string }) => body ? (
  <div className="mt-10">
    <h2 className="font-display text-2xl font-bold mb-3">{title}</h2>
    <div className="text-foreground/90 leading-relaxed whitespace-pre-line">{body}</div>
  </div>
) : null;

export default JobDetail;
