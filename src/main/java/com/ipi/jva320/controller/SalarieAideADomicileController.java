package com.ipi.jva320.controller;

import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.service.SalarieAideADomicileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class SalarieAideADomicileController {

    @Autowired
    private SalarieAideADomicileService salarieService;

    /** ✅ Injecté automatiquement dans le model pour chaque handler de ce controller */
    @ModelAttribute("nbSalaries")
    public long nbSalaries() {
        return salarieService.countSalaries();
    }

    /** ✅ Formulaire de création */
    @GetMapping("/salaries/aide/new")
    public String newAide(Model model) {
        SalarieAideADomicile salarie = new SalarieAideADomicile(
                "",
                LocalDate.now(),
                LocalDate.now(),
                0, 0, 0, 0, 0
        );
        model.addAttribute("salarie", salarie);
        return "detail_Salarie";
    }

    /** ✅ Détail d'un salarié */
    @GetMapping("/salaries/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        SalarieAideADomicile salarie = salarieService.getSalarie(id);

        if (salarie == null) {
            ra.addFlashAttribute("errorMessage", "Salarié introuvable (id=" + id + ")");
            return "redirect:/salaries";
        }

        model.addAttribute("salarie", salarie);
        return "detail_Salarie";
    }

    /** ✅ Création */
    @PostMapping("/salaries/save")
    public String saveAide(
            @Valid @ModelAttribute("salarie") SalarieAideADomicile salarie,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            return "detail_Salarie";
        }

        try {
            salarie.setId(null);

            if (salarie.getMoisDebutContrat() != null && salarie.getMoisEnCours() != null
                    && salarie.getMoisDebutContrat().isAfter(salarie.getMoisEnCours())) {
                br.rejectValue("moisDebutContrat", "error.salarie",
                        "La date de début de contrat ne peut pas être après le mois en cours");
                return "detail_Salarie";
            }

            SalarieAideADomicile created = salarieService.creerSalarieAideADomicile(salarie);

            ra.addFlashAttribute("successMessage",
                    "Le salarié " + created.getNom() + " a été créé avec succès !");

            return "redirect:/salaries/" + created.getId();

        } catch (SalarieException e) {
            br.reject("error.global", e.getMessage());
            return "detail_Salarie";
        } catch (Exception e) {
            br.reject("error.global", "Erreur technique : " + e.getMessage());
            return "detail_Salarie";
        }
    }

    /** ✅ Modification */
    @PostMapping("/salaries/{id}")
    public String updateAide(
            @PathVariable Long id,
            @Valid @ModelAttribute("salarie") SalarieAideADomicile salarie,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            return "detail_Salarie";
        }

        try {
            salarie.setId(id);

            if (salarie.getMoisDebutContrat() != null && salarie.getMoisEnCours() != null
                    && salarie.getMoisDebutContrat().isAfter(salarie.getMoisEnCours())) {
                br.rejectValue("moisDebutContrat", "error.salarie",
                        "La date de début de contrat ne peut pas être après le mois en cours");
                return "detail_Salarie";
            }

            SalarieAideADomicile updated = salarieService.updateSalarieAideADomicile(salarie);

            ra.addFlashAttribute("successMessage",
                    "Le salarié " + updated.getNom() + " a été modifié avec succès !");

            return "redirect:/salaries/" + updated.getId();

        } catch (SalarieException e) {
            br.reject("error.global", e.getMessage());
            return "detail_Salarie";
        } catch (Exception e) {
            br.reject("error.global", "Erreur technique : " + e.getMessage());
            return "detail_Salarie";
        }
    }

    /** ✅ Suppression */
    @GetMapping("/salaries/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            salarieService.deleteSalarieAideADomicile(id);
            ra.addFlashAttribute("successMessage", "Salarié supprimé avec succès !");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Suppression impossible : " + e.getMessage());
        }
        return "redirect:/salaries";
    }
}