import { useTranslation } from 'react-i18next';
import { useSiteContent } from '@/hooks/useSiteContent';
import { Reveal } from '@/components/motion/Reveal';
import TrustedPartners from '@/components/TrustedPartners';

const About = () => {
  const { t } = useTranslation();
  const { t: c } = useSiteContent();
  const items = [
    { title: t('home.mission'), body: c('about.mission'), emoji: '🎯' },
    { title: t('home.vision'), body: c('about.vision'), emoji: '🚀' },
    { title: t('home.coreValues'), body: c('about.values'), emoji: '💎' },
  ];
  return (
    <div className="container-tight py-20">
      <Reveal>
        <h1 className="font-display text-5xl lg:text-6xl font-bold">{t('home.aboutUs')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
        <p className="mt-6 text-lg text-muted-foreground max-w-2xl">{c('hero.subtitle')}</p>
      </Reveal>
      <div className="mt-16 space-y-8">
        {items.map((it, i) => (
          <Reveal key={i} delay={i * 0.1}>
            <div className="grid lg:grid-cols-12 gap-8 items-start p-8 rounded-3xl bg-card border border-border shadow-soft">
              <div className="lg:col-span-3 text-7xl">{it.emoji}</div>
              <div className="lg:col-span-9">
                <h2 className="font-display text-3xl font-bold mb-4">{it.title}</h2>
                <p className="text-lg text-muted-foreground leading-relaxed">{it.body}</p>
              </div>
            </div>
          </Reveal>
        ))}
      </div>
      <TrustedPartners />
    </div>
  );
};
export default About;
