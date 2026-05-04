DROP POLICY IF EXISTS "Anyone can submit application" ON public.applications;

CREATE POLICY "Guests submit applications"
ON public.applications FOR INSERT
TO anon
WITH CHECK (user_id IS NULL);

CREATE POLICY "Users submit own applications"
ON public.applications FOR INSERT
TO authenticated
WITH CHECK (user_id IS NULL OR user_id = auth.uid());