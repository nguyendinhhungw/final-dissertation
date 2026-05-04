import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Reveal, StaggerGroup, StaggerItem } from '@/components/motion/Reveal';
import { ArrowRight, Briefcase, MapPin, Wallet } from 'lucide-react';

const Recruitment = () => {
  const { t } = useTranslation();
  const { lang } = useLang();
  const { data } = useRealtimeTable<any>({
    table: 'jobs',
    filters: [{ column: 'is_open', value: true }],
    order: [{ column: 'display_order', ascending: true }],
  });
  const jobs = (data as any[]) ?? [];
  const pick = (row: any, k: string) => row?.[`${k}_${lang}`] ?? row?.[`${k}_en`] ?? '';
  return (
    <div className="container-tight py-20">
      <Reveal>
        <h1 className="font-display text-5xl lg:text-6xl font-bold">{t('careers.title')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
        <p className="mt-6 text-lg text-muted-foreground max-w-2xl">{t('careers.subtitle')}</p>
      </Reveal>
      <StaggerGroup className="mt-16 grid gap-5">
        {jobs.map((j) => (
          <StaggerItem key={j.id}>
            <Link to={`/recruitment/${j.slug}`} className="group block p-8 rounded-2xl bg-card border border-border hover:border-primary/40 hover-lift">
              <div className="flex flex-wrap items-start justify-between gap-6">
                <div className="flex-1 min-w-0">
                  <div className="flex flex-wrap gap-2 mb-3 text-xs">
                    {j.department && <span className="px-2.5 py-1 rounded-full bg-accent text-accent-foreground font-semibold uppercase tracking-wide">{j.department}</span>}
                    {j.employment_type && <span className="px-2.5 py-1 rounded-full bg-secondary text-secondary-foreground font-medium">{j.employment_type}</span>}
                  </div>
                  <h3 className="font-display text-2xl lg:text-3xl font-bold mb-3 group-hover:text-primary transition-smooth">{pick(j, 'title')}</h3>
                  <p className="text-muted-foreground">{pick(j, 'short')}</p>
                  <div className="mt-4 flex flex-wrap gap-5 text-sm text-muted-foreground">
                    {j.location && <span className="inline-flex items-center gap-1.5"><MapPin className="h-4 w-4 text-primary" />{j.location}</span>}
                    {j.salary_range && <span className="inline-flex items-center gap-1.5"><Wallet className="h-4 w-4 text-primary" />{j.salary_range}</span>}
                    <span className="inline-flex items-center gap-1.5"><Briefcase className="h-4 w-4 text-primary" />{j.employment_type}</span>
                  </div>
                </div>
                <div className="inline-flex items-center gap-2 text-primary font-semibold self-center">
                  {t('careers.viewJob')} <ArrowRight className="h-4 w-4 group-hover:translate-x-1 transition-smooth" />
                </div>
              </div>
            </Link>
          </StaggerItem>
        ))}
        {jobs.length === 0 && <p className="text-muted-foreground">No open positions right now.</p>}
      </StaggerGroup>
    </div>
  );
};
export default Recruitment;
