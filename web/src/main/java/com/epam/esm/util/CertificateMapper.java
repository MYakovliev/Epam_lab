package com.epam.esm.util;

import com.epam.esm.data.CertificateCreationData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CertificateMapper {
    @Autowired
    private TagService tagService;

    private void mapTag(List<String> tagNames, Certificate certificate){
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            List<Tag> listByName = tagService.findByName(tagName, 1, Integer.MAX_VALUE).getContent();
            Tag tag = listByName.stream()
                    .filter(tag1 -> tag1.getName().equalsIgnoreCase(tagName))
                    .findAny()
                    .orElse(new Tag(0, tagName));
            tags.add(tag);
        }
        certificate.setTags(tags);
    }

    public Certificate map(CertificateCreationData certificateCreationData){
        Certificate certificate = new Certificate();
        certificate.setName(certificateCreationData.getName());
        certificate.setDescription(certificateCreationData.getDescription());
        certificate.setPrice(certificateCreationData.getPrice());
        certificate.setDuration(certificateCreationData.getDuration());
        mapTag(certificateCreationData.getTags(), certificate);
        return certificate;
    }

    public void mapForUpdate(CertificateCreationData certificateCreationData, Certificate certificate){
        Certificate certificateFromData = map(certificateCreationData);
        String name = certificateFromData.getName();
        if (name != null && !name.isEmpty()){
            certificate.setName(name);
        }
        String description = certificateFromData.getDescription();
        if (description != null && !description.isEmpty()){
            certificate.setDescription(description);
        }
        BigDecimal price = certificateFromData.getPrice();
        if (price !=null){
            certificate.setPrice(price);
        }
        int duration = certificateFromData.getDuration();
        if (duration > 0){
            certificate.setDuration(duration);
        }
        List<String> tagNames = certificateCreationData.getTags();
        if (!tagNames.isEmpty()){
            mapTag(tagNames, certificate);
        }
    }
}
