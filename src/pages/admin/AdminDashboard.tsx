import { useEffect, useState } from 'react';
import { supabase } from '@/integrations/supabase/client';
import { Briefcase, FileText, MessageSquare, FolderKanban } from 'lucide-react';
import { useTranslation } from 'react-i18next';

const AdminDashboard = () => {
  const { t } = useTranslation();
  const [kpi, setKpi] = useState({ jobs: 0, apps: 0, unreadContacts: 0, projects: 0 });
  useEffect(() => {
    (async () => {
      const [j, a, c, p] = await Promise.all([
        supabase.from('jobs').select('id', { count: 'exact', head: true }).eq('is_open', true),
        supabase.from('applications').select('id', { count: 'exact', head: true }),
        supabase.from('contacts').select('id', { count: 'exact', head: true }).eq('is_read', false),
        supabase.from('portfolio_projects').select('id', { count: 'exact', head: true }),
      ]);
      setKpi({ jobs: j.count ?? 0, apps: a.count ?? 0, unreadContacts: c.count ?? 0, projects: p.count ?? 0 });
    })();
  }, []);

  const cards = [
    { label: t('admin.dashboard.openJobs'), value: kpi.jobs, icon: Briefcase, color: 'from-orange-500 to-pink-500' },
    { label: t('admin.dashboard.applications'), value: kpi.apps, icon: FileText, color: 'from-amber-500 to-orange-600' },
    { label: t('admin.dashboard.unread'), value: kpi.unreadContacts, icon: MessageSquare, color: 'from-rose-500 to-orange-500' },
    { label: t('admin.dashboard.projects'), value: kpi.projects, icon: FolderKanban, color: 'from-yellow-500 to-orange-500' },
  ];

  return (
    <div className="p-8">
      <h1 className="font-display text-3xl font-bold">{t('admin.dashboard.title')}</h1>
      <p className="text-muted-foreground mt-1">{t('admin.dashboard.welcome')}</p>
      <div className="mt-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {cards.map((c) => (
          <div key={c.label} className="p-6 rounded-2xl bg-card border border-border shadow-soft">
            <div className={`w-12 h-12 grid place-items-center rounded-xl bg-gradient-to-br ${c.color} text-white mb-4`}>
              <c.icon className="h-6 w-6" />
            </div>
            <div className="text-3xl font-display font-bold">{c.value}</div>
            <div className="text-sm text-muted-foreground mt-1">{c.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
};
export default AdminDashboard;
