import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Reveal, StaggerGroup, StaggerItem } from '@/components/motion/Reveal';
import { ArrowRight, Sparkles, Smartphone, DollarSign, Palette, BarChart } from 'lucide-react';

const iconMap: Record<string, any> = { smartphone: Smartphone, 'dollar-sign': DollarSign, palette: Palette, 'bar-chart': BarChart };

const Services = () => {
  const { t } = useTranslation();
  const { lang } = useLang();
  const { data } = useRealtimeTable<any>({
    table: 'services',
    filters: [{ column: 'is_published', value: true }],
    order: [{ column: 'display_order', ascending: true }],
  });
  const services = (data as any[]) ?? [];
  const pick = (row: any, key: string) => row?.[`${key}_${lang}`] ?? row?.[`${key}_en`] ?? '';
  return (
    <div className="container-tight py-20">
      <Reveal>
        <h1 className="font-display text-5xl lg:text-6xl font-bold">{t('home.ourServices')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
      </Reveal>
      <StaggerGroup className="mt-16 grid md:grid-cols-2 gap-6">
        {services.map((s) => {
          const Icon = iconMap[s.icon] ?? Sparkles;
          return (
            <StaggerItem key={s.id}>
              <Link to={`/services/${s.slug}`} className="grid-card group block h-full">
                <div className="grid place-items-center w-14 h-14 rounded-2xl bg-gradient-primary text-primary-foreground mb-5">
                  <Icon className="h-7 w-7" />
                </div>
                <h3 className="font-display text-2xl font-bold mb-2">{pick(s, 'title')}</h3>
                <p className="text-muted-foreground">{pick(s, 'short')}</p>
                <div className="mt-4 inline-flex items-center text-sm font-semibold text-primary">{t('common.readMore')}<ArrowRight className="ml-2 h-4 w-4" /></div>
              </Link>
            </StaggerItem>
          );
        })}
      </StaggerGroup>
    </div>
  );
};
export default Services;
