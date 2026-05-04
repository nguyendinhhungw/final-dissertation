INSERT INTO public.user_roles (user_id, role)
SELECT id, 'hr'::app_role FROM auth.users WHERE email = 'hr2@gmail.com'
ON CONFLICT (user_id, role) DO NOTHING;
DELETE FROM public.user_roles ur USING auth.users u
WHERE ur.user_id = u.id AND u.email = 'hr2@gmail.com' AND ur.role = 'user';