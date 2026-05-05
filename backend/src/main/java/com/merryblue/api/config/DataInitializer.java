package com.merryblue.api.config;

import com.merryblue.api.model.SiteContent;
import com.merryblue.api.repository.SiteContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SiteContentRepository siteContentRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing application data...");
        
        if (siteContentRepository.count() == 0) {
            log.info("Seeding SiteContent table...");
            saveContent("site.title", "Merryblue - Digital Agency", "Merryblue - Giải pháp công nghệ");
            saveContent("site.description", "Expert digital solutions", "Giải pháp kỹ thuật số chuyên nghiệp");
        }
    }

    private void saveContent(String key, String en, String vi) {
        SiteContent content = new SiteContent();
        content.setKey(key);
        content.setValueEn(en);
        content.setValueVi(vi);
        siteContentRepository.save(content);
    }
}
