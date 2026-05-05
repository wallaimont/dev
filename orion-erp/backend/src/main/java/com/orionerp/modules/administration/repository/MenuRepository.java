package com.orionerp.modules.administration.repository;

import com.orionerp.modules.administration.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByAtivoTrueAndDeletedFalseOrderByOrdemAsc();
}
