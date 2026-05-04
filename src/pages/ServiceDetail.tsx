import { Link, useParams } from 'react-router-dom';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { useTranslation } from 'react-i18next';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { Reveal } from '@/components/motion/Reveal';

const ServiceDetail = () => {
  const { slug } = useParams();
  const { lang } = useLang();
  const { t } = useTranslation();
  const { data, loading } = useRealtimeTable<any>({
    table: 'services',
    filters: [{ column: 'slug', value: slug! }],
    single: true,
    enabled: !!slug,
  });
  const s = data as any;

  if (loading) {
    return (
      <div className="container-tight py-20">
        <div className="h-8 w-24 bg-muted animate-pulse rounded mb-8" />
        <div className="h-12 w-3/4 bg-muted animate-pulse rounded mb-4" />
        <div className="h-40 w-full bg-muted animate-pulse rounded-xl" />
      </div>
    );
  }

  if (!s) {
    return (
      <div className="container-tight py-20 text-center">
        <h1 className="font-display text-4xl font-bold mb-4">404</h1>
        <p className="text-muted-foreground mb-8">{t('admin.common.empty')}</p>
        <Button asChild variant="outline">
          <Link to="/services"><ArrowLeft className="mr-2 h-4 w-4" />{t('common.back')}</Link>
        </Button>
      </div>
    );
  }

  const pick = (k: string) => s?.[`${k}_${lang}`] ?? s?.[`${k}_en`] ?? '';
  return (
    <div className="container-tight py-20 max-w-4xl">
      <Button asChild variant="ghost" className="mb-8"><Link to="/services"><ArrowLeft className="mr-2 h-4 w-4" />{t('common.back')}</Link></Button>
      <Reveal>
        <div className="text-xs uppercase tracking-wider text-primary font-semibold">{t('home.ourServices')}</div>
        <h1 className="font-display text-5xl lg:text-6xl font-bold mt-3">{pick('title')}</h1>
        <p className="mt-4 text-xl text-muted-foreground">{pick('short')}</p>
        <div className="mt-12 prose prose-lg max-w-none text-foreground/90 leading-relaxed whitespace-pre-line">
          {pick('body')}
        </div>
        <div className="mt-12">
          <Button asChild size="lg" className="bg-gradient-primary text-primary-foreground rounded-full h-14 px-8">
            <Link to="/contact">{t('common.contactNow')}</Link>
          </Button>
        </div>
      </Reveal>
    </div>
  );
};
export default ServiceDetail;
