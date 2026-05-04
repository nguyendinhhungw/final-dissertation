import { useMemo } from 'react';
import { useLang } from '@/contexts/LanguageContext';
import { useRealtimeTable } from '@/hooks/useRealtimeTable';

export function useSiteContent() {
  const { lang } = useLang();
  const { data, loading } = useRealtimeTable<any>({ table: 'site_content', select: '*' });

  const map = useMemo(() => {
    const m: Record<string, string> = {};
    (Array.isArray(data) ? data : []).forEach((row: any) => {
      m[row.key] = (lang === 'vi' ? row.value_vi : row.value_en) ?? row.value_en ?? row.value_vi ?? '';
    });
    return m;
  }, [data, lang]);

  const t = (key: string, fallback = '') => map[key] ?? fallback;
  return { t, map, loading };
}
