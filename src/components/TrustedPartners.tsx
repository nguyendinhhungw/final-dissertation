import { Reveal, StaggerGroup, StaggerItem } from '@/components/motion/Reveal';
import { useLang } from '@/contexts/LanguageContext';


type Partner = {
  name: string;
  /** Đường dẫn ảnh logo. Để trống ('') sẽ hiển thị placeholder. */
  logo: string;
  /** Link tới trang chính thức của đối tác */
  url: string;
};

// 👉 Sau này chỉ cần cập nhật `logo` (ví dụ: import logo from '@/assets/admob.png')
const partners: Partner[] = [
  { name: 'Google AdMob', logo: 'https://icon.horse/icon/admob.google.com', url: 'https://admob.google.com/' },
  { name: 'AppLovin', logo: 'https://icon.horse/icon/applovin.com', url: 'https://www.applovin.com/' },
  { name: 'Google Ads', logo: 'https://icon.horse/icon/ads.google.com', url: 'https://ads.google.com/' },
  { name: 'Unity Ads', logo: 'https://icon.horse/icon/unity.com', url: 'https://unity.com/products/unity-ads' },
  { name: 'Meta Audience', logo: 'https://icon.horse/icon/meta.com', url: 'https://www.facebook.com/business/marketing/audience-network' },
  { name: 'IronSource', logo: 'https://icon.horse/icon/is.com', url: 'https://www.is.com/' },
];

const TrustedPartners = () => {
  const { lang } = useLang();

  return (
    <section className="mt-20">
      <Reveal>
        <div className="text-center max-w-2xl mx-auto mb-12">
          <div className="inline-block px-4 py-1.5 rounded-full bg-accent text-primary text-xs font-semibold tracking-widest uppercase mb-5">
            {lang === 'vi' ? 'Đối tác' : 'Partners'}
          </div>
          <h2 className="font-display text-4xl lg:text-5xl font-bold">
            {lang === 'vi' ? 'Đối tác tin cậy' : 'Trusted Partners'}
          </h2>
          <p className="mt-4 text-muted-foreground">
            {lang === 'vi'
              ? 'Chúng tôi tự hào là đối tác của các thương hiệu công nghệ hàng đầu toàn cầu.'
              : 'We are proud to partner with leading global technology brands.'}
          </p>
        </div>
      </Reveal>
      <StaggerGroup className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
        {partners.map((p) => (
          <StaggerItem key={p.name}>
            <a
              href={p.url}
              target="_blank"
              rel="noopener noreferrer"
              aria-label={`${p.name} (mở trong tab mới)`}
              className="group block h-28 rounded-2xl bg-card border border-border shadow-soft grid place-items-center p-4 transition-smooth hover:border-primary/40 hover:shadow-elegant hover:-translate-y-0.5"
            >
              {p.logo ? (
                <img
                  src={p.logo}
                  alt={`${p.name} logo`}
                  loading="lazy"
                  className="max-h-14 max-w-full object-contain transition-smooth"
                />
              ) : (
                <div className="flex flex-col items-center gap-1.5 text-muted-foreground group-hover:text-primary transition-smooth">
                  <div className="w-10 h-10 rounded-lg border-2 border-dashed border-muted-foreground/30 grid place-items-center text-xs group-hover:border-primary/40">
                    IMG
                  </div>
                  <span className="font-display font-medium text-xs text-center">
                    {p.name}
                  </span>
                </div>
              )}
            </a>
          </StaggerItem>
        ))}
      </StaggerGroup>
    </section>
  );
};

export default TrustedPartners;
