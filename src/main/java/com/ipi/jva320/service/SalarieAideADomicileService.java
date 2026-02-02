package com.ipi.jva320.service;

import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.Entreprise;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.repository.SalarieAideADomicileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityExistsException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;

@Service
public class SalarieAideADomicileService {

    @Autowired
    private SalarieAideADomicileRepository salarieAideADomicileRepository;

    public SalarieAideADomicileService() {}

    public Long countSalaries() {
        return salarieAideADomicileRepository.count();
    }

    public Page<SalarieAideADomicile> getSalaries(Pageable pageable) {
        return salarieAideADomicileRepository.findAll(pageable);
    }

    // ✅ TP Recherche : nom + pagination + tri
    public Page<SalarieAideADomicile> getSalaries(String nom, Pageable pageable) {
        return salarieAideADomicileRepository.findAllByNomContainingIgnoreCase(nom, pageable);
    }

    public SalarieAideADomicile getSalarie(Long id) {
        return salarieAideADomicileRepository.findById(id).orElse(null);
    }

    public SalarieAideADomicile creerSalarieAideADomicile(SalarieAideADomicile salarieAideADomicile)
            throws SalarieException, EntityExistsException {
        if (salarieAideADomicile.getId() != null) {
            throw new SalarieException("L'id ne doit pas être fourni car il est généré");
        }
        return salarieAideADomicileRepository.save(salarieAideADomicile);
    }

    public SalarieAideADomicile updateSalarieAideADomicile(SalarieAideADomicile salarieAideADomicile)
            throws SalarieException, EntityExistsException {
        if (salarieAideADomicile.getId() == null) {
            throw new SalarieException("L'id doit être fourni");
        }
        Optional<SalarieAideADomicile> existantOptional =
                salarieAideADomicileRepository.findById(salarieAideADomicile.getId());
        if (existantOptional.isEmpty()) {
            throw new SalarieException("Le salarié n'existe pas d'id " + salarieAideADomicile.getId());
        }
        return salarieAideADomicileRepository.save(salarieAideADomicile);
    }

    public void deleteSalarieAideADomicile(Long id)
            throws SalarieException, EntityExistsException {
        if (id == null) {
            throw new SalarieException("L'id doit être fourni");
        }
        if (!salarieAideADomicileRepository.existsById(id)) {
            throw new SalarieException("Le salarié n'existe pas d'id " + id);
        }
        salarieAideADomicileRepository.deleteById(id);
    }

    // ---- le reste de ton service inchangé ----

    public long calculeLimiteEntrepriseCongesPermis(LocalDate moisEnCours, double congesPayesAcquisAnneeNMoins1,
                                                    LocalDate moisDebutContrat,
                                                    LocalDate premierJourDeConge, LocalDate dernierJourDeConge) {

        double proportionPondereeDuConge = Math.max(Entreprise.proportionPondereeDuMois(premierJourDeConge),
                Entreprise.proportionPondereeDuMois(dernierJourDeConge));
        double limiteConges = proportionPondereeDuConge * congesPayesAcquisAnneeNMoins1;

        Double partCongesPrisTotauxAnneeNMoins1 = salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1();

        double proportionMoisEnCours = ((premierJourDeConge.getMonthValue()
                - Entreprise.getPremierJourAnneeDeConges(moisEnCours).getMonthValue()) % 12) / 12d;

        double proportionTotauxEnRetardSurLAnnee = proportionMoisEnCours - partCongesPrisTotauxAnneeNMoins1;
        limiteConges += proportionTotauxEnRetardSurLAnnee * 0.2 * congesPayesAcquisAnneeNMoins1;

        int distanceMois = (dernierJourDeConge.getMonthValue() - moisEnCours.getMonthValue()) % 12;
        limiteConges += limiteConges * 0.1 * distanceMois / 12;

        int anciennete = moisEnCours.getYear() - moisDebutContrat.getYear();
        limiteConges += Math.min(anciennete, 10);

        BigDecimal limiteCongesBd = new BigDecimal(Double.toString(limiteConges));
        limiteCongesBd = limiteCongesBd.setScale(3, RoundingMode.HALF_UP);
        return Math.round(limiteCongesBd.doubleValue());
    }
}