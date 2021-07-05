package com.epam.esm.controller;


import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/tags")
public class TagController {
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<Tag> findAll(){
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public Tag findById(@PathVariable("id") long id, Locale locale){
        return tagService.findById(id, locale);
    }

    @GetMapping("/search")
    public List<Tag> findByName(@RequestParam String search){
        return tagService.findByName(search);
    }

    @PostMapping
    public Tag create(@RequestBody Tag name, Locale locale){
        long id = tagService.create(name.getName(), locale);
        return tagService.findById(id, locale);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        tagService.delete(id);
    }
}
