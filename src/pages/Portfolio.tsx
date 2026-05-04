import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Reveal, StaggerGroup, StaggerItem } from '@/components/motion/Reveal';

const Portfolio = () => {
  const { t } = useTranslation();
  const { lang } = useLang();
  const { data } = useRealtimeTable<any>({
    table: 'portfolio_projects',
    filters: [{ column: 'is_published', value: true }],
    order: [{ column: 'display_order', ascending: true }],
  });
  const items = (data as any[]) ?? [];
  const pick = (row: any, k: string) => row?.[`${k}_${lang}`] ?? row?.[`${k}_en`] ?? '';
  return (
    <div className="container-tight py-20">
      <Reveal>
        <h1 className="font-display text-5xl lg:text-6xl font-bold">{t('home.ourPortfolio')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
      </Reveal>
      <StaggerGroup className="mt-16 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {items.map((p) => (
          <StaggerItem key={p.id}>
            <Link to={`/portfolio/${p.slug}`} className="grid-card block h-full">
              {p.cover_url ? (
                <img src={p.cover_url} alt={pick(p, 'title')} className="aspect-video w-full object-cover rounded-xl mb-5" loading="lazy" />
              ) : (
                <div className="aspect-video rounded-xl bg-gradient-warm mb-5 grid place-items-center text-primary-foreground font-display font-bold text-5xl">
                  {pick(p, 'title').charAt(0)}
                </div>
              )}
              <div className="text-xs uppercase tracking-wider text-primary font-semibold">{p.category}</div>
              <h3 className="font-display text-2xl font-bold mt-2">{pick(p, 'title')}</h3>
              <p className="text-muted-foreground mt-2 line-clamp-2">{pick(p, 'short')}</p>
              {p.tech_stack?.length > 0 && (
                <div className="mt-4 flex flex-wrap gap-2">
                  {p.tech_stack.map((t: string) => (
                    <span key={t} className="text-xs px-2 py-1 rounded-full bg-accent text-accent-foreground">{t}</span>
                  ))}
                </div>
              )}
            </Link>
          </StaggerItem>
        ))}
      </StaggerGroup>
    </div>
  );
};
export default Portfolio;
