-- Allow guest applications: user_id becomes optional
ALTER TABLE public.applications ALTER COLUMN user_id DROP NOT NULL;

-- Allow public storage upload to cv-uploads under a 'guest/' prefix
CREATE POLICY "Guests can upload CV to guest folder"
ON storage.objects FOR INSERT
TO anon, authenticated
WITH CHECK (
  bucket_id = 'cv-uploads'
  AND (storage.foldername(name))[1] = 'guest'
);

-- Allow authenticated users to upload to their own folder (if not already)
CREATE POLICY "Users can upload CV to own folder"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (
  bucket_id = 'cv-uploads'
  AND auth.uid()::text = (storage.foldername(name))[1]
);

-- Replace insert policy on applications to allow guest submissions
DROP POLICY IF EXISTS "Users insert own applications" ON public.applications;

CREATE POLICY "Anyone can submit application"
ON public.applications FOR INSERT
TO anon, authenticated
WITH CHECK (
  (auth.uid() IS NOT NULL AND auth.uid() = user_id)
  OR (user_id IS NULL)
);

-- Allow HR to read applications too (so they can see guest CVs)
DROP POLICY IF EXISTS "Users see own applications" ON public.applications;
CREATE POLICY "View applications"
ON public.applications FOR SELECT
USING (
  (auth.uid() IS NOT NULL AND auth.uid() = user_id)
  OR has_role(auth.uid(), 'admin'::app_role)
  OR has_role(auth.uid(), 'hr'::app_role)
);

-- Allow HR to update applications too
DROP POLICY IF EXISTS "Admins update applications" ON public.applications;
CREATE POLICY "Admins or HR update applications"
ON public.applications FOR UPDATE
USING (has_role(auth.uid(), 'admin'::app_role) OR has_role(auth.uid(), 'hr'::app_role));

-- Allow admins/HR to read CV files in cv-uploads
CREATE POLICY "Admins and HR can read CV uploads"
ON storage.objects FOR SELECT
TO authenticated
USING (
  bucket_id = 'cv-uploads'
  AND (has_role(auth.uid(), 'admin'::app_role) OR has_role(auth.uid(), 'hr'::app_role))
);