package com.ks.mspring7.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class OrderInvoice {

   @Id
   @GeneratedValue
   private Long id;
   private LocalDate localDate;
   private String state;

   @Transient
   String event;

   @Transient
   String paymentType;

}
