package com.enigma.duitku.entity;

import com.enigma.duitku.entity.constant.EWalletType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_wallet")
@Builder(toBuilder = true)
public class Wallet {

    @Id
    @Column(name="wallet_id")
    private String id;

    @Column(nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    private EWalletType walletType;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Transaction> listOfTransactions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Beneficiary> listOfBeneficiaries = new ArrayList<>();

    @ElementCollection
    private List<Bill> listofBills = new ArrayList<>();
}
