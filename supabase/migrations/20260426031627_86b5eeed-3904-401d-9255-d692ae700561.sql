
-- Function: notify all admins + hr when a new contact arrives
CREATE OR REPLACE FUNCTION public.notify_admins_on_new_contact()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  INSERT INTO public.notifications (user_id, title, message, link)
  SELECT ur.user_id,
         'Tin nhắn liên hệ mới',
         COALESCE(NEW.name, 'Khách') || ' • ' || COALESCE(NEW.subject, 'Liên hệ'),
         '/admin/contacts'
  FROM public.user_roles ur
  WHERE ur.role IN ('admin','hr');
  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_notify_admins_on_new_contact ON public.contacts;
CREATE TRIGGER trg_notify_admins_on_new_contact
AFTER INSERT ON public.contacts
FOR EACH ROW EXECUTE FUNCTION public.notify_admins_on_new_contact();

-- Function: notify all admins + hr when a new application arrives
CREATE OR REPLACE FUNCTION public.notify_admins_on_new_application()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  job_title TEXT;
BEGIN
  SELECT title_vi INTO job_title FROM public.jobs WHERE id = NEW.job_id;
  INSERT INTO public.notifications (user_id, title, message, link)
  SELECT ur.user_id,
         'Hồ sơ ứng tuyển mới',
         COALESCE(NEW.full_name,'Ứng viên') || ' • ' || COALESCE(job_title,'Vị trí'),
         '/admin/applications'
  FROM public.user_roles ur
  WHERE ur.role IN ('admin','hr');
  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_notify_admins_on_new_application ON public.applications;
CREATE TRIGGER trg_notify_admins_on_new_application
AFTER INSERT ON public.applications
FOR EACH ROW EXECUTE FUNCTION public.notify_admins_on_new_application();
