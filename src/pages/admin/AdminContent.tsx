import { useEffect, useState } from 'react';
import { supabase } from '@/integrations/supabase/client';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';

const AdminContent = () => {
  const { t } = useTranslation();
  const [rows, setRows] = useState<any[]>([]);
  const load = () => supabase.from('site_content').select('*').order('key').then(({ data }) => setRows(data ?? []));
  useEffect(() => { load(); }, []);

  const save = async (row: any) => {
    const { error } = await supabase.from('site_content').update({ value_vi: row.value_vi, value_en: row.value_en }).eq('id', row.id);
    if (error) return toast.error(error.message);
    toast.success(t('admin.content.saved') + row.key);
  };

  return (
    <div className="p-8">
      <h1 className="font-display text-3xl font-bold mb-2">{t('admin.content.title')}</h1>
      <p className="text-muted-foreground mb-6">{t('admin.content.subtitle')}</p>
      <div className="space-y-4">
        {rows.map((r, i) => (
          <div key={r.id} className="p-5 rounded-xl bg-card border border-border">
            <div className="flex items-center justify-between mb-3">
              <div>
                <code className="text-xs px-2 py-1 rounded bg-muted">{r.key}</code>
                {r.description && <span className="ml-3 text-sm text-muted-foreground">{r.description}</span>}
              </div>
              <Button size="sm" onClick={() => save(rows[i])} className="bg-gradient-primary text-primary-foreground">{t('admin.common.save')}</Button>
            </div>
            <div className="grid md:grid-cols-2 gap-3">
              <div><Label>VI</Label><Textarea rows={2} value={r.value_vi ?? ''} onChange={(e) => { const next = [...rows]; next[i].value_vi = e.target.value; setRows(next); }} /></div>
              <div><Label>EN</Label><Textarea rows={2} value={r.value_en ?? ''} onChange={(e) => { const next = [...rows]; next[i].value_en = e.target.value; setRows(next); }} /></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
export default AdminContent;
