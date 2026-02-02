package com.ipi.jva320.controller;

import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.repository.SalarieAideADomicileRepository;
import com.ipi.jva320.service.SalarieAideADomicileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SalarieController {

    @Autowired
    private SalarieAideADomicileService service;

    @Autowired
    private SalarieAideADomicileRepository repo;

    /** nb salariés pour la navbar */
    @ModelAttribute("nbSalaries")
    public long nbSalaries() {
        return service.countSalaries();
    }

    /** ✅ LISTE (recherche + pagination + tri) */
    @GetMapping("/salaries")
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortProperty,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String nom,
            Model model
    ) {
        Sort.Direction dir = "DESC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        if (sortProperty == null || sortProperty.isBlank()) {
            sortProperty = "nom";
        }

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(dir, sortProperty)
        );

        Page<SalarieAideADomicile> p;

        if (nom != null && !nom.isBlank()) {
            p = repo.findAllByNomContainingIgnoreCase(nom, pageable);
        } else {
            p = repo.findAll(pageable);
        }

        model.addAttribute("salaries", p.getContent());
        model.addAttribute("currentPage", p.getNumber());
        model.addAttribute("pageSize", p.getSize());
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalItems", p.getTotalElements());

        model.addAttribute("sortProperty", sortProperty);
        model.addAttribute("sortDirection", dir.name());
        model.addAttribute("nomRecherche", nom);

        return "list";
    }
}