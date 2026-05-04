
-- ============ ENUM ROLE ============
CREATE TYPE public.app_role AS ENUM ('admin', 'user');

-- ============ TIMESTAMP TRIGGER ============
CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SET search_path = public;

-- ============ PROFILES ============
CREATE TABLE public.profiles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL UNIQUE REFERENCES auth.users(id) ON DELETE CASCADE,
  display_name TEXT,
  avatar_url TEXT,
  phone TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Profiles are viewable by everyone" ON public.profiles FOR SELECT USING (true);
CREATE POLICY "Users update own profile" ON public.profiles FOR UPDATE USING (auth.uid() = user_id);
CREATE POLICY "Users insert own profile" ON public.profiles FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE TRIGGER trg_profiles_updated BEFORE UPDATE ON public.profiles FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============ USER ROLES ============
CREATE TABLE public.user_roles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  role app_role NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (user_id, role)
);
ALTER TABLE public.user_roles ENABLE ROW LEVEL SECURITY;

CREATE OR REPLACE FUNCTION public.has_role(_user_id UUID, _role app_role)
RETURNS BOOLEAN
LANGUAGE SQL STABLE SECURITY DEFINER SET search_path = public
AS $$
  SELECT EXISTS (SELECT 1 FROM public.user_roles WHERE user_id = _user_id AND role = _role);
$$;

