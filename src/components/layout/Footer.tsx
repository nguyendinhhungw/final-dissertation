import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useSiteContent } from '@/hooks/useSiteContent';
import logo from '@/assets/logo.png';
import { Mail, MapPin, Phone } from 'lucide-react';

const Footer = () => {
  const { t } = useTranslation();
  const { t: c } = useSiteContent();

  return (
    <footer className="border-t border-border bg-secondary/40 mt-24">
      <div className="container-tight py-16 grid gap-12 lg:grid-cols-4">
        <div>
          <Link to="/" className="flex items-center gap-2">
            <img src={logo} alt="Merryblue" className="h-12 w-auto object-contain shrink-0 p-1.5 bg-orange-500/5 border border-orange-500/20 rounded-xl shadow-sm" />
          </Link>
          <p className="mt-4 text-sm text-muted-foreground max-w-xs">{t('footer.tagline')}</p>
        </div>
        <div>
          <h4 className="font-display font-semibold mb-4">{t('footer.company')}</h4>
          <ul className="space-y-2 text-sm text-muted-foreground">
            <li><Link to="/about" className="hover:text-primary">{t('nav.about')}</Link></li>
            <li><Link to="/services" className="hover:text-primary">{t('nav.services')}</Link></li>
            <li><Link to="/portfolio" className="hover:text-primary">{t('nav.portfolio')}</Link></li>
            <li><Link to="/blog" className="hover:text-primary">{t('nav.blog')}</Link></li>
            <li><Link to="/recruitment" className="hover:text-primary">{t('nav.recruitment')}</Link></li>
          </ul>
        </div>
        <div>
          <h4 className="font-display font-semibold mb-4">{t('nav.contact')}</h4>
          <ul className="space-y-3 text-sm text-muted-foreground">
            <li className="flex gap-2"><MapPin className="h-4 w-4 mt-0.5 text-primary shrink-0" />{c('contact.address', 'Hanoi, Vietnam')}</li>
            <li className="flex gap-2"><Mail className="h-4 w-4 mt-0.5 text-primary shrink-0" />{c('contact.email', 'contact@merryblue.llc')}</li>
            <li className="flex gap-2"><Phone className="h-4 w-4 mt-0.5 text-primary shrink-0" />{c('contact.phone', '+84')}</li>
          </ul>
        </div>
        <div>
          <h4 className="font-display font-semibold mb-4">{t('footer.legal')}</h4>
          <ul className="space-y-2 text-sm text-muted-foreground">
            <li><Link to="/auth" className="hover:text-primary">{t('nav.login')}</Link></li>
            <li><Link to="/admin" className="hover:text-primary">{t('nav.admin')}</Link></li>
          </ul>
        </div>
      </div>
      <div className="border-t border-border">
        <div className="container-tight py-6 text-xs text-muted-foreground text-center">
          {c('footer.copyright', '© 2026 Merryblue.')}
        </div>
      </div>
    </footer>
  );
};

export default Footer;
