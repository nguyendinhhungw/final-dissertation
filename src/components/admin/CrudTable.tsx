import { useEffect, useState } from 'react';
import { supabase } from '@/integrations/supabase/client';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from '@/components/ui/dialog';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';

interface Field { name: string; label: string; type?: 'text' | 'textarea' | 'number' | 'switch' | 'array'; }

interface Props {
  table: string;
  title: string;
  fields: Field[];
  orderBy?: string;
  defaults?: Record<string, any>;
}

const CrudTable = ({ table, title, fields, orderBy = 'display_order', defaults = {} }: Props) => {
  const { t } = useTranslation();
  const [rows, setRows] = useState<any[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<any>(null);

  const load = async () => {
    const { data } = await (supabase as any).from(table).select('*').order(orderBy, { ascending: true });
    setRows(data ?? []);
  };
  useEffect(() => {
    load();
    // Live-refresh admin table when anyone (including this admin) changes data.
    const channel = supabase
      .channel(`admin:${table}:${Math.random().toString(36).slice(2, 9)}`)
      .on('postgres_changes', { event: '*', schema: 'public', table }, () => load())
      .subscribe();
    return () => { supabase.removeChannel(channel); };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [table]);

  const openNew = () => { setEditing({ ...defaults }); setOpen(true); };
  const openEdit = (row: any) => { setEditing({ ...row }); setOpen(true); };

  const save = async () => {
    const payload = { ...editing };
    fields.forEach((f) => {
      if (f.type === 'array' && typeof payload[f.name] === 'string') {
        payload[f.name] = payload[f.name].split(',').map((s: string) => s.trim()).filter(Boolean);
      } else if (f.type === 'number') {
        payload[f.name] = Number(payload[f.name] ?? 0);
      } else if (typeof payload[f.name] === 'string') {
        payload[f.name] = payload[f.name].trim();
      }
    });
    let res;
    if (editing.id) {
      const { id, created_at, updated_at, ...rest } = payload;
      res = await (supabase as any).from(table).update(rest).eq('id', id);
    } else {
      res = await (supabase as any).from(table).insert(payload);
    }
    if (res.error) return toast.error(res.error.message);
    toast.success(t('admin.common.saved'));
    setOpen(false);
    load();
  };

  const del = async (id: string) => {
    if (!confirm(t('admin.common.deleteConfirm'))) return;
    const { error } = await (supabase as any).from(table).delete().eq('id', id);
    if (error) return toast.error(error.message);
    load();
  };

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="font-display text-3xl font-bold">{title}</h1>
        <Button onClick={openNew} className="bg-gradient-primary text-primary-foreground"><Plus className="mr-2 h-4 w-4" />{t('admin.common.new')}</Button>
      </div>
      <div className="rounded-xl border border-border bg-card overflow-x-auto">
        <table className="w-full min-w-[640px] text-sm">
          <thead className="bg-secondary/60">
            <tr>
              {fields.slice(0, 4).map((f) => <th key={f.name} className="text-left px-4 py-3 font-semibold">{f.label}</th>)}
              <th className="px-4 py-3 w-28 text-right">{t('admin.common.actions')}</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} className="border-t border-border hover:bg-secondary/40">
                {fields.slice(0, 4).map((f) => (
                  <td key={f.name} className="px-4 py-3 max-w-xs truncate">
                    {f.type === 'switch' ? (r[f.name] ? '✓' : '—') : Array.isArray(r[f.name]) ? r[f.name].join(', ') : String(r[f.name] ?? '')}
                  </td>
                ))}
                <td className="px-4 py-3 text-right whitespace-nowrap">
                  <div className="inline-flex items-center gap-1 justify-end">
                    <Button size="icon" variant="ghost" onClick={() => openEdit(r)} aria-label="Edit"><Pencil className="h-4 w-4" /></Button>
                    <Button size="icon" variant="ghost" onClick={() => del(r.id)} aria-label="Delete"><Trash2 className="h-4 w-4 text-destructive" /></Button>
                  </div>
                </td>
              </tr>
            ))}
            {rows.length === 0 && <tr><td className="px-4 py-12 text-center text-muted-foreground" colSpan={5}>{t('admin.common.empty')}</td></tr>}
          </tbody>
        </table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-2xl max-h-[85vh] overflow-y-auto">
          <DialogHeader><DialogTitle>{editing?.id ? t('admin.common.editTitle') : t('admin.common.newTitle')} {title}</DialogTitle></DialogHeader>
          {editing && (
            <div className="space-y-4">
              {fields.map((f) => (
                <div key={f.name}>
                  <Label>{f.label}</Label>
                  {f.type === 'switch' ? (
                    <div className="pt-2"><Switch checked={!!editing[f.name]} onCheckedChange={(v) => setEditing({ ...editing, [f.name]: v })} /></div>
                  ) : f.type === 'textarea' ? (
                    <Textarea rows={4} value={editing[f.name] ?? ''} onChange={(e) => setEditing({ ...editing, [f.name]: e.target.value })} />
                  ) : (
                    <Input type={f.type === 'number' ? 'number' : 'text'}
                      value={Array.isArray(editing[f.name]) ? editing[f.name].join(', ') : (editing[f.name] ?? '')}
                      onChange={(e) => setEditing({ ...editing, [f.name]: e.target.value })} />
                  )}
                </div>
              ))}
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

export default CrudTable;
