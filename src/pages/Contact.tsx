import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { supabase } from '@/integrations/supabase/client';
import { useSiteContent } from '@/hooks/useSiteContent';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Reveal } from '@/components/motion/Reveal';
import { Mail, MapPin, Phone } from 'lucide-react';
import { toast } from 'sonner';
import { z } from 'zod';

const schema = z.object({
  name: z.string().trim().min(1).max(100),
  email: z.string().trim().email().max(200),
  phone: z.string().trim().max(30).optional().or(z.literal('')),
  subject: z.string().trim().max(200).optional().or(z.literal('')),
  message: z.string().trim().min(1).max(5000),
});

const Contact = () => {
  const { t } = useTranslation();
  const { t: c } = useSiteContent();
  const [form, setForm] = useState({ name: '', email: '', phone: '', subject: '', message: '' });
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    const p = schema.safeParse(form);
    if (!p.success) { toast.error(p.error.issues[0].message); return; }
    setBusy(true);
    const { error } = await supabase.from('contacts').insert({
      name: form.name, email: form.email,
      phone: form.phone || null, subject: form.subject || null, message: form.message,
    });
    setBusy(false);
    if (error) { toast.error(error.message); return; }
    toast.success(t('contact.success'));
    setForm({ name: '', email: '', phone: '', subject: '', message: '' });
  };

  return (
    <div className="container-tight py-20 grid lg:grid-cols-2 gap-16">
      <Reveal>
        <h1 className="font-display text-5xl lg:text-6xl font-bold">{t('contact.title')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
        <p className="mt-6 text-lg text-muted-foreground">{t('contact.subtitle')}</p>
        <div className="mt-10 space-y-5">
          <Item icon={MapPin} label={c('contact.address')} />
          <Item icon={Mail} label={c('contact.email')} />
          <Item icon={Phone} label={c('contact.phone')} />
        </div>
      </Reveal>
      <Reveal delay={0.15}>
        <form onSubmit={submit} className="p-8 rounded-3xl border border-border bg-card shadow-soft space-y-4">
          <div className="grid sm:grid-cols-2 gap-4">
            <div><Label>{t('contact.name')}</Label><Input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required maxLength={100} /></div>
            <div><Label>{t('careers.email')}</Label><Input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required maxLength={200} /></div>
          </div>
          <div className="grid sm:grid-cols-2 gap-4">
            <div><Label>{t('careers.phone')}</Label><Input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} maxLength={30} /></div>
            <div><Label>{t('contact.subject')}</Label><Input value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} maxLength={200} /></div>
          </div>
          <div><Label>{t('contact.message')}</Label><Textarea rows={6} value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} required maxLength={5000} /></div>
          <Button disabled={busy} type="submit" className="w-full bg-gradient-primary text-primary-foreground rounded-full h-12">{busy ? '...' : t('common.send')}</Button>
        </form>
      </Reveal>
    </div>
  );
};

const Item = ({ icon: Icon, label }: { icon: any; label: string }) => (
  <div className="flex items-center gap-4">
    <div className="grid place-items-center w-12 h-12 rounded-xl bg-accent text-accent-foreground"><Icon className="h-5 w-5" /></div>
    <span className="text-foreground">{label}</span>
  </div>
);

export default Contact;
