import { useEffect, useState } from 'react';
import { supabase } from '@/integrations/supabase/client';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { Trash2, StickyNote, Save, X, Pencil } from 'lucide-react';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';

const AdminContacts = () => {
  const { t, i18n } = useTranslation();
  const [rows, setRows] = useState<any[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [draft, setDraft] = useState('');
  const [saving, setSaving] = useState(false);

  const load = () =>
    (supabase as any)
      .from('contacts')
      .select('*')
      .order('created_at', { ascending: false })
      .then(({ data }: any) => setRows(data ?? []));

  useEffect(() => {
    load();
    const ch = supabase
      .channel('admin:contacts')
      .on('postgres_changes', { event: '*', schema: 'public', table: 'contacts' }, () => load())
      .subscribe();
    return () => { supabase.removeChannel(ch); };
  }, []);

  const toggleRead = async (id: string, v: boolean) => {
    await (supabase as any).from('contacts').update({ is_read: v }).eq('id', id);
    load();
  };
  const del = async (id: string) => {
    if (!confirm(t('admin.contacts.deleteConfirm'))) return;
    await (supabase as any).from('contacts').delete().eq('id', id);
    load();
  };

  const startEdit = (r: any) => {
    setEditingId(r.id);
    setDraft(r.admin_notes ?? '');
  };
  const cancelEdit = () => {
    setEditingId(null);
    setDraft('');
  };
  const saveNote = async (id: string) => {
    setSaving(true);
    const { error } = await (supabase as any)
      .from('contacts')
      .update({ admin_notes: draft.trim() === '' ? null : draft.trim() })
      .eq('id', id);
    setSaving(false);
    if (error) {
      toast.error(error.message);
      return;
    }
    toast.success(t('admin.contacts.noteSaved'));
    setEditingId(null);
    setDraft('');
    load();
  };

  return (
    <div className="p-8">
      <h1 className="font-display text-3xl font-bold mb-6">{t('admin.contacts.title')}</h1>
      <div className="space-y-3">
        {rows.map((r) => (
          <div
            key={r.id}
            className={`p-5 rounded-xl border bg-card ${r.is_read ? 'border-border' : 'border-primary/40'}`}
          >
            <div className="flex items-start justify-between gap-4 flex-wrap">
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2 flex-wrap">
                  <strong>{r.name}</strong>
                  <span className="text-sm text-muted-foreground">{r.email}</span>
                  {!r.is_read && <Badge>{t('admin.contacts.new')}</Badge>}
                  {r.admin_notes && (
                    <Badge variant="secondary" className="gap-1">
                      <StickyNote className="h-3 w-3" /> {t('admin.contacts.hasNote')}
                    </Badge>
                  )}
                </div>
                {r.subject && <div className="text-sm font-medium mt-1">{r.subject}</div>}
                <p className="mt-2 text-sm whitespace-pre-line">{r.message}</p>
                <div className="mt-2 text-xs text-muted-foreground">
                  {new Date(r.created_at).toLocaleString(i18n.language === 'vi' ? 'vi-VN' : 'en-US')}
                </div>
              </div>
              <div className="flex gap-1 shrink-0">
                <Button size="sm" variant="ghost" onClick={() => toggleRead(r.id, !r.is_read)}>
                  {r.is_read ? t('admin.contacts.markUnread') : t('admin.contacts.markRead')}
                </Button>
                <Button size="sm" variant="ghost" onClick={() => del(r.id)}>
                  <Trash2 className="h-4 w-4 text-destructive" />
                </Button>
              </div>
            </div>

            {/* Internal notes */}
            <div className="mt-4 pt-4 border-t border-border">
              <div className="flex items-center justify-between gap-2 mb-2">
                <div className="flex items-center gap-2 text-sm font-semibold text-muted-foreground">
                  <StickyNote className="h-4 w-4 text-primary" />
                  {t('admin.contacts.internalNotes')}
                </div>
                {editingId !== r.id && (
                  <Button size="sm" variant="outline" onClick={() => startEdit(r)}>
                    <Pencil className="h-3.5 w-3.5 mr-1.5" />
                    {r.admin_notes ? t('admin.contacts.editNote') : t('admin.contacts.addNote')}
                  </Button>
                )}
              </div>

              {editingId === r.id ? (
                <div className="space-y-2">
                  <Textarea
                    value={draft}
                    onChange={(e) => setDraft(e.target.value)}
                    rows={3}
                    maxLength={2000}
                    placeholder={t('admin.contacts.notePlaceholder')}
                  />
                  <div className="flex items-center justify-between">
                    <span className="text-xs text-muted-foreground">{draft.length}/2000</span>
                    <div className="flex gap-2">
                      <Button size="sm" variant="ghost" onClick={cancelEdit} disabled={saving}>
                        <X className="h-4 w-4 mr-1" />{t('admin.common.cancel')}
                      </Button>
                      <Button
                        size="sm"
                        onClick={() => saveNote(r.id)}
                        disabled={saving}
                        className="bg-gradient-primary text-primary-foreground"
                      >
                        <Save className="h-4 w-4 mr-1" />{t('admin.common.save')}
                      </Button>
                    </div>
                  </div>
                </div>
              ) : r.admin_notes ? (
                <p className="text-sm whitespace-pre-line bg-secondary/50 rounded-lg p-3 border border-border">
                  {r.admin_notes}
                </p>
              ) : (
                <p className="text-sm text-muted-foreground italic">{t('admin.contacts.noNote')}</p>
              )}
            </div>
          </div>
        ))}
        {rows.length === 0 && <p className="text-muted-foreground">{t('admin.contacts.empty')}</p>}
      </div>
    </div>
  );
};
export default AdminContacts;
