/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.quejas.repository;
import com.profeco.quejas.model.Queja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuejaRepository extends JpaRepository<Queja, Long> {
    List<Queja> findAllByOrderByFechaDesc();
    List<Queja> findByUsuarioOrderByFechaDesc(String usuario);
    Optional<Queja> findByQuejaId(String quejaId);
    long count();
}
