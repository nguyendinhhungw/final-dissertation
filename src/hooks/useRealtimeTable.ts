import { useEffect, useState, useCallback, useRef } from 'react';
import { supabase } from '@/integrations/supabase/client';

type Filter = { column: string; value: any };

interface Options {
  table: string;
  select?: string;
  filters?: Filter[];
  order?: { column: string; ascending?: boolean }[];
  limit?: number;
  /** If provided, returns only the matching row (.maybeSingle). */
  single?: boolean;
  /** Skip fetching/subscribing while false. */
  enabled?: boolean;
}

/**
 * Fetches rows from a Supabase table and keeps them in sync via Postgres realtime.
 * Any INSERT/UPDATE/DELETE on the table triggers a refetch so the UI updates live.
 */
export function useRealtimeTable<T = any>({
  table,
  select = '*',
  filters = [],
  order = [],
  limit,
  single = false,
  enabled = true,
}: Options) {
  const [data, setData] = useState<T | T[] | null>(single ? null : ([] as any));
  const [loading, setLoading] = useState(true);

  // Stabilize deps via JSON (filters/order are usually inline objects).
  const filtersKey = JSON.stringify(filters);
  const orderKey = JSON.stringify(order);

  const fetchData = useCallback(async () => {
    let q: any = (supabase as any).from(table).select(select);
    filters.forEach((f) => { q = q.eq(f.column, f.value); });
    order.forEach((o) => { q = q.order(o.column, { ascending: o.ascending ?? true }); });
    if (limit) q = q.limit(limit);
    if (single) {
      const { data: row } = await q.maybeSingle();
      setData(row ?? null);
    } else {
      const { data: rows } = await q;
      setData((rows ?? []) as any);
    }
    setLoading(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [table, select, filtersKey, orderKey, limit, single]);

  const channelRef = useRef<any>(null);

  useEffect(() => {
    if (!enabled) return;
    setLoading(true);
    fetchData();

    // Use a unique channel per mount to avoid collisions.
    const channel = supabase
      .channel(`realtime:${table}:${Math.random().toString(36).slice(2, 9)}`)
      .on('postgres_changes', { event: '*', schema: 'public', table }, () => {
        fetchData();
      })
      .subscribe();

    channelRef.current = channel;
    return () => {
      supabase.removeChannel(channel);
      channelRef.current = null;
    };
  }, [enabled, fetchData, table]);

  return { data, loading, refetch: fetchData };
}
