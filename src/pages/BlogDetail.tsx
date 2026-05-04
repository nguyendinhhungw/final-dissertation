import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLang } from '@/contexts/LanguageContext';
import { supabase } from '@/integrations/supabase/client';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';
import { Reveal } from '@/components/motion/Reveal';
import { ArrowLeft, Calendar, Tag, User } from 'lucide-react';

const BlogDetail = () => {
  const { slug } = useParams();
  const { t } = useTranslation();
  const { lang } = useLang();
  const { data, loading } = useRealtimeTable<any>({
    table: 'blog_posts',
    filters: [{ column: 'slug', value: slug! }],
    single: true,
    enabled: !!slug,
  });
  const post = data as any;
  const [related, setRelated] = useState<any[]>([]);

  useEffect(() => {
    if (!post?.id) return;
    supabase
      .from('blog_posts')
      .select('id,slug,title_vi,title_en,excerpt_vi,excerpt_en,cover_url,category,published_at')
      .eq('is_published', true)
      .neq('id', post.id)
      .limit(3)
      .then(({ data: r }) => setRelated(r ?? []));
  }, [post?.id]);

  const pick = (row: any, k: string) => row?.[`${k}_${lang}`] ?? row?.[`${k}_en`] ?? '';
  const fmtDate = (d: string) =>
    new Date(d).toLocaleDateString(lang === 'vi' ? 'vi-VN' : 'en-US', { day: '2-digit', month: 'long', year: 'numeric' });

  if (loading) return <div className="container-tight py-20"><div className="h-96 bg-muted rounded-3xl animate-pulse" /></div>;
  if (!post) return <div className="container-tight py-20"><h1 className="font-display text-4xl">404</h1></div>;

  // Render markdown-ish body: split by lines, support ## headings, **bold**, lists
  const renderBody = (text: string) => {
    const lines = (text || '').split('\n');
    const out: JSX.Element[] = [];
    let listBuf: string[] = [];
    const flushList = () => {
      if (listBuf.length) {
        out.push(
          <ul key={`ul-${out.length}`} className="my-4 space-y-2 list-disc list-inside text-foreground/90">
            {listBuf.map((li, i) => <li key={i} dangerouslySetInnerHTML={{ __html: li.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>') }} />)}
          </ul>
        );
        listBuf = [];
      }
    };
    lines.forEach((raw, idx) => {
      const line = raw.trimEnd();
      if (/^\d+\.\s+/.test(line)) { listBuf.push(line.replace(/^\d+\.\s+/, '')); return; }
      if (/^[-*]\s+/.test(line)) { listBuf.push(line.replace(/^[-*]\s+/, '')); return; }
      flushList();
      if (line.startsWith('## ')) {
        out.push(<h2 key={idx} className="font-display text-3xl font-bold mt-10 mb-4">{line.slice(3)}</h2>);
      } else if (line.startsWith('# ')) {
        out.push(<h1 key={idx} className="font-display text-4xl font-bold mt-10 mb-4">{line.slice(2)}</h1>);
      } else if (line.trim() === '') {
        out.push(<div key={idx} className="h-2" />);
      } else {
        out.push(<p key={idx} className="leading-relaxed text-foreground/90 my-3" dangerouslySetInnerHTML={{ __html: line.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>') }} />);
      }
    });
    flushList();
    return out;
  };

  return (
    <article className="container-tight py-20 max-w-4xl">
      <Reveal>
        <Link to="/blog" className="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-primary transition-smooth mb-8">
          <ArrowLeft className="h-4 w-4" /> {t('blog.backToBlog')}
        </Link>
        <div className="flex flex-wrap gap-2 text-xs">
          {post.category && <span className="px-2.5 py-1 rounded-full bg-accent text-accent-foreground font-semibold uppercase tracking-wide">{post.category}</span>}
        </div>
        <h1 className="font-display text-4xl lg:text-6xl font-bold mt-4 leading-[1.1]">{pick(post, 'title')}</h1>
        <div className="mt-6 flex flex-wrap items-center gap-5 text-sm text-muted-foreground">
          <span className="inline-flex items-center gap-1.5"><User className="h-4 w-4 text-primary" />{post.author}</span>
          <span className="inline-flex items-center gap-1.5"><Calendar className="h-4 w-4 text-primary" />{fmtDate(post.published_at)}</span>
        </div>
        {post.cover_url && (
          <img src={post.cover_url} alt={pick(post, 'title')} className="mt-10 aspect-[16/8] w-full object-cover rounded-3xl" />
        )}
        {post.excerpt_vi && (
          <p className="mt-10 text-xl text-muted-foreground leading-relaxed border-l-4 border-primary pl-5">{pick(post, 'excerpt')}</p>
        )}
      </Reveal>

      <div className="mt-10 prose prose-lg max-w-none">{renderBody(pick(post, 'body'))}</div>

      {post.tags?.length > 0 && (
        <div className="mt-12 flex flex-wrap items-center gap-2">
          <Tag className="h-4 w-4 text-primary" />
          {post.tags.map((tag: string) => (
            <span key={tag} className="text-xs px-2.5 py-1 rounded-full bg-accent text-accent-foreground">#{tag}</span>
          ))}
        </div>
      )}

      {related.length > 0 && (
        <div className="mt-20 pt-10 border-t border-border">
          <h2 className="font-display text-2xl font-bold mb-6">{t('blog.related')}</h2>
          <div className="grid sm:grid-cols-3 gap-6">
            {related.map((r) => (
              <Link key={r.id} to={`/blog/${r.slug}`} className="grid-card block group">
                <h3 className="font-display text-lg font-bold group-hover:text-primary transition-smooth">{pick(r, 'title')}</h3>
                <p className="text-sm text-muted-foreground mt-2 line-clamp-2">{pick(r, 'excerpt')}</p>
              </Link>
            ))}
          </div>
        </div>
      )}
    </article>
  );
};

export default BlogDetail;
