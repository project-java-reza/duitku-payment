package com.enigma.duitku.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_transaction")
@Builder(toBuilder = true)
public class Transaction {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    @Column(name = "wallet_id", nullable = false, length = 20)
    private String walletId;

    @Column(name = "local_date", nullable = false)
    private LocalDate localDate;

    @Column(name = "local_time", nullable = false)
    private LocalTime localTime;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 30)
    private String description;

    @Column(nullable = false, length = 50)
    private String receiver;

}
