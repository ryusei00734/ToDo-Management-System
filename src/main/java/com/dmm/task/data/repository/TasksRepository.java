package com.dmm.task.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import com.dmm.task.data.entity.Tasks;

public interface TasksRepository extends JpaRepository<Tasks, Integer> {
	@Query("select a from Tasks a where a.date between :from and :to and name = :name")
	List<Tasks> findByDateBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("name") String name);

	Streamable<Order> findAllByDeadlineBetween(LocalDateTime atStartOfDay, LocalDateTime atStartOfDay2);

	Streamable<Order> findAllByDeadlineBetweenAndName(LocalDateTime atStartOfDay, LocalDateTime atStartOfDay2,
			String name);
}