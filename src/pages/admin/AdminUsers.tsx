import { useEffect, useState } from 'react';
import { supabase } from '@/integrations/supabase/client';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Plus, Pencil, Trash2, Shield, UserCog, User as UserIcon } from 'lucide-react';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';


type Role = 'admin' | 'hr' | 'user';
interface UserRow { user_id: string; display_name: string | null; phone: string | null; created_at: string; role: Role; email?: string | null; }

const roleIcon = (r: Role) => r === 'admin' ? Shield : r === 'hr' ? UserCog : UserIcon;
const roleColor = (r: Role) => r === 'admin' ? 'bg-gradient-primary text-primary-foreground' : r === 'hr' ? 'bg-accent text-accent-foreground' : 'bg-secondary text-secondary-foreground';


const AdminUsers = () => {
  const { t, i18n } = useTranslation();
  const [rows, setRows] = useState<UserRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState<{ user_id?: string; email: string; password: string; display_name: string; phone: string; role: Role } | null>(null);

  const load = async () => {
    setLoading(true);
    const [{ data: profiles }, { data: roles }] = await Promise.all([
      supabase.from('profiles').select('user_id, display_name, phone, created_at').order('created_at', { ascending: false }),
      supabase.from('user_roles').select('user_id, role'),
    ]);
    const roleMap = new Map<string, Role>();
    (roles ?? []).forEach((r: any) => {
      const cur = roleMap.get(r.user_id);
      if (!cur || (cur === 'user') || (cur === 'hr' && r.role === 'admin')) roleMap.set(r.user_id, r.role);
    });
    setRows((profiles ?? []).map((p: any) => ({ ...p, role: roleMap.get(p.user_id) ?? 'user' })));
    setLoading(false);
  };
  useEffect(() => { load(); }, []);

  const openNew = () => { setCreating(true); setEditing({ email: '', password: '', display_name: '', phone: '', role: 'user' }); setOpen(true); };
  const openEdit = (r: UserRow) => { setCreating(false); setEditing({ user_id: r.user_id, email: '', password: '', display_name: r.display_name ?? '', phone: r.phone ?? '', role: r.role }); setOpen(true); };

  const save = async () => {
    if (!editing) return;
    if (creating) {
      if (!editing.email || !editing.password) return toast.error(t('admin.users.requireEmailPwd'));
      const { data, error } = await supabase.functions.invoke('admin-create-user', {
        body: {
          email: editing.email,
          password: editing.password,
          display_name: editing.display_name,
          phone: editing.phone,
          role: editing.role,
        },
      });
      if (error || (data as any)?.error) return toast.error((data as any)?.error || error?.message || 'Tạo thất bại');
      toast.success(`Đã tạo người dùng ${editing.role.toUpperCase()} mới thành công`);
    } else if (editing.user_id) {
      await supabase.from('profiles').update({ display_name: editing.display_name || null, phone: editing.phone || null }).eq('user_id', editing.user_id);
      await supabase.from('user_roles').delete().eq('user_id', editing.user_id);
      const { error } = await supabase.from('user_roles').insert({ user_id: editing.user_id, role: editing.role });
      if (error) return toast.error(error.message);
      toast.success(t('admin.users.updated'));
    }
    setOpen(false);
    load();
  };

  const deleteUser = async (user_id: string, name: string | null) => {
    if (!confirm(`Xoá vĩnh viễn người dùng "${name || user_id}"? Hành động này không thể hoàn tác.`)) return;
    const { data, error } = await supabase.functions.invoke('admin-delete-user', { body: { user_id } });
    if (error || (data as any)?.error) return toast.error((data as any)?.error || error?.message || 'Xoá thất bại');
    toast.success('Đã xoá người dùng');
    load();
  };

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="font-display text-3xl font-bold">{t('admin.users.title')}</h1>
          <p className="text-muted-foreground mt-1 text-sm">{t('admin.users.subtitle')}</p>
        </div>
        <Button onClick={openNew} className="bg-gradient-primary text-primary-foreground"><Plus className="mr-2 h-4 w-4" />{t('admin.users.createBtn')}</Button>
      </div>

      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-secondary/60">
            <tr>
              <th className="text-left px-4 py-3 font-semibold">{t('admin.users.name')}</th>
              <th className="text-left px-4 py-3 font-semibold">{t('admin.users.phone')}</th>
              <th className="text-left px-4 py-3 font-semibold">{t('admin.users.role')}</th>
              <th className="text-left px-4 py-3 font-semibold">{t('admin.users.createdAt')}</th>
              <th className="px-4 py-3 w-32"></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan={5} className="px-4 py-12 text-center text-muted-foreground">{t('admin.users.loading')}</td></tr>}
            {!loading && rows.length === 0 && <tr><td colSpan={5} className="px-4 py-12 text-center text-muted-foreground">{t('admin.users.empty')}</td></tr>}
            {rows.map((r) => {
              const Icon = roleIcon(r.role);
              return (
                <tr key={r.user_id} className="border-t border-border hover:bg-secondary/40">
                  <td className="px-4 py-3 font-medium">{r.display_name || '—'}</td>
                  <td className="px-4 py-3 text-muted-foreground">{r.phone || '—'}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold ${roleColor(r.role)}`}>
                      <Icon className="h-3 w-3" />{r.role.toUpperCase()}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-muted-foreground">{new Date(r.created_at).toLocaleDateString(i18n.language === 'vi' ? 'vi-VN' : 'en-US')}</td>
                  <td className="px-4 py-3 text-right space-x-1">
                    <Button size="icon" variant="ghost" onClick={() => openEdit(r)} title={t('admin.users.editRole')}><Pencil className="h-4 w-4" /></Button>
                    <Button size="icon" variant="ghost" onClick={() => deleteUser(r.user_id, r.display_name)} title="Xoá người dùng">
                      <Trash2 className="h-4 w-4 text-destructive" />
                    </Button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader><DialogTitle>{creating ? t('admin.users.createTitle') : t('admin.users.updateTitle')}</DialogTitle></DialogHeader>
          {editing && (
            <div className="space-y-4">
              {creating && (
                <>
                  <div>
                    <Label>{t('admin.users.email')}</Label>
                    <Input type="email" value={editing.email} onChange={(e) => setEditing({ ...editing, email: e.target.value })} placeholder="hr@merryblue.llc" />
                  </div>
                  <div>
                    <Label>{t('admin.users.password')}</Label>
                    <Input type="password" value={editing.password} onChange={(e) => setEditing({ ...editing, password: e.target.value })} placeholder={t('admin.users.passwordHint')} />
                  </div>
                </>
              )}
              <div>
                <Label>{t('admin.users.name')}</Label>
                <Input value={editing.display_name} onChange={(e) => setEditing({ ...editing, display_name: e.target.value })} />
              </div>
              <div>
                <Label>{t('admin.users.phone')}</Label>
                <Input value={editing.phone} onChange={(e) => setEditing({ ...editing, phone: e.target.value })} />
              </div>
              <div>
                <Label>{t('admin.users.role')}</Label>
                <Select value={editing.role} onValueChange={(v) => setEditing({ ...editing, role: v as Role })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="user">{t('admin.users.roleUser')}</SelectItem>
                    <SelectItem value="hr">{t('admin.users.roleHr')}</SelectItem>
                    <SelectItem value="admin">{t('admin.users.roleAdmin')}</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              {creating && (
                <p className="text-xs text-muted-foreground">{t('admin.users.createNote')}</p>
              )}
            </div>
          )}
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpen(false)}>{t('admin.common.cancel')}</Button>
            <Button onClick={save} className="bg-gradient-primary text-primary-foreground">{t('admin.common.save')}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default AdminUsers;
