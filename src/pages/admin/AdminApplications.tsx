import { useEffect, useState } from 'react';
import { supabase } from '@/integrations/supabase/client';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Download, Send, Eye } from 'lucide-react';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';
import CvViewerDialog from '@/components/CvViewerDialog';

const AdminApplications = () => {
  const { t, i18n } = useTranslation();
  const [rows, setRows] = useState<any[]>([]);
  const [notify, setNotify] = useState<any | null>(null);
  const [msg, setMsg] = useState({ title: '', message: '' });
  const [cvPath, setCvPath] = useState<string | null>(null);

  const load = () => supabase.from('applications').select('*, jobs(title_vi, title_en, slug)').order('created_at', { ascending: false }).then(({ data }) => setRows(data ?? []));
  useEffect(() => { load(); }, []);

  const downloadCv = async (path: string) => {
    const { data, error } = await supabase.storage.from('cv-uploads').download(path);
    if (error || !data) return toast.error(error?.message || 'Failed');
    const blob = data.type ? data : new Blob([data], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = path.split('/').pop() || 'cv.pdf';
    document.body.appendChild(a);
    a.click();
    a.remove();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  };

  const updateStatus = async (id: string, status: string) => {
    await supabase.from('applications').update({ status }).eq('id', id);
    load();
  };

  const sendNotification = async () => {
    if (!notify) return;
    const { error } = await supabase.from('notifications').insert({
      user_id: notify.user_id, title: msg.title, message: msg.message, link: '/my-applications',
    });
    if (error) return toast.error(error.message);
    // Persist the message on the application so it shows on the candidate's My Applications page
    await supabase.from('applications').update({ admin_notes: msg.message }).eq('id', notify.id);
    toast.success(t('admin.applications.notifSent'));
    setNotify(null); setMsg({ title: '', message: '' });
    load();
  };

  return (
    <div className="p-8">
      <h1 className="font-display text-3xl font-bold mb-6">{t('admin.applications.title')}</h1>
      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-secondary/60"><tr>
            <th className="text-left px-4 py-3">{t('admin.applications.candidate')}</th>
            <th className="text-left px-4 py-3">{t('admin.applications.job')}</th>
            <th className="text-left px-4 py-3">{t('admin.applications.email')}</th>
            <th className="text-left px-4 py-3">{t('admin.applications.status')}</th>
            <th className="text-left px-4 py-3">{t('admin.applications.date')}</th>
            <th></th>
          </tr></thead>
          <tbody>
            {rows.map((r) => {
              const isGuest = !r.user_id;
              const jobTitle = i18n.language === 'vi' ? r.jobs?.title_vi : (r.jobs?.title_en || r.jobs?.title_vi);
              return (
              <tr key={r.id} className={`border-t border-border ${isGuest ? 'bg-muted/40' : ''}`}>
                <td className="px-4 py-3 font-medium">
                  <div className="flex items-center gap-2">
                    <span>{r.full_name}</span>
                    {isGuest ? (
                      <Badge variant="outline">{t('admin.applications.guest')}</Badge>
                    ) : (
                      <Badge variant="secondary" className="text-xs">{t('admin.applications.member')}</Badge>
                    )}
                  </div>
                </td>
                <td className="px-4 py-3">{jobTitle}</td>
                <td className="px-4 py-3 text-muted-foreground">{r.email}</td>
                <td className="px-4 py-3">
                  <select value={r.status} onChange={(e) => updateStatus(r.id, e.target.value)} className="bg-transparent border border-border rounded px-2 py-1">
                    <option value="submitted">{t('admin.applications.submitted')}</option>
                    <option value="reviewing">{t('admin.applications.reviewing')}</option>
                    <option value="interview">{t('admin.applications.interview')}</option>
                    <option value="offered">{t('admin.applications.offered')}</option>
                    <option value="rejected">{t('admin.applications.rejected')}</option>
                  </select>
                </td>
                <td className="px-4 py-3 text-muted-foreground">{new Date(r.created_at).toLocaleDateString(i18n.language === 'vi' ? 'vi-VN' : 'en-US')}</td>
                <td className="px-4 py-3 text-right space-x-1">
                  {r.cv_path && (
                    <>
                      <Button size="sm" variant="ghost" title={i18n.language === 'vi' ? 'Xem CV' : 'View CV'} onClick={() => setCvPath(r.cv_path)}><Eye className="h-4 w-4" /></Button>
                      <Button size="sm" variant="ghost" title={i18n.language === 'vi' ? 'Tải về' : 'Download'} onClick={() => downloadCv(r.cv_path)}><Download className="h-4 w-4" /></Button>
                    </>
                  )}
                  {!isGuest && (
                    <Button size="sm" variant="ghost" onClick={() => { setNotify(r); setMsg({ title: t('admin.applications.notifDefaultTitle'), message: `${t('admin.applications.notifDefaultGreeting')} ${r.full_name}, ` }); }}><Send className="h-4 w-4" /></Button>
                  )}
                </td>
              </tr>
              );
            })}
            {rows.length === 0 && <tr><td colSpan={6} className="px-4 py-12 text-center text-muted-foreground">{t('admin.applications.empty')}</td></tr>}
          </tbody>
        </table>
      </div>

      <Dialog open={!!notify} onOpenChange={(o) => !o && setNotify(null)}>
        <DialogContent>
          <DialogHeader><DialogTitle>{t('admin.applications.sendNotif')} {notify?.full_name}</DialogTitle></DialogHeader>
          <div className="space-y-4">
            <div><Label>{t('admin.applications.notifTitle')}</Label><Input value={msg.title} onChange={(e) => setMsg({ ...msg, title: e.target.value })} /></div>
            <div><Label>{t('admin.applications.notifMessage')}</Label><Textarea rows={4} value={msg.message} onChange={(e) => setMsg({ ...msg, message: e.target.value })} /></div>
          </div>
          <DialogFooter><Button onClick={sendNotification} className="bg-gradient-primary text-primary-foreground">{t('admin.applications.send')}</Button></DialogFooter>
        </DialogContent>
      </Dialog>

      <CvViewerDialog path={cvPath} onOpenChange={(o) => !o && setCvPath(null)} />
    </div>
  );
};
export default AdminApplications;
