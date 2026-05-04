import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Globe, Menu, X, Bell, User, LayoutDashboard, LogOut, FileText, MessageSquare } from 'lucide-react';
import { useEffect, useState } from 'react';
import logo from '@/assets/logo.png';
import { motion, AnimatePresence } from 'framer-motion';
import { supabase } from '@/integrations/supabase/client';
import { toast } from 'sonner';

type NotifItem = {
  id: string;
  kind: 'application' | 'contact' | 'personal';
  title: string;
  description: string;
  link: string;
  createdAt: string;
  pending: boolean;
};


const Header = () => {
  const { t, i18n } = useTranslation();
  const { lang, toggle } = useLang();
  const { user, isAdmin, isHr, signOut } = useAuth();
  const staff = isAdmin || isHr;
  const [open, setOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [unread, setUnread] = useState(0);
  const [notifItems, setNotifItems] = useState<NotifItem[]>([]);
  const [notifOpen, setNotifOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();


  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  useEffect(() => { setOpen(false); }, [location.pathname]);

  useEffect(() => {
    if (!user) { setUnread(0); setNotifItems([]); return; }

    const loadCount = async () => {
      if (staff) {
        const [{ count: appsCount }, { count: contactsCount }] = await Promise.all([
          supabase.from('applications').select('id', { count: 'exact', head: true }).eq('status', 'submitted'),
          supabase.from('contacts').select('id', { count: 'exact', head: true }).eq('is_read', false),
        ]);
        setUnread((appsCount ?? 0) + (contactsCount ?? 0));
      } else {
        const { count } = await supabase.from('notifications').select('id', { count: 'exact', head: true })
          .eq('user_id', user.id).eq('is_read', false);
        setUnread(count ?? 0);
      }
    };

    const loadList = async () => {
      if (staff) {
        const since = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString();
        const [appsRes, contactsRes] = await Promise.all([
          supabase.from('applications').select('id, full_name, email, created_at, status')
            .gte('created_at', since).order('created_at', { ascending: false }).limit(15),
          isAdmin
            ? supabase.from('contacts').select('id, name, subject, created_at, is_read')
                .gte('created_at', since).order('created_at', { ascending: false }).limit(15)
            : Promise.resolve({ data: [] as any[] } as any),
        ]);
        const merged: NotifItem[] = [];
        (appsRes.data ?? []).forEach((a: any) => merged.push({
          id: `app:${a.id}`, kind: 'application',
          title: t('admin.notif.newApp'),
          description: `${a.full_name} (${a.email})`,
          link: '/admin/applications', createdAt: a.created_at,
          pending: a.status === 'submitted',
        }));
        ((contactsRes as any).data ?? []).forEach((c: any) => merged.push({
          id: `contact:${c.id}`, kind: 'contact',
          title: t('admin.notif.newContact'),
          description: `${c.name}${c.subject ? ' — ' + c.subject : ''}`,
          link: '/admin/contacts', createdAt: c.created_at,
          pending: c.is_read === false,
        }));
        merged.sort((a, b) => {
          if (a.pending !== b.pending) return a.pending ? -1 : 1;
          return +new Date(b.createdAt) - +new Date(a.createdAt);
        });
        setNotifItems(merged.slice(0, 20));
      } else {
        const { data } = await supabase.from('notifications').select('id, title, message, link, created_at, is_read')
          .eq('user_id', user.id).order('created_at', { ascending: false }).limit(20);
        const mapped: NotifItem[] = (data ?? []).map((n: any) => ({
          id: `notif:${n.id}`, kind: 'personal',
          title: n.title, description: n.message,
          link: n.link || '/notifications', createdAt: n.created_at,
          pending: n.is_read === false,
        }));
        mapped.sort((a, b) => {
          if (a.pending !== b.pending) return a.pending ? -1 : 1;
          return +new Date(b.createdAt) - +new Date(a.createdAt);
        });
        setNotifItems(mapped);
      }
    };

    loadCount();
    loadList();

    const channel = supabase.channel(`user-notifs:${user.id}`);

    channel.on(
      'postgres_changes',
      { event: 'INSERT', schema: 'public', table: 'notifications', filter: `user_id=eq.${user.id}` },
      (payload) => {
        const n: any = payload.new;
        toast.success(n.title, {
          description: n.message,
          action: n.link ? { label: t('common.viewAll'), onClick: () => navigate(n.link) } : undefined,
        });
        loadCount(); loadList();
      },
    );
    channel.on('postgres_changes', { event: 'UPDATE', schema: 'public', table: 'notifications', filter: `user_id=eq.${user.id}` }, () => { loadCount(); loadList(); });
    channel.on('postgres_changes', { event: 'DELETE', schema: 'public', table: 'notifications', filter: `user_id=eq.${user.id}` }, () => { loadCount(); loadList(); });

    if (staff) {
      channel.on('postgres_changes', { event: '*', schema: 'public', table: 'applications' }, () => { loadCount(); loadList(); });
      channel.on('postgres_changes', { event: '*', schema: 'public', table: 'contacts' }, () => { loadCount(); loadList(); });
    }

    channel.subscribe();
    return () => { supabase.removeChannel(channel); };
  }, [user, staff, isAdmin, navigate, t]);


  const links = [
    { to: '/about', label: t('nav.about') },
    { to: '/services', label: t('nav.services') },
    { to: '/portfolio', label: t('nav.portfolio') },
    { to: '/blog', label: t('nav.blog') },
    { to: '/recruitment', label: t('nav.recruitment') },
    { to: '/contact', label: t('nav.contact') },
  ];

  return (
    <motion.header
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.5 }}
      className={`fixed top-0 inset-x-0 z-50 transition-smooth ${
        scrolled ? 'bg-background/85 backdrop-blur-md border-b border-border shadow-soft' : 'bg-transparent'
      }`}
    >
      <div className="container-tight flex h-20 items-center justify-between gap-6">
        <Link to="/" className="flex items-center gap-2 shrink-0">
          <img src={logo} alt="Merryblue" className="h-12 w-auto object-contain shrink-0 p-1.5 bg-orange-500/5 border border-orange-500/20 rounded-xl shadow-sm" />
        </Link>

        <nav className="hidden lg:flex items-center gap-8">
          {links.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              className={`text-sm font-medium underline-grow transition-smooth ${
                location.pathname.startsWith(l.to) ? 'text-primary' : 'text-foreground/80 hover:text-foreground'
              }`}
            >
              {l.label}
            </Link>
          ))}
        </nav>

        <div className="flex items-center gap-2">
          <button
            onClick={toggle}
            className="hidden sm:flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-foreground/80 hover:text-primary transition-smooth"
          >
            <Globe className="h-4 w-4" />
            {lang === 'vi' ? 'VI' : 'EN'}
          </button>

          {user ? (
            <>
              <Popover
                open={notifOpen}
                onOpenChange={async (o) => {
                  setNotifOpen(o);
                  if (o && user && !staff && unread > 0) {
                    await supabase
                      .from('notifications')
                      .update({ is_read: true })
                      .eq('user_id', user.id)
                      .eq('is_read', false);
                    setUnread(0);
                    setNotifItems((prev) => prev.map((n) => ({ ...n, pending: false })));
                  }
                }}
              >
                <PopoverTrigger asChild>
                  <button type="button" className="relative p-2 hover:bg-muted rounded-full transition-smooth" aria-label={t('nav.notifications')}>
                    <motion.span
                      key={unread}
                      initial={unread > 0 ? { rotate: -15 } : false}
                      animate={unread > 0 ? { rotate: [0, -15, 12, -8, 6, 0] } : { rotate: 0 }}
                      transition={{ duration: 0.7 }}
                      className="inline-flex"
                    >
                      <Bell className="h-5 w-5" />
                    </motion.span>
                    <AnimatePresence>
                      {unread > 0 && (
                        <motion.span
                          initial={{ scale: 0 }}
                          animate={{ scale: 1 }}
                          exit={{ scale: 0 }}
                          className="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 rounded-full bg-primary text-[10px] font-bold text-primary-foreground flex items-center justify-center shadow-soft"
                        >
                          {unread > 99 ? '99+' : unread}
                        </motion.span>
                      )}
                    </AnimatePresence>
                  </button>
                </PopoverTrigger>
                <PopoverContent align="end" className="w-80 p-0">
                  <div className="px-4 py-3 border-b border-border flex items-center justify-between">
                    <div>
                      <div className="font-display font-bold">{t('admin.notif.title')}</div>
                      <div className="text-xs text-muted-foreground">
                        {staff ? t('admin.notif.subtitle') : t('nav.notifications')}
                      </div>
                    </div>
                    <Link to="/notifications" onClick={() => setNotifOpen(false)} className="text-xs font-medium text-primary hover:underline">
                      {t('common.viewAll')}
                    </Link>
                  </div>
                  <ScrollArea className="max-h-96">
                    {notifItems.length === 0 ? (
                      <div className="p-8 text-center text-sm text-muted-foreground">{t('admin.notif.empty')}</div>
                    ) : (
                      <ul className="divide-y divide-border">
                        {notifItems.map((it) => (
                          <li key={it.id}>
                            <Link
                              to={it.link}
                              onClick={() => setNotifOpen(false)}
                              className={`flex items-start gap-3 p-3 transition-smooth ${it.pending ? 'bg-primary/5 hover:bg-primary/10 border-l-2 border-primary' : 'hover:bg-muted'}`}
                            >
                              <div className={`shrink-0 h-9 w-9 rounded-full grid place-items-center ${it.kind === 'application' ? 'bg-primary/10 text-primary' : it.kind === 'contact' ? 'bg-accent text-accent-foreground' : 'bg-muted text-foreground'}`}>
                                {it.kind === 'contact' ? <MessageSquare className="h-4 w-4" /> : <FileText className="h-4 w-4" />}
                              </div>
                              <div className="min-w-0 flex-1">
                                <div className="flex items-center gap-2">
                                  <div className={`text-sm truncate ${it.pending ? 'font-bold text-foreground' : 'font-semibold'}`}>{it.title}</div>
                                  {it.pending && <span className="shrink-0 h-2 w-2 rounded-full bg-primary animate-pulse" />}
                                </div>
                                <div className="text-xs text-muted-foreground truncate">{it.description}</div>
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

              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon" className="rounded-full">
                    <User className="h-5 w-5" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <div className="px-2 py-1.5 text-xs text-muted-foreground truncate">{user.email}</div>
                  <DropdownMenuSeparator />
                  {isAdmin && (
                    <DropdownMenuItem onClick={() => navigate('/admin')}>
                      <LayoutDashboard className="mr-2 h-4 w-4" />{t('nav.admin')}
                    </DropdownMenuItem>
                  )}
                  {!staff && (
                    <DropdownMenuItem onClick={() => navigate('/my-applications')}>
                      <FileText className="mr-2 h-4 w-4" />{t('nav.myApps')}
                    </DropdownMenuItem>
                  )}
                  <DropdownMenuItem onClick={() => navigate('/notifications')}>
                    <Bell className="mr-2 h-4 w-4" />{t('nav.notifications')}
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={signOut}>
                    <LogOut className="mr-2 h-4 w-4" />{t('nav.logout')}
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </>
          ) : (
            <Button asChild variant="ghost" className="hidden sm:inline-flex">
              <Link to="/auth">{t('nav.login')}</Link>
            </Button>
          )}

          <Button asChild className="hidden sm:inline-flex bg-gradient-primary hover:opacity-90 text-primary-foreground shadow-elegant">
            <Link to="/contact">{t('common.contactNow')}</Link>
          </Button>

          <button onClick={() => setOpen(!open)} className="lg:hidden p-2">
            {open ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>
        </div>
      </div>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }} exit={{ height: 0, opacity: 0 }}
            className="lg:hidden bg-background border-t border-border overflow-hidden"
          >
            <div className="container-tight py-6 flex flex-col gap-4">
              {links.map((l) => (
                <Link key={l.to} to={l.to} className="text-base font-medium py-2">{l.label}</Link>
              ))}
              <div className="flex items-center gap-3 pt-2 border-t border-border">
                <Button variant="outline" onClick={toggle}><Globe className="h-4 w-4 mr-1" />{lang === 'vi' ? 'VI' : 'EN'}</Button>
                {!user && <Button asChild className="flex-1 bg-gradient-primary text-primary-foreground"><Link to="/auth">{t('nav.login')}</Link></Button>}
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.header>
  );
};

export default Header;
