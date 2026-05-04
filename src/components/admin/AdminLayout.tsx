import { Navigate, NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { LayoutDashboard, Briefcase, FolderKanban, Wrench, FileText, MessageSquare, FileEdit, LogOut, Home, Users, Newspaper } from 'lucide-react';
import logo from '@/assets/logo.png';
import { Button } from '@/components/ui/button';
import AdminNotifications from './AdminNotifications';
import { useTranslation } from 'react-i18next';

const AdminLayout = () => {
  const { isAdmin, isHr, loading, user, signOut } = useAuth();
  const { t } = useTranslation();
  if (loading) return <div className="grid min-h-screen place-items-center">{t('admin.common.loading')}</div>;
  if (!user) return <Navigate to="/auth?redirect=/admin" replace />;
  if (!isAdmin && !isHr) return <Navigate to="/" replace />;

  const allItems = [
    { to: '/admin', icon: LayoutDashboard, label: t('admin.nav.dashboard'), end: true, roles: ['admin', 'hr'] },
    { to: '/admin/portfolio', icon: FolderKanban, label: t('admin.nav.portfolio'), roles: ['admin'] },
    { to: '/admin/services', icon: Wrench, label: t('admin.nav.services'), roles: ['admin'] },
    { to: '/admin/blog', icon: Newspaper, label: t('admin.nav.blog'), roles: ['admin'] },
    { to: '/admin/jobs', icon: Briefcase, label: t('admin.nav.jobs'), roles: ['admin', 'hr'] },
    { to: '/admin/applications', icon: FileText, label: t('admin.nav.applications'), roles: ['admin', 'hr'] },
    { to: '/admin/contacts', icon: MessageSquare, label: t('admin.nav.contacts'), roles: ['admin', 'hr'] },
    { to: '/admin/content', icon: FileEdit, label: t('admin.nav.content'), roles: ['admin'] },
    { to: '/admin/users', icon: Users, label: t('admin.nav.users'), roles: ['admin'] },
  ];

  const role = isAdmin ? 'admin' : 'hr';
  const items = allItems.filter((i) => i.roles.includes(role));

  return (
    <div className="min-h-screen flex w-full bg-secondary/30">
      <aside className="w-64 shrink-0 bg-card border-r border-border flex flex-col">
        <div className="p-6 flex flex-col items-center text-center gap-4 border-b border-border">
          <img src={logo} alt="Merryblue" className="h-12 w-auto object-contain shrink-0 p-2 bg-orange-500/5 border border-orange-500/20 rounded-xl shadow-sm" />
          <div className="inline-flex px-3 py-1.5 bg-orange-500/10 border border-orange-500/20 rounded-md text-sm font-semibold text-orange-600 dark:text-orange-400">
            {isAdmin ? t('admin.adminPanel') : t('admin.hrPanel')}
          </div>
        </div>
        <nav className="flex-1 p-3 space-y-1">
          {items.map((it) => (
            <NavLink key={it.to} to={it.to} end={it.end}
              className={({ isActive }) => `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-smooth ${isActive ? 'bg-gradient-primary text-primary-foreground shadow-soft' : 'text-foreground/70 hover:bg-muted'}`}>
              <it.icon className="h-4 w-4" />{it.label}
            </NavLink>
          ))}
        </nav>
        <div className="p-3 border-t border-border space-y-1">
          <Button asChild variant="ghost" className="w-full justify-start"><a href="/"><Home className="mr-2 h-4 w-4" />{t('admin.viewSite')}</a></Button>
          <Button onClick={signOut} variant="ghost" className="w-full justify-start"><LogOut className="mr-2 h-4 w-4" />{t('admin.signOut')}</Button>
        </div>
      </aside>
      <main className="flex-1 overflow-auto">
        <div className="sticky top-0 z-30 flex justify-end items-center gap-2 px-6 py-3 bg-background/80 backdrop-blur border-b border-border">
          <AdminNotifications />
        </div>
        <Outlet />
      </main>
    </div>
  );
};
export default AdminLayout;
