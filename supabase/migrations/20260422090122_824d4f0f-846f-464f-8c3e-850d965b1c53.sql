
CREATE TABLE public.blog_posts (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  slug TEXT NOT NULL UNIQUE,
  title_vi TEXT NOT NULL,
  title_en TEXT NOT NULL,
  excerpt_vi TEXT,
  excerpt_en TEXT,
  body_vi TEXT,
  body_en TEXT,
  cover_url TEXT,
  category TEXT,
  tags TEXT[] DEFAULT '{}',
  author TEXT DEFAULT 'Merryblue',
  is_published BOOLEAN NOT NULL DEFAULT true,
  is_featured BOOLEAN NOT NULL DEFAULT false,
  views INTEGER NOT NULL DEFAULT 0,
  display_order INTEGER NOT NULL DEFAULT 0,
  published_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

ALTER TABLE public.blog_posts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view published posts"
  ON public.blog_posts FOR SELECT
  USING (is_published = true OR public.has_role(auth.uid(), 'admin'));

CREATE POLICY "Admins can insert posts"
  ON public.blog_posts FOR INSERT
  WITH CHECK (public.has_role(auth.uid(), 'admin'));

CREATE POLICY "Admins can update posts"
  ON public.blog_posts FOR UPDATE
  USING (public.has_role(auth.uid(), 'admin'));

CREATE POLICY "Admins can delete posts"
  ON public.blog_posts FOR DELETE
  USING (public.has_role(auth.uid(), 'admin'));

CREATE TRIGGER update_blog_posts_updated_at
  BEFORE UPDATE ON public.blog_posts
  FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

INSERT INTO public.blog_posts (slug, title_vi, title_en, excerpt_vi, excerpt_en, body_vi, body_en, category, tags, is_featured, display_order) VALUES
('toi-uu-admob-2026', 'Tối ưu doanh thu AdMob năm 2026', 'Optimizing AdMob revenue in 2026',
 'Chiến lược A/B testing và waterfall mediation giúp tăng eCPM 30-50% chỉ trong 3 tháng.',
 'A/B testing and waterfall mediation strategies that lift eCPM 30-50% in just 3 months.',
 E'## Bối cảnh\nThị trường quảng cáo di động 2026 cạnh tranh hơn bao giờ hết. Tại Merryblue, chúng tôi đã giúp hàng chục đối tác tăng trưởng doanh thu bền vững.\n\n## 3 chiến lược cốt lõi\n1. **Mediation đa nguồn**: AdMob + Meta Audience Network + Unity Ads\n2. **A/B testing liên tục**: thay đổi placement, refresh rate, định dạng\n3. **Phân tích cohort**: bám sát LTV theo từng quốc gia\n\n## Kết quả thực tế\nTrung bình eCPM tăng 42% sau 90 ngày triển khai.',
 E'## Context\nThe 2026 mobile ad market is more competitive than ever. At Merryblue we have helped dozens of partners grow revenue sustainably.\n\n## Three core strategies\n1. **Multi-source mediation**: AdMob + Meta Audience Network + Unity Ads\n2. **Continuous A/B testing**: placements, refresh rate, formats\n3. **Cohort analysis**: track LTV per country\n\n## Real results\nAverage eCPM lifted by 42% after 90 days.',
 'Monetization', ARRAY['AdMob','Mediation','Revenue'], true, 1),
('kotlin-multiplatform-mobile', 'Kotlin Multiplatform: tương lai của ứng dụng mobile', 'Kotlin Multiplatform: the future of mobile apps',
 'KMP cho phép chia sẻ business logic giữa Android và iOS mà vẫn giữ UI native.',
 'KMP lets you share business logic between Android and iOS while keeping UI native.',
 E'## Vì sao chọn KMP?\nKhác với cross-platform truyền thống (React Native, Flutter), KMP chỉ chia sẻ logic — UI vẫn 100% native, đảm bảo trải nghiệm mượt mà.\n\n## Use case của Merryblue\nỨng dụng Photo Editor Pro của chúng tôi đã chuyển sang KMP, giảm 40% codebase trùng lặp và rút ngắn cycle release xuống còn 2 tuần.',
 E'## Why KMP?\nUnlike traditional cross-platform stacks (React Native, Flutter), KMP only shares logic — UI stays 100% native, ensuring smooth UX.\n\n## Merryblue use case\nOur Photo Editor Pro app moved to KMP, cutting 40% of duplicated code and reducing release cycles to 2 weeks.',
 'Engineering', ARRAY['Kotlin','KMP','Mobile'], true, 2),
('thiet-ke-ux-cho-the-he-z', 'Thiết kế UX cho thế hệ Z', 'UX design for Gen Z',
 'Gen Z không kiên nhẫn — UX phải tức thì, trực quan và có yếu tố cảm xúc.',
 'Gen Z is impatient — UX must be instant, intuitive and emotional.',
 E'## 5 nguyên tắc thiết kế cho Gen Z\n1. Onboarding ≤ 3 màn hình\n2. Micro-interactions ở mọi tap\n3. Dark mode mặc định\n4. Personalization sau 30 giây\n5. Social proof ngay trên home\n\n## Case study\nỨng dụng Fitness Tracker của Merryblue đạt retention D7 = 38% sau khi áp dụng các nguyên tắc trên.',
 E'## 5 design principles for Gen Z\n1. Onboarding ≤ 3 screens\n2. Micro-interactions on every tap\n3. Dark mode by default\n4. Personalization within 30 seconds\n5. Social proof on home\n\n## Case study\nOur Fitness Tracker app reached D7 retention = 38% after applying these principles.',
 'Design', ARRAY['UX','GenZ','Mobile'], false, 3),
('vai-tro-ai-trong-mobile', 'Vai trò của AI trong ứng dụng mobile', 'The role of AI in mobile apps',
 'AI on-device đang định hình lại trải nghiệm người dùng — nhanh hơn, riêng tư hơn.',
 'On-device AI is reshaping UX — faster and more private.',
 E'## Xu hướng 2026\nGoogle Gemini Nano và Apple Intelligence cho phép chạy LLM trực tiếp trên thiết bị. Điều này mở ra cơ hội xây dựng tính năng thông minh mà không cần round-trip lên server.\n\n## Ứng dụng thực tế\nTại Merryblue, chúng tôi đã tích hợp AI on-device cho tính năng smart-crop trong app chỉnh ảnh, giảm 80% chi phí cloud.',
 E'## 2026 trends\nGoogle Gemini Nano and Apple Intelligence let LLMs run directly on device, unlocking smart features without server round-trips.\n\n## Real applications\nAt Merryblue we integrated on-device AI for smart-crop in our photo editor, cutting cloud costs by 80%.',
 'AI', ARRAY['AI','OnDevice','LLM'], true, 4);
