package com.ipi.jva320.repository;

import com.ipi.jva320.model.SalarieAideADomicile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SalarieAideADomicileRepository extends JpaRepository<SalarieAideADomicile, Long> {

    SalarieAideADomicile findByNom(String nom);

    // ✅ Recherche par nom (contient) + insensitive + pagination + tri
    Page<SalarieAideADomicile> findAllByNomContainingIgnoreCase(String nom, Pageable pageable);

    @Query("select sum(congesPayesPrisAnneeNMoins1)/sum(congesPayesAcquisAnneeNMoins1) from SalarieAideADomicile")
    Double partCongesPrisTotauxAnneeNMoins1();
}