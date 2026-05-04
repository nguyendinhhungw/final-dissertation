import { useEffect, useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { supabase } from '@/integrations/supabase/client';
import { useTranslation } from 'react-i18next';
import { Navigate, Link } from 'react-router-dom';
import { Reveal } from '@/components/motion/Reveal';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { FileText, MessageSquare } from 'lucide-react';
import CvViewerDialog from '@/components/CvViewerDialog';

const MyApplications = () => {
  const { user, loading } = useAuth();
  const { t, i18n } = useTranslation();
  const [apps, setApps] = useState<any[]>([]);
  const [cvPath, setCvPath] = useState<string | null>(null);

  useEffect(() => {
    if (!user) return;
    supabase
      .from('applications')
      .select('*, jobs(slug, title_vi, title_en)')
      .eq('user_id', user.id)
      .order('created_at', { ascending: false })
      .then(({ data }) => setApps(data ?? []));
  }, [user]);

  if (loading) return <div className="container-tight py-20">{t('common.loading')}</div>;
  if (!user) return <Navigate to="/auth?redirect=/my-applications" replace />;

  return (
    <div className="container-tight py-20 max-w-4xl">
      <Reveal>
        <h1 className="font-display text-5xl font-bold">{t('nav.myApps')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
      </Reveal>
      <div className="mt-12 space-y-4">
        {apps.map((a) => {
          const jobTitle =
            i18n.language === 'vi' ? a.jobs?.title_vi : a.jobs?.title_en || a.jobs?.title_vi;
          return (
            <div key={a.id} className="p-6 rounded-2xl border border-border bg-card space-y-4">
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div>
                  <Link
                    to={`/recruitment/${a.jobs?.slug}`}
                    className="font-display text-xl font-bold hover:text-primary"
                  >
                    {jobTitle}
                  </Link>
                  <div className="text-sm text-muted-foreground mt-1">
                    {new Date(a.created_at).toLocaleDateString(
                      i18n.language === 'vi' ? 'vi-VN' : 'en-US',
                    )}
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {a.cv_path && (
                    <Button size="sm" variant="outline" onClick={() => setCvPath(a.cv_path)}>
                      <FileText className="h-4 w-4 mr-1.5" />
                      {i18n.language === 'vi' ? 'Xem CV' : 'View CV'}
                    </Button>
                  )}
                  <Badge variant="outline" className="capitalize">
                    {a.status}
                  </Badge>
                </div>
              </div>

              {a.admin_notes && (
                <div className="rounded-xl bg-primary/5 border border-primary/20 p-4">
                  <div className="flex items-center gap-2 text-sm font-semibold text-primary mb-1.5">
                    <MessageSquare className="h-4 w-4" />
                    {i18n.language === 'vi' ? 'Phản hồi từ HR' : 'Message from HR'}
                  </div>
                  <div className="text-sm text-foreground/90 whitespace-pre-line">
                    {a.admin_notes}
                  </div>
                </div>
              )}
            </div>
          );
        })}
        {apps.length === 0 && (
          <p className="text-muted-foreground">
            {i18n.language === 'vi' ? 'Chưa có hồ sơ ứng tuyển nào.' : 'No applications yet.'}
          </p>
        )}
      </div>

      <CvViewerDialog path={cvPath} onOpenChange={(o) => !o && setCvPath(null)} />
    </div>
  );
};
export default MyApplications;
