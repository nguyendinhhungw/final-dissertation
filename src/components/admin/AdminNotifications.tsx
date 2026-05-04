import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { supabase } from '@/integrations/supabase/client';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from 'sonner';
import { Bell, FileText, MessageSquare } from 'lucide-react';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { motion, AnimatePresence } from 'framer-motion';
import { useTranslation } from 'react-i18next';

type Item = {
  id: string;
  kind: 'application' | 'contact';
  title: string;
  description: string;
  link: string;
  createdAt: string;
  unread: boolean;
};

const STORAGE_KEY = 'mb-admin-notif-seen';

const AdminNotifications = () => {
  const { t, i18n } = useTranslation();
  const { isAdmin, isHr, user } = useAuth();
  const [items, setItems] = useState<Item[]>([]);
  const [open, setOpen] = useState(false);
  const seenRef = useRef<Set<string>>(new Set());
  const initializedRef = useRef(false);

  // Load seen ids from localStorage so toasts don't replay on refresh.
  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) seenRef.current = new Set(JSON.parse(raw));
    } catch {}
  }, []);

  const persistSeen = () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(Array.from(seenRef.current)));
    } catch {}
  };

  const [pendingCount, setPendingCount] = useState(0);

  const loadPendingCount = async () => {
    const [appsRes, contactsRes] = await Promise.all([
      supabase.from('applications').select('id', { count: 'exact', head: true }).eq('status', 'submitted'),
      supabase.from('contacts').select('id', { count: 'exact', head: true }).eq('is_read', false),
    ]);
    setPendingCount((appsRes.count ?? 0) + (contactsRes.count ?? 0));
  };

  useEffect(() => {
    if (!user || (!isAdmin && !isHr)) return;

    let cancelled = false;
    loadPendingCount();

    const loadInitial = async () => {
      const since = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString();
      const [appsRes, contactsRes] = await Promise.all([
        supabase
          .from('applications')
          .select('id, full_name, email, created_at, job_id, status')
          .gte('created_at', since)
          .order('created_at', { ascending: false })
          .limit(20),
        isAdmin
          ? supabase
              .from('contacts')
              .select('id, name, email, subject, created_at, is_read')
              .gte('created_at', since)
              .order('created_at', { ascending: false })
              .limit(20)
          : Promise.resolve({ data: [] as any[] } as any),
      ]);

      if (cancelled) return;
      const merged: Item[] = [];
      (appsRes.data ?? []).forEach((a: any) => {
        merged.push({
          id: `app:${a.id}`,
          kind: 'application',
          title: t('admin.notif.newApp'),
          description: `${a.full_name} (${a.email})`,
          link: '/admin/applications',
          createdAt: a.created_at,
          unread: a.status === 'submitted',
        });
      });
      ((contactsRes as any)?.data ?? []).forEach((c: any) => {
        merged.push({
          id: `contact:${c.id}`,
          kind: 'contact',
          title: t('admin.notif.newContact'),
          description: `${c.name}${c.subject ? ' — ' + c.subject : ''}`,
          link: '/admin/contacts',
          createdAt: c.created_at,
          unread: c.is_read === false,
        });
      });
      merged.sort((a, b) => +new Date(b.createdAt) - +new Date(a.createdAt));
      if (!initializedRef.current) {
        merged.forEach((m) => seenRef.current.add(m.id));
        persistSeen();
      }
      setItems(merged.slice(0, 30));
      initializedRef.current = true;
    };

    loadInitial();

    const pushItem = (item: Item, showToast: boolean) => {
      setItems((prev) => {
        if (prev.some((p) => p.id === item.id)) return prev;
        return [item, ...prev].slice(0, 30);
      });
      if (showToast && !seenRef.current.has(item.id)) {
        seenRef.current.add(item.id);
        persistSeen();
        toast.success(item.title, {
          description: item.description,
          action: { label: t('admin.notif.view'), onClick: () => (window.location.href = item.link) },
        });
      }
    };

    const updateItem = (id: string, patch: Partial<Item>) => {
      setItems((prev) => prev.map((p) => (p.id === id ? { ...p, ...patch } : p)));
    };

    const channel = supabase
      .channel('admin-notifications')
      .on(
        'postgres_changes',
        { event: 'INSERT', schema: 'public', table: 'applications' },
        (payload) => {
          const a: any = payload.new;
          pushItem(
            {
              id: `app:${a.id}`,
              kind: 'application',
              title: t('admin.notif.newApp'),
              description: `${a.full_name} (${a.email})`,
              link: '/admin/applications',
              createdAt: a.created_at,
              unread: a.status === 'submitted',
            },
            initializedRef.current,
          );
          loadPendingCount();
        },
      )
      .on('postgres_changes', { event: 'UPDATE', schema: 'public', table: 'applications' }, (payload) => {
        const a: any = payload.new;
        updateItem(`app:${a.id}`, { unread: a.status === 'submitted' });
        loadPendingCount();
      })
      .on('postgres_changes', { event: 'DELETE', schema: 'public', table: 'applications' }, (payload) => {
        const a: any = payload.old;
        if (a?.id) setItems((prev) => prev.filter((p) => p.id !== `app:${a.id}`));
        loadPendingCount();
      });

    if (isAdmin) {
      channel
        .on(
          'postgres_changes',
          { event: 'INSERT', schema: 'public', table: 'contacts' },
          (payload) => {
            const c: any = payload.new;
            pushItem(
              {
                id: `contact:${c.id}`,
                kind: 'contact',
                title: t('admin.notif.newContact'),
                description: `${c.name}${c.subject ? ' — ' + c.subject : ''}`,
                link: '/admin/contacts',
                createdAt: c.created_at,
                unread: c.is_read === false,
              },
              initializedRef.current,
            );
            loadPendingCount();
          },
        )
        .on('postgres_changes', { event: 'UPDATE', schema: 'public', table: 'contacts' }, (payload) => {
          const c: any = payload.new;
          updateItem(`contact:${c.id}`, { unread: c.is_read === false });
          loadPendingCount();
        })
        .on('postgres_changes', { event: 'DELETE', schema: 'public', table: 'contacts' }, (payload) => {
          const c: any = payload.old;
          if (c?.id) setItems((prev) => prev.filter((p) => p.id !== `contact:${c.id}`));
          loadPendingCount();
        });
    }

    channel.subscribe();

    return () => {
      cancelled = true;
      supabase.removeChannel(channel);
    };
  }, [user, isAdmin, isHr]);

  const badge = pendingCount;

  const handleOpenChange = (v: boolean) => {
    setOpen(v);
    if (v) loadPendingCount();
  };

  if (!user || (!isAdmin && !isHr)) return null;

  return (
    <Popover open={open} onOpenChange={handleOpenChange}>
      <PopoverTrigger asChild>
        <Button variant="ghost" size="icon" className="relative" aria-label={t('admin.notif.aria')}>
          <motion.span
            key={badge}
            initial={badge > 0 ? { rotate: -15 } : false}
            animate={badge > 0 ? { rotate: [0, -15, 12, -8, 6, 0] } : { rotate: 0 }}
            transition={{ duration: 0.7 }}
            className="inline-flex"
          >
            <Bell className="h-5 w-5" />
          </motion.span>
          <AnimatePresence>
            {badge > 0 && (
              <motion.span
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                exit={{ scale: 0 }}
                className="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 rounded-full bg-primary text-primary-foreground text-[10px] font-bold flex items-center justify-center shadow-soft"
              >
                {badge > 99 ? '99+' : badge}
              </motion.span>
            )}
          </AnimatePresence>
        </Button>
      </PopoverTrigger>
      <PopoverContent align="end" className="w-80 p-0">
        <div className="px-4 py-3 border-b border-border">
          <div className="font-display font-bold">{t('admin.notif.title')}</div>
          <div className="text-xs text-muted-foreground">{t('admin.notif.subtitle')}</div>
        </div>
        <ScrollArea className="max-h-96">
          {items.length === 0 ? (
            <div className="p-8 text-center text-sm text-muted-foreground">{t('admin.notif.empty')}</div>
          ) : (
            <ul className="divide-y divide-border">
              {items.map((it) => (
                <li key={it.id}>
                  <Link
                    to={it.link}
                    onClick={() => setOpen(false)}
                    className={`flex items-start gap-3 p-3 transition-smooth ${it.unread ? 'bg-primary/5 hover:bg-primary/10 border-l-2 border-primary' : 'hover:bg-muted'}`}
                  >
                    <div className={`shrink-0 h-9 w-9 rounded-full grid place-items-center ${it.kind === 'application' ? 'bg-primary/10 text-primary' : 'bg-accent text-accent-foreground'}`}>
                      {it.kind === 'application' ? <FileText className="h-4 w-4" /> : <MessageSquare className="h-4 w-4" />}
                    </div>
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2">
                        <div className={`text-sm truncate ${it.unread ? 'font-bold text-foreground' : 'font-semibold'}`}>{it.title}</div>
                        {it.unread && <span className="shrink-0 h-2 w-2 rounded-full bg-primary" aria-hidden />}
                      </div>
                      <div className={`text-xs truncate ${it.unread ? 'text-foreground/80' : 'text-muted-foreground'}`}>{it.description}</div>
                      <div className="text-[10px] text-muted-foreground mt-0.5">
                        {new Date(it.createdAt).toLocaleString(i18n.language === 'vi' ? 'vi-VN' : 'en-US')}
                      </div>
                    </div>
                  </Link>
                </li>
              ))}
            </ul>
          )}
        </ScrollArea>
      </PopoverContent>
    </Popover>
  );
};

export default AdminNotifications;
