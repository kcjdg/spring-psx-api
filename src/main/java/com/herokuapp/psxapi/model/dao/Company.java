package com.herokuapp.psxapi.model.dao;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table
@NoArgsConstructor
public class Company implements Serializable {

    @Id
    private String symbol;
    private String securityStatus;
    private String securityId;
    private String securityName;

}
