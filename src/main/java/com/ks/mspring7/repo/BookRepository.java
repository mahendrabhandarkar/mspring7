package com.ks.mspring7.repo;

import com.ks.mspring7.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
