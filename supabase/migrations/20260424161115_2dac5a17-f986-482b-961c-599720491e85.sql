DROP POLICY IF EXISTS "Admins read contacts" ON public.contacts;
DROP POLICY IF EXISTS "Admins update contacts" ON public.contacts;
DROP POLICY IF EXISTS "Admins delete contacts" ON public.contacts;

CREATE POLICY "Admins or HR read contacts"
ON public.contacts FOR SELECT
USING (has_role(auth.uid(), 'admin'::app_role) OR has_role(auth.uid(), 'hr'::app_role));

CREATE POLICY "Admins or HR update contacts"
ON public.contacts FOR UPDATE
USING (has_role(auth.uid(), 'admin'::app_role) OR has_role(auth.uid(), 'hr'::app_role));

CREATE POLICY "Admins or HR delete contacts"
ON public.contacts FOR DELETE
USING (has_role(auth.uid(), 'admin'::app_role) OR has_role(auth.uid(), 'hr'::app_role));