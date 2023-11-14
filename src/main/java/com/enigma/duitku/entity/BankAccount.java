package com.enigma.duitku.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_bank_account")
@Builder(toBuilder = true)
public class BankAccount {

        @Id
        @Column(name="wallet_id")
        private String id;

        @Column(name="account_no", length = 20, unique = true)
        private String accountNo;

        @Column(name = "bank_name", length = 10)
        private String bankName;

        private Double balance;

}
