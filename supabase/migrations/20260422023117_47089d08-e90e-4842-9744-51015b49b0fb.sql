
-- Restrict media bucket: chỉ SELECT object cụ thể bằng URL công khai, không cho phép listing bucket
DROP POLICY IF EXISTS "Public reads media" ON storage.objects;
CREATE POLICY "Public reads media files"
ON storage.objects FOR SELECT
USING (bucket_id = 'media' AND name IS NOT NULL);

-- Tighten contact insert: validate non-empty + length to limit spam
DROP POLICY IF EXISTS "Anyone can submit contact" ON public.contacts;
CREATE POLICY "Public submit valid contact"
ON public.contacts FOR INSERT
WITH CHECK (
  length(trim(name)) BETWEEN 1 AND 100
  AND length(trim(email)) BETWEEN 5 AND 200
  AND email LIKE '%@%.%'
  AND length(trim(message)) BETWEEN 1 AND 5000
);
