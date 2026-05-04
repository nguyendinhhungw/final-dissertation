import { useEffect, useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { supabase } from '@/integrations/supabase/client';
import { useTranslation } from 'react-i18next';
import { Link, Navigate } from 'react-router-dom';
import { Reveal } from '@/components/motion/Reveal';
import { Bell, FileText, MessageSquare } from 'lucide-react';

type Item = {
  id: string;
  kind: 'application' | 'contact' | 'personal';
  title: string;
  description: string;
  link?: string;
  createdAt: string;
};

const Notifications = () => {
  const { user, isAdmin, isHr, loading } = useAuth();
  const { t, i18n } = useTranslation();
  const staff = isAdmin || isHr;
  const [items, setItems] = useState<Item[]>([]);

  useEffect(() => {
    if (!user) return;

    const load = async () => {
      if (staff) {
        const [appsRes, contactsRes] = await Promise.all([
          supabase
            .from('applications')
            .select('id, full_name, email, created_at')
            .order('created_at', { ascending: false })
            .limit(100),
          isAdmin || isHr
            ? supabase
                .from('contacts')
                .select('id, name, subject, created_at')
                .order('created_at', { ascending: false })
                .limit(100)
            : Promise.resolve({ data: [] as any[] } as any),
        ]);
        const merged: Item[] = [];
        (appsRes.data ?? []).forEach((a: any) =>
          merged.push({
            id: `app:${a.id}`,
            kind: 'application',
            title: t('admin.notif.newApp'),
            description: `${a.full_name} (${a.email})`,
            link: '/admin/applications',
            createdAt: a.created_at,
          }),
        );
        ((contactsRes as any).data ?? []).forEach((c: any) =>
          merged.push({
            id: `contact:${c.id}`,
            kind: 'contact',
            title: t('admin.notif.newContact'),
            description: `${c.name}${c.subject ? ' — ' + c.subject : ''}`,
            link: '/admin/contacts',
            createdAt: c.created_at,
          }),
        );
        merged.sort((a, b) => +new Date(b.createdAt) - +new Date(a.createdAt));
        setItems(merged);
      } else {
        const { data } = await supabase
          .from('notifications')
          .select('*')
          .eq('user_id', user.id)
          .order('created_at', { ascending: false });
        setItems(
          (data ?? []).map((n: any) => ({
            id: `notif:${n.id}`,
            kind: 'personal',
            title: n.title,
            description: n.message,
            link: n.link,
            createdAt: n.created_at,
          })),
        );
        // mark personal notifications as read
        await supabase
          .from('notifications')
          .update({ is_read: true })
          .eq('user_id', user.id)
          .eq('is_read', false);
      }
    };

    load();

    const channel = supabase.channel(`notif-page:${user.id}`);
    if (staff) {
      channel.on('postgres_changes', { event: '*', schema: 'public', table: 'applications' }, () => load());
      channel.on('postgres_changes', { event: '*', schema: 'public', table: 'contacts' }, () => load());
    } else {
      channel.on(
        'postgres_changes',
        { event: '*', schema: 'public', table: 'notifications', filter: `user_id=eq.${user.id}` },
        () => load(),
      );
    }
    channel.subscribe();
    return () => {
      supabase.removeChannel(channel);
    };
  }, [user, staff, isAdmin, isHr, t]);

  if (loading) return <div className="container-tight py-20">{t('common.loading')}</div>;
  if (!user) return <Navigate to="/auth?redirect=/notifications" replace />;

  const renderIcon = (kind: Item['kind']) => {
    if (kind === 'application') return <FileText className="h-5 w-5" />;
    if (kind === 'contact') return <MessageSquare className="h-5 w-5" />;
    return <Bell className="h-5 w-5" />;
  };

  return (
    <div className="container-tight py-20 max-w-3xl">
      <Reveal>
        <h1 className="font-display text-5xl font-bold">{t('nav.notifications')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
      </Reveal>
      <div className="mt-12 space-y-3">
        {items.map((n) => {
          const Wrapper: any = n.link ? Link : 'div';
          const wrapperProps = n.link ? { to: n.link } : {};
          return (
            <Wrapper
              key={n.id}
              {...wrapperProps}
              className={`p-5 rounded-2xl border border-border bg-card flex gap-4 ${n.link ? 'hover:bg-muted transition-smooth' : ''}`}
            >
              <div className={`grid place-items-center w-10 h-10 rounded-xl shrink-0 ${n.kind === 'application' ? 'bg-primary/10 text-primary' : n.kind === 'contact' ? 'bg-accent text-accent-foreground' : 'bg-accent text-accent-foreground'}`}>
                {renderIcon(n.kind)}
              </div>
              <div className="flex-1 min-w-0">
                <div className="font-semibold">{n.title}</div>
                <div className="text-sm text-muted-foreground mt-0.5 truncate">{n.description}</div>
                <div className="text-xs text-muted-foreground mt-2">
                  {new Date(n.createdAt).toLocaleString(i18n.language === 'vi' ? 'vi-VN' : 'en-US')}
                </div>
              </div>
            </Wrapper>
          );
        })}
        {items.length === 0 && <p className="text-muted-foreground">{t('admin.notif.empty')}</p>}
      </div>
    </div>
  );
};
export default Notifications;
