import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { ArrowRight, PlayCircle, Smartphone, DollarSign, Palette, BarChart, Sparkles } from 'lucide-react';
import { Reveal, StaggerGroup, StaggerItem } from '@/components/motion/Reveal';
import { useSiteContent } from '@/hooks/useSiteContent';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import heroImg from '@/assets/hero-rocket.png';

const iconMap: Record<string, any> = {
  smartphone: Smartphone, 'dollar-sign': DollarSign, palette: Palette, 'bar-chart': BarChart,
};

const Home = () => {
  const { t } = useTranslation();
  const { lang } = useLang();
  const { t: c } = useSiteContent();
  const { data: servicesData } = useRealtimeTable<any>({
    table: 'services',
    filters: [{ column: 'is_published', value: true }],
    order: [{ column: 'display_order', ascending: true }],
  });
  const { data: projectsData } = useRealtimeTable<any>({
    table: 'portfolio_projects',
    filters: [{ column: 'is_published', value: true }],
    order: [{ column: 'display_order', ascending: true }],
    limit: 4,
  });
  const services = (servicesData as any[]) ?? [];
  const projects = (projectsData as any[]) ?? [];

  const pick = (row: any, key: string) => row?.[`${key}_${lang}`] ?? row?.[`${key}_en`] ?? '';

  const stats = [
    { value: c('stats.dau', '1M+'), label: t('home.dau') },
    { value: c('stats.team', '20+'), label: t('home.team') },
    { value: c('stats.apps', '50+'), label: t('home.apps') },
  ];

  return (
    <div>
      {/* HERO */}
      <section className="relative bg-hero overflow-hidden">
        <div className="absolute -top-40 -right-40 w-[600px] h-[600px] bg-primary/15 rounded-full blur-3xl pointer-events-none" />
        <div className="container-tight relative grid lg:grid-cols-2 gap-12 items-center pt-12 pb-24 lg:pt-20 lg:pb-32">
          <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.8 }}>
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-accent border border-primary/20 text-sm font-medium text-accent-foreground mb-6">
              <Sparkles className="h-3.5 w-3.5" />
              {c('hero.brand', 'Merryblue')} • {lang === 'vi' ? 'Công ty công nghệ' : 'Technology company'}
            </div>
            <h1 className="font-display text-5xl lg:text-7xl font-bold leading-[1.05] tracking-tight">
              <span className="text-foreground">{c('hero.brand', 'Merryblue')}</span>
              <br />
              <span className="text-gradient-warm">{c('hero.title_vi', t('footer.tagline'))}</span>
            </h1>
            <p className="mt-6 text-lg text-muted-foreground max-w-xl leading-relaxed">
              {c('hero.subtitle', '')}
            </p>
            <div className="mt-10 flex flex-wrap gap-4">
              <Button asChild size="lg" className="bg-gradient-primary hover:opacity-95 text-primary-foreground shadow-elegant text-base h-14 px-8 rounded-full">
                <Link to="/contact">{t('common.contactNow')}<ArrowRight className="ml-2 h-4 w-4" /></Link>
              </Button>
              <Button asChild size="lg" variant="outline" className="text-base h-14 px-8 rounded-full border-border hover:bg-accent">
                <Link to="/about"><PlayCircle className="mr-2 h-5 w-5 text-primary" />{t('common.learnMore')}</Link>
              </Button>
            </div>
          </motion.div>
          <motion.div
            initial={{ opacity: 0, scale: 0.92 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 1, delay: 0.2 }}
            className="relative"
          >
            <div className="absolute inset-0 bg-gradient-warm opacity-30 blur-3xl rounded-full" />
            <motion.img
              src={heroImg} alt="Technology platform launching"
              className="relative w-full max-w-xl mx-auto"
              animate={{ y: [0, -16, 0] }} transition={{ duration: 6, repeat: Infinity, ease: 'easeInOut' }}
              width={1280} height={1024}
            />
          </motion.div>
        </div>
      </section>

      {/* ABOUT */}
      <section className="py-24 container-tight">
        <Reveal>
          <div className="text-center max-w-2xl mx-auto mb-16">
            <h2 className="font-display text-4xl lg:text-5xl font-bold">{t('home.aboutUs')}</h2>
            <div className="h-1 w-20 bg-gradient-primary mx-auto mt-4 rounded-full" />
          </div>
        </Reveal>
        <StaggerGroup className="grid md:grid-cols-3 gap-6">
          {[
            { title: t('home.mission'), body: c('about.mission'), icon: '🎯' },
            { title: t('home.vision'), body: c('about.vision'), icon: '🚀' },
            { title: t('home.coreValues'), body: c('about.values'), icon: '💎' },
          ].map((item, i) => (
            <StaggerItem key={i}>
              <Link to="/about" className="grid-card block h-full">
                <div className="text-5xl mb-4">{item.icon}</div>
                <h3 className="font-display text-2xl font-bold mb-3">{item.title}</h3>
                <p className="text-muted-foreground leading-relaxed">{item.body}</p>
                <div className="mt-6 inline-flex items-center text-sm font-semibold text-primary group-hover:gap-3 transition-smooth">
                  {t('common.readMore')} <ArrowRight className="ml-2 h-4 w-4" />
                </div>
              </Link>
            </StaggerItem>
          ))}
        </StaggerGroup>
      </section>

      {/* STATS */}
      <section className="py-24 bg-gradient-warm relative overflow-hidden">
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,transparent_30%,hsl(var(--background))_120%)]" />
        <div className="container-tight relative">
          <Reveal>
            <h2 className="font-display text-4xl lg:text-5xl font-bold text-center text-primary-foreground mb-16">
              {t('home.statsTitle')}
            </h2>
          </Reveal>
          <StaggerGroup className="grid grid-cols-1 sm:grid-cols-3 gap-8 max-w-4xl mx-auto">
            {stats.map((s, i) => (
              <StaggerItem key={i}>
                <div className="text-center text-primary-foreground">
                  <div className="font-display text-5xl lg:text-7xl font-bold">{s.value}</div>
                  <div className="mt-3 text-sm lg:text-base opacity-90">{s.label}</div>
                </div>
              </StaggerItem>
            ))}
          </StaggerGroup>
        </div>
      </section>

      {/* SERVICES */}
      <section className="py-24 container-tight">
        <Reveal>
          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-16">
            <div>
              <h2 className="font-display text-4xl lg:text-5xl font-bold">{t('home.ourServices')}</h2>
              <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
            </div>
            <Button asChild variant="ghost" className="self-start lg:self-auto">
              <Link to="/services">{t('common.viewAll')} <ArrowRight className="ml-2 h-4 w-4" /></Link>
            </Button>
          </div>
        </Reveal>
        <StaggerGroup className="grid md:grid-cols-2 gap-6">
          {services.map((s) => {
            const Icon = iconMap[s.icon] ?? Sparkles;
            return (
              <StaggerItem key={s.id}>
                <Link to={`/services/${s.slug}`} className="grid-card group block h-full">
                  <div className="flex items-start gap-5">
                    <div className="grid place-items-center w-14 h-14 rounded-2xl bg-gradient-primary text-primary-foreground shrink-0">
                      <Icon className="h-7 w-7" />
                    </div>
                    <div className="flex-1">
                      <h3 className="font-display text-2xl font-bold mb-2">{pick(s, 'title')}</h3>
                      <p className="text-muted-foreground">{pick(s, 'short')}</p>
                      <div className="mt-4 inline-flex items-center text-sm font-semibold text-primary">
                        {t('common.readMore')} <ArrowRight className="ml-2 h-4 w-4" />
                      </div>
                    </div>
                  </div>
                </Link>
              </StaggerItem>
            );
          })}
        </StaggerGroup>
      </section>

      {/* PORTFOLIO */}
      <section className="py-24 bg-secondary/40">
        <div className="container-tight">
          <Reveal>
            <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-16">
              <div>
                <h2 className="font-display text-4xl lg:text-5xl font-bold">{t('home.ourPortfolio')}</h2>
                <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
              </div>
              <Button asChild variant="ghost"><Link to="/portfolio">{t('common.viewAll')}<ArrowRight className="ml-2 h-4 w-4" /></Link></Button>
            </div>
          </Reveal>
          <StaggerGroup className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {projects.map((p) => (
              <StaggerItem key={p.id}>
                <Link to={`/portfolio/${p.slug}`} className="grid-card block h-full">
                  <div className="aspect-square rounded-xl bg-gradient-warm mb-5 grid place-items-center text-primary-foreground font-display font-bold text-3xl">
                    {pick(p, 'title').charAt(0)}
                  </div>
                  <div className="text-xs uppercase tracking-wider text-primary font-semibold mb-2">{p.category}</div>
                  <h3 className="font-display text-xl font-bold mb-2">{pick(p, 'title')}</h3>
                  <p className="text-sm text-muted-foreground line-clamp-2">{pick(p, 'short')}</p>
                </Link>
              </StaggerItem>
            ))}
          </StaggerGroup>
        </div>
      </section>

      {/* CTA */}
      <section className="py-24 container-tight">
        <Reveal>
          <div className="relative overflow-hidden rounded-3xl bg-gradient-primary p-12 lg:p-16 text-center shadow-elegant">
            <div className="absolute inset-0 bg-[radial-gradient(circle_at_30%_20%,hsl(var(--primary-glow)/0.6),transparent_60%)]" />
            <div className="relative">
              <h2 className="font-display text-4xl lg:text-5xl font-bold text-primary-foreground mb-4">
                {t('home.joinUs')}
              </h2>
              <p className="text-primary-foreground/90 max-w-xl mx-auto mb-8">
                {lang === 'vi'
                  ? 'Cùng Merryblue xây dựng những sản phẩm chạm tới hàng triệu người dùng trên toàn cầu.'
                  : 'Join Merryblue and build products that touch millions of users worldwide.'}
              </p>
              <div className="flex flex-wrap gap-4 justify-center">
                <Button asChild size="lg" variant="secondary" className="rounded-full h-14 px-8 text-base">
                  <Link to="/recruitment">{t('careers.title')}<ArrowRight className="ml-2 h-4 w-4" /></Link>
                </Button>
                <Button asChild size="lg" variant="outline" className="rounded-full h-14 px-8 text-base bg-transparent text-primary-foreground border-primary-foreground/30 hover:bg-primary-foreground/10">
                  <Link to="/contact">{t('common.contactNow')}</Link>
                </Button>
              </div>
            </div>
          </div>
        </Reveal>
      </section>
    </div>
  );
};

export default Home;
