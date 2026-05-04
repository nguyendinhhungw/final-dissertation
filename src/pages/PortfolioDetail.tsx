import { Link, useParams } from 'react-router-dom';
import { useLang } from '@/contexts/LanguageContext';
import { useTranslation } from 'react-i18next';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { Reveal } from '@/components/motion/Reveal';

const PortfolioDetail = () => {
  const { slug } = useParams();
  const { lang } = useLang();
  const { t } = useTranslation();
  const { data, loading } = useRealtimeTable<any>({
    table: 'portfolio_projects',
    filters: [{ column: 'slug', value: slug! }],
    single: true,
    enabled: !!slug,
  });
  const p = data as any;

  if (loading) {
    return (
      <div className="container-tight py-20">
        <div className="h-8 w-24 bg-muted animate-pulse rounded mb-8" />
        <div className="h-12 w-3/4 bg-muted animate-pulse rounded mb-4" />
        <div className="h-6 w-1/2 bg-muted animate-pulse rounded mb-10" />
        <div className="aspect-video w-full bg-muted animate-pulse rounded-3xl" />
      </div>
    );
  }

  if (!p) {
    return (
      <div className="container-tight py-20 text-center">
        <h1 className="font-display text-4xl font-bold mb-4">404</h1>
        <p className="text-muted-foreground mb-8">{t('admin.common.empty')}</p>
        <Button asChild variant="outline">
          <Link to="/portfolio"><ArrowLeft className="mr-2 h-4 w-4" />{t('common.back')}</Link>
        </Button>
      </div>
    );
  }

  const pick = (k: string) => p?.[`${k}_${lang}`] ?? p?.[`${k}_en`] ?? '';
  return (
    <div className="container-tight py-20 max-w-4xl">
      <Button asChild variant="ghost" className="mb-8"><Link to="/portfolio"><ArrowLeft className="mr-2 h-4 w-4" />{t('common.back')}</Link></Button>
      <Reveal>
        <div className="text-xs uppercase tracking-wider text-primary font-semibold">{p.category}</div>
        <h1 className="font-display text-5xl lg:text-6xl font-bold mt-3">{pick('title')}</h1>
        <p className="mt-4 text-xl text-muted-foreground">{pick('short')}</p>
        {p.cover_url ? (
          <img src={p.cover_url} alt={pick('title')} className="mt-10 w-full rounded-3xl shadow-elegant" />
        ) : (
          <div className="mt-10 aspect-video w-full rounded-3xl bg-gradient-warm grid place-items-center text-primary-foreground font-display font-bold text-8xl shadow-elegant">
            {pick('title').charAt(0)}
          </div>
        )}
        <div className="mt-10 prose prose-lg max-w-none whitespace-pre-line text-foreground/90">{pick('body')}</div>
        {p.tech_stack?.length > 0 && (
          <div className="mt-8 flex flex-wrap gap-2">
            {p.tech_stack.map((tt: string) => <span key={tt} className="text-sm px-3 py-1.5 rounded-full bg-accent text-accent-foreground font-medium">{tt}</span>)}
          </div>
        )}
      </Reveal>
    </div>
  );
};
export default PortfolioDetail;
