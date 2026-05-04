import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { useTranslation } from 'react-i18next';

type Lang = 'vi' | 'en';
interface LangContextValue { lang: Lang; setLang: (l: Lang) => void; toggle: () => void; }

const LangContext = createContext<LangContextValue>({ lang: 'vi', setLang: () => {}, toggle: () => {} });

export const LanguageProvider = ({ children }: { children: ReactNode }) => {
  const { i18n } = useTranslation();
  const [lang, setLangState] = useState<Lang>((localStorage.getItem('mb-lang') as Lang) || 'vi');

  useEffect(() => { i18n.changeLanguage(lang); localStorage.setItem('mb-lang', lang); }, [lang, i18n]);

  const setLang = (l: Lang) => setLangState(l);
  const toggle = () => setLangState((p) => (p === 'vi' ? 'en' : 'vi'));

  return <LangContext.Provider value={{ lang, setLang, toggle }}>{children}</LangContext.Provider>;
};

export const useLang = () => useContext(LangContext);