CREATE POLICY "Users see own roles" ON public.user_roles FOR SELECT USING (auth.uid() = user_id OR public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins manage roles" ON public.user_roles FOR ALL USING (public.has_role(auth.uid(), 'admin'));

-- Auto-create profile + assign role on signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER LANGUAGE plpgsql SECURITY DEFINER SET search_path = public AS $$
BEGIN
  INSERT INTO public.profiles (user_id, display_name)
  VALUES (NEW.id, COALESCE(NEW.raw_user_meta_data->>'display_name', split_part(NEW.email, '@', 1)));
  -- Hardcoded admin
  IF NEW.email = 'admin@merryblue.llc' THEN
    INSERT INTO public.user_roles (user_id, role) VALUES (NEW.id, 'admin');
  ELSE
    INSERT INTO public.user_roles (user_id, role) VALUES (NEW.id, 'user');
  END IF;
  RETURN NEW;
END;
$$;
CREATE TRIGGER on_auth_user_created AFTER INSERT ON auth.users FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ============ SITE CONTENT (CMS key-value, đa ngôn ngữ) ============
CREATE TABLE public.site_content (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  key TEXT NOT NULL UNIQUE,
  value_vi TEXT,
  value_en TEXT,
  description TEXT,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.site_content ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Anyone reads site content" ON public.site_content FOR SELECT USING (true);
CREATE POLICY "Admins manage site content" ON public.site_content FOR ALL USING (public.has_role(auth.uid(), 'admin'));
CREATE TRIGGER trg_site_content_updated BEFORE UPDATE ON public.site_content FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============ SERVICES ============
CREATE TABLE public.services (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug TEXT NOT NULL UNIQUE,
  title_vi TEXT NOT NULL,
  title_en TEXT NOT NULL,
  short_vi TEXT,
  short_en TEXT,
  body_vi TEXT,
  body_en TEXT,
  icon TEXT,
  image_url TEXT,
  display_order INT NOT NULL DEFAULT 0,
  is_published BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.services ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Anyone reads published services" ON public.services FOR SELECT USING (is_published OR public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins manage services" ON public.services FOR ALL USING (public.has_role(auth.uid(), 'admin'));
CREATE TRIGGER trg_services_updated BEFORE UPDATE ON public.services FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============ PORTFOLIO ============
CREATE TABLE public.portfolio_projects (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug TEXT NOT NULL UNIQUE,
  title_vi TEXT NOT NULL,
  title_en TEXT NOT NULL,
  short_vi TEXT,
  short_en TEXT,
  body_vi TEXT,
  body_en TEXT,
  cover_url TEXT,
  gallery JSONB DEFAULT '[]'::jsonb,
  tech_stack TEXT[],
  category TEXT,
  display_order INT NOT NULL DEFAULT 0,
  is_published BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.portfolio_projects ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Anyone reads published portfolio" ON public.portfolio_projects FOR SELECT USING (is_published OR public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins manage portfolio" ON public.portfolio_projects FOR ALL USING (public.has_role(auth.uid(), 'admin'));
CREATE TRIGGER trg_portfolio_updated BEFORE UPDATE ON public.portfolio_projects FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============ JOBS ============
CREATE TABLE public.jobs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug TEXT NOT NULL UNIQUE,
  title_vi TEXT NOT NULL,
  title_en TEXT NOT NULL,
  department TEXT,
  location TEXT,
  employment_type TEXT,
  salary_range TEXT,
  short_vi TEXT,
  short_en TEXT,
  description_vi TEXT,
  description_en TEXT,
  requirements_vi TEXT,
  requirements_en TEXT,
  benefits_vi TEXT,
  benefits_en TEXT,
  is_open BOOLEAN NOT NULL DEFAULT TRUE,
  display_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.jobs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Anyone reads open jobs" ON public.jobs FOR SELECT USING (is_open OR public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins manage jobs" ON public.jobs FOR ALL USING (public.has_role(auth.uid(), 'admin'));
CREATE TRIGGER trg_jobs_updated BEFORE UPDATE ON public.jobs FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============ APPLICATIONS ============
CREATE TABLE public.applications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  job_id UUID NOT NULL REFERENCES public.jobs(id) ON DELETE CASCADE,
  full_name TEXT NOT NULL,
  email TEXT NOT NULL,
  phone TEXT,
  cv_path TEXT,
  cover_letter TEXT,
  status TEXT NOT NULL DEFAULT 'submitted',
  admin_notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.applications ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users see own applications" ON public.applications FOR SELECT USING (auth.uid() = user_id OR public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Users insert own applications" ON public.applications FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Admins update applications" ON public.applications FOR UPDATE USING (public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins delete applications" ON public.applications FOR DELETE USING (public.has_role(auth.uid(), 'admin'));
CREATE TRIGGER trg_applications_updated BEFORE UPDATE ON public.applications FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============ CONTACTS ============
CREATE TABLE public.contacts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  phone TEXT,
  subject TEXT,
  message TEXT NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.contacts ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Anyone can submit contact" ON public.contacts FOR INSERT WITH CHECK (true);
CREATE POLICY "Admins read contacts" ON public.contacts FOR SELECT USING (public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins update contacts" ON public.contacts FOR UPDATE USING (public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admins delete contacts" ON public.contacts FOR DELETE USING (public.has_role(auth.uid(), 'admin'));

-- ============ NOTIFICATIONS ============
CREATE TABLE public.notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  message TEXT NOT NULL,
  link TEXT,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users see own notifications" ON public.notifications FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users update own notifications" ON public.notifications FOR UPDATE USING (auth.uid() = user_id);
CREATE POLICY "Admins manage notifications" ON public.notifications FOR ALL USING (public.has_role(auth.uid(), 'admin'));

-- ============ STORAGE BUCKETS ============
INSERT INTO storage.buckets (id, name, public) VALUES ('cv-uploads', 'cv-uploads', false) ON CONFLICT DO NOTHING;
INSERT INTO storage.buckets (id, name, public) VALUES ('media', 'media', true) ON CONFLICT DO NOTHING;

-- CV uploads: user can upload to own folder, admin can read all
CREATE POLICY "Users upload own CV" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'cv-uploads' AND auth.uid()::text = (storage.foldername(name))[1]);
CREATE POLICY "Users read own CV" ON storage.objects FOR SELECT USING (bucket_id = 'cv-uploads' AND (auth.uid()::text = (storage.foldername(name))[1] OR public.has_role(auth.uid(), 'admin')));
CREATE POLICY "Admin delete CVs" ON storage.objects FOR DELETE USING (bucket_id = 'cv-uploads' AND public.has_role(auth.uid(), 'admin'));

-- Media: public read, admin write
CREATE POLICY "Public reads media" ON storage.objects FOR SELECT USING (bucket_id = 'media');
CREATE POLICY "Admin uploads media" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'media' AND public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admin updates media" ON storage.objects FOR UPDATE USING (bucket_id = 'media' AND public.has_role(auth.uid(), 'admin'));
CREATE POLICY "Admin deletes media" ON storage.objects FOR DELETE USING (bucket_id = 'media' AND public.has_role(auth.uid(), 'admin'));

-- ============ SEED DATA ============
INSERT INTO public.site_content (key, value_vi, value_en, description) VALUES
('hero.brand', 'Merryblue', 'Merryblue', 'Tên hiển thị trên hero'),
('hero.title_vi', 'Công nghệ kiến tạo cuộc sống tốt đẹp hơn', 'Embracing technology for a better life', 'Slogan hero'),
('hero.subtitle', 'là công ty công nghệ chuyên phát triển ứng dụng Android được kiếm tiền qua quảng cáo. Chúng tôi xây dựng các giải pháp di động thông minh giúp nhà phát hành và doanh nghiệp tối đa hóa doanh thu, tập trung vào hiệu năng, khả năng mở rộng và trải nghiệm người dùng.', 'is a technology-driven company specializing in the development of Android applications monetized through advertising. We build smart mobile solutions that help publishers and businesses maximize revenue from ads, with a strong focus on performance, scalability, and user experience.', 'Mô tả hero'),
('about.mission', 'Xây dựng các ứng dụng Android chất lượng cao, mang lại trải nghiệm tuyệt vời đồng thời tối đa hóa doanh thu quảng cáo qua chiến lược kiếm tiền thông minh và bền vững.', 'To build high-quality Android applications that deliver great user experiences while maximizing advertising revenue through smart and sustainable monetization strategies.', 'Sứ mệnh'),
('about.vision', 'Trở thành công ty hàng đầu về phát triển ứng dụng Android và kiếm tiền từ quảng cáo, giúp các ý tưởng và doanh nghiệp phát triển thành sản phẩm số có khả năng mở rộng và sinh lời.', 'To become a leading company in Android app development and ad monetization, empowering ideas and businesses to grow into scalable and profitable digital products.', 'Tầm nhìn'),
('about.values', 'Đổi mới, hiệu suất và minh bạch là kim chỉ nam. Chúng tôi tập trung vào thiết kế lấy người dùng làm trung tâm, tối ưu dựa trên dữ liệu và tạo giá trị dài hạn cho đối tác và người dùng.', 'Innovation, performance, and transparency guide everything we do. We focus on user-centric design, data-driven optimization, and long-term value creation for our partners and users.', 'Giá trị cốt lõi'),
('stats.dau', '1M+', '1M+', 'Người dùng hoạt động hàng ngày'),
('stats.team', '20+', '20+', 'Thành viên đội ngũ'),
('stats.apps', '50+', '50+', 'Ứng dụng đã phát triển'),
('stats.countries', '30+', '30+', 'Quốc gia'),
('contact.address', 'Hà Nội, Việt Nam', 'Hanoi, Vietnam', 'Địa chỉ'),
('contact.email', 'contact@merryblue.llc', 'contact@merryblue.llc', 'Email'),
('contact.phone', '+84 000 000 000', '+84 000 000 000', 'Số điện thoại'),
('footer.copyright', '© 2026 Merryblue. Bảo lưu mọi quyền.', '© 2026 Merryblue. All rights reserved.', 'Footer');

INSERT INTO public.services (slug, title_vi, title_en, short_vi, short_en, body_vi, body_en, icon, display_order) VALUES
('android-development', 'Phát triển Android', 'Android Development', 'Xây dựng ứng dụng Android hiệu suất cao, mượt mà.', 'Build high-performance, smooth Android apps.', 'Chúng tôi phát triển ứng dụng Android native với Kotlin/Java, tối ưu hiệu năng, UI/UX hiện đại và sẵn sàng mở rộng đến hàng triệu người dùng.', 'We build native Android apps with Kotlin/Java, optimized for performance, modern UI/UX and ready to scale to millions of users.', 'smartphone', 1),
('ad-monetization', 'Kiếm tiền quảng cáo', 'Ad Monetization', 'Tối ưu doanh thu quảng cáo trên ứng dụng.', 'Maximize ad revenue from your apps.', 'Tích hợp AdMob, Meta, Unity Ads và các mạng quảng cáo lớn. Mediation thông minh, A/B testing waterfall, tối ưu eCPM và fill rate.', 'Integrate AdMob, Meta, Unity Ads and major ad networks. Smart mediation, waterfall A/B testing, eCPM and fill rate optimization.', 'dollar-sign', 2),
('product-design', 'Thiết kế sản phẩm', 'Product Design', 'UX/UI hiện đại lấy người dùng làm trung tâm.', 'Modern UX/UI focused on the user.', 'Đội ngũ thiết kế sản phẩm cùng bạn từ ý tưởng đến launch: research, wireframe, prototype, design system và handoff cho dev.', 'Our design team partners from idea to launch: research, wireframe, prototype, design system and dev handoff.', 'palette', 3),
('data-optimization', 'Tối ưu dữ liệu', 'Data Optimization', 'Phân tích & tối ưu vận hành dựa trên dữ liệu.', 'Data-driven analytics and optimization.', 'Triển khai Firebase Analytics, BigQuery, dashboard realtime; thử nghiệm A/B liên tục để tăng retention, ARPU và LTV.', 'We deploy Firebase Analytics, BigQuery, real-time dashboards; continuous A/B testing to grow retention, ARPU and LTV.', 'bar-chart', 4);

INSERT INTO public.portfolio_projects (slug, title_vi, title_en, short_vi, short_en, body_vi, body_en, tech_stack, category, display_order) VALUES
('photo-cleaner', 'Photo Cleaner Pro', 'Photo Cleaner Pro', 'Ứng dụng dọn ảnh trùng lặp với AI.', 'AI-powered duplicate photo cleaner.', 'Sản phẩm flagship của Merryblue, đạt 1M+ DAU, top 10 Tools tại nhiều thị trường.', 'A Merryblue flagship reaching 1M+ DAU, top 10 Tools in multiple markets.', ARRAY['Kotlin','TensorFlow','AdMob'], 'Tools', 1),
('battery-master', 'Battery Master', 'Battery Master', 'Tối ưu pin và hiệu năng thiết bị.', 'Battery and performance optimizer.', 'Ứng dụng utility được hơn 500K người dùng tin tưởng mỗi ngày.', 'A utility trusted by 500K+ daily users.', ARRAY['Kotlin','WorkManager'], 'Utility', 2),
('quick-scan', 'Quick Scan', 'Quick Scan', 'Quét tài liệu thành PDF chỉ trong vài giây.', 'Scan documents to PDF in seconds.', 'Ứng dụng quét tài liệu sử dụng ML Kit của Google, xuất PDF chất lượng cao.', 'Document scanner powered by Google ML Kit, exporting high-quality PDFs.', ARRAY['Kotlin','MLKit','PDF'], 'Productivity', 3),
('weather-now', 'Weather Now', 'Weather Now', 'Dự báo thời tiết chính xác theo giờ.', 'Accurate hourly weather forecast.', 'Ứng dụng thời tiết với hơn 200K cài đặt, giao diện đẹp và dữ liệu chính xác.', 'Weather app with 200K+ installs, beautiful UI and accurate data.', ARRAY['Kotlin','OpenWeather'], 'Lifestyle', 4);

INSERT INTO public.jobs (slug, title_vi, title_en, department, location, employment_type, salary_range, short_vi, short_en, description_vi, description_en, requirements_vi, requirements_en, benefits_vi, benefits_en, display_order) VALUES
('android-developer', 'Lập trình viên Android', 'Android Developer', 'Engineering', 'Hà Nội', 'Full-time', '20-40 triệu', 'Phát triển ứng dụng Android hiệu suất cao cho hàng triệu người dùng.', 'Build high-performance Android apps for millions of users.', 'Tham gia phát triển và bảo trì các ứng dụng Android flagship của Merryblue, làm việc với đội ngũ product và designer để tạo ra trải nghiệm tốt nhất.', 'Join the team to build and maintain Merryblue flagship Android apps, working with product and design to deliver best-in-class experiences.', '2+ năm kinh nghiệm Kotlin/Java\nThành thạo Jetpack, Coroutines\nKinh nghiệm với AdMob là lợi thế', '2+ years Kotlin/Java\nProficient in Jetpack, Coroutines\nAdMob experience is a plus', 'Lương cạnh tranh\nThưởng theo doanh thu app\nMacBook + thiết bị test\nDu lịch hằng năm', 'Competitive salary\nApp revenue bonus\nMacBook + test devices\nAnnual company trip', 1),
('ui-ux-designer', 'Thiết kế UI/UX', 'UI/UX Designer', 'Design', 'Hà Nội', 'Full-time', '15-30 triệu', 'Thiết kế trải nghiệm cho ứng dụng triệu người dùng.', 'Design experiences for million-user apps.', 'Bạn sẽ làm việc trực tiếp với product manager và developer để tạo ra giao diện đẹp, dễ dùng và tối ưu chuyển đổi.', 'You will work directly with PM and developers to craft beautiful, easy-to-use, conversion-optimized interfaces.', 'Portfolio mạnh về mobile design\nThành thạo Figma\nHiểu Material Design', 'Strong mobile design portfolio\nProficient in Figma\nMaterial Design knowledge', 'Môi trường sáng tạo\nNgân sách học tập\nFlexible time', 'Creative environment\nLearning budget\nFlexible hours', 2),
('product-manager', 'Quản lý sản phẩm', 'Product Manager', 'Product', 'Hà Nội', 'Full-time', '25-50 triệu', 'Dẫn dắt sản phẩm từ ý tưởng đến hàng triệu người dùng.', 'Lead products from idea to millions of users.', 'Quản lý vòng đời sản phẩm, phân tích dữ liệu, lên roadmap và phối hợp với engineering, design, marketing.', 'Manage product lifecycle, analyze data, build roadmaps and collaborate with engineering, design and marketing.', '3+ năm kinh nghiệm PM mobile\nTư duy data-driven\nTiếng Anh tốt', '3+ years mobile PM\nData-driven mindset\nGood English', 'Cổ phần ESOP\nBHYT cao cấp\nBonus theo KPI', 'ESOP shares\nPremium health insurance\nKPI bonus', 3);
