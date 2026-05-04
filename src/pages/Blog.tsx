import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Reveal, StaggerGroup, StaggerItem } from '@/components/motion/Reveal';
import { ArrowRight, Calendar, Tag } from 'lucide-react';

const Blog = () => {
  const { t } = useTranslation();
  const { lang } = useLang();
  const { data, loading } = useRealtimeTable<any>({
    table: 'blog_posts',
    filters: [{ column: 'is_published', value: true }],
    order: [
      { column: 'is_featured', ascending: false },
      { column: 'published_at', ascending: false },
    ],
  });
  const posts = (data as any[]) ?? [];

  const pick = (row: any, k: string) => row?.[`${k}_${lang}`] ?? row?.[`${k}_en`] ?? '';
  const fmtDate = (d: string) =>
    new Date(d).toLocaleDateString(lang === 'vi' ? 'vi-VN' : 'en-US', { day: '2-digit', month: 'short', year: 'numeric' });

  const featured = posts.find((p) => p.is_featured) ?? posts[0];
  const rest = posts.filter((p) => p.id !== featured?.id);

  return (
    <div className="container-tight py-20">
      <Reveal>
        <div className="text-xs uppercase tracking-[0.2em] text-primary font-semibold">{t('blog.kicker')}</div>
        <h1 className="font-display text-5xl lg:text-6xl font-bold mt-3">{t('blog.title')}</h1>
        <div className="h-1 w-20 bg-gradient-primary mt-4 rounded-full" />
        <p className="mt-6 text-lg text-muted-foreground max-w-2xl">{t('blog.subtitle')}</p>
      </Reveal>

      {loading ? (
        <div className="mt-16 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-72 rounded-2xl bg-muted animate-pulse" />
          ))}
        </div>
      ) : posts.length === 0 ? (
        <p className="mt-16 text-muted-foreground">{t('blog.empty')}</p>
      ) : (
        <>
          {/* Featured */}
          {featured && (
            <Reveal delay={0.1}>
              <Link
                to={`/blog/${featured.slug}`}
                className="mt-14 grid lg:grid-cols-2 gap-8 p-6 lg:p-10 rounded-3xl bg-card border border-border hover:border-primary/40 hover-lift group"
              >
                <div className="relative overflow-hidden rounded-2xl aspect-[4/3] lg:aspect-auto">
                  {featured.cover_url ? (
                    <img src={featured.cover_url} alt={pick(featured, 'title')} className="w-full h-full object-cover" loading="lazy" />
                  ) : (
                    <div className="w-full h-full bg-gradient-warm grid place-items-center text-primary-foreground font-display font-bold text-7xl">
                      {pick(featured, 'title').charAt(0)}
                    </div>
                  )}
                </div>
                <div className="flex flex-col justify-center">
                  <div className="flex flex-wrap gap-2 text-xs">
                    <span className="px-2.5 py-1 rounded-full bg-primary text-primary-foreground font-semibold uppercase tracking-wide">
                      {t('blog.featured')}
                    </span>
                    {featured.category && (
                      <span className="px-2.5 py-1 rounded-full bg-accent text-accent-foreground font-semibold">{featured.category}</span>
                    )}
                  </div>
                  <h2 className="font-display text-3xl lg:text-4xl font-bold mt-4 group-hover:text-primary transition-smooth">
                    {pick(featured, 'title')}
                  </h2>
                  <p className="mt-4 text-muted-foreground line-clamp-3">{pick(featured, 'excerpt')}</p>
                  <div className="mt-6 flex flex-wrap items-center gap-5 text-sm text-muted-foreground">
                    <span className="inline-flex items-center gap-1.5"><Calendar className="h-4 w-4 text-primary" />{fmtDate(featured.published_at)}</span>
                    <span className="font-medium">{featured.author}</span>
                  </div>
                  <div className="mt-6 inline-flex items-center text-sm font-semibold text-primary">
                    {t('common.readMore')} <ArrowRight className="ml-2 h-4 w-4 group-hover:translate-x-1 transition-smooth" />
                  </div>
                </div>
              </Link>
            </Reveal>
          )}

          {/* Grid */}
          {rest.length > 0 && (
            <StaggerGroup className="mt-12 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {rest.map((p) => (
                <StaggerItem key={p.id}>
                  <Link to={`/blog/${p.slug}`} className="grid-card block h-full group">
                    {p.cover_url ? (
                      <img src={p.cover_url} alt={pick(p, 'title')} className="aspect-video w-full object-cover rounded-xl mb-5" loading="lazy" />
                    ) : (
                      <div className="aspect-video rounded-xl bg-gradient-warm mb-5 grid place-items-center text-primary-foreground font-display font-bold text-5xl">
                        {pick(p, 'title').charAt(0)}
                      </div>
                    )}
                    {p.category && <div className="text-xs uppercase tracking-wider text-primary font-semibold">{p.category}</div>}
                    <h3 className="font-display text-xl font-bold mt-2 group-hover:text-primary transition-smooth">{pick(p, 'title')}</h3>
                    <p className="text-sm text-muted-foreground mt-2 line-clamp-2">{pick(p, 'excerpt')}</p>
                    <div className="mt-4 flex items-center gap-3 text-xs text-muted-foreground">
                      <Calendar className="h-3.5 w-3.5" />{fmtDate(p.published_at)}
                      <span>•</span>
                      <span>{p.author}</span>
                    </div>
                  </Link>
                </StaggerItem>
              ))}
            </StaggerGroup>
          )}
        </>
      )}
    </div>
  );
};

export default Blog;
