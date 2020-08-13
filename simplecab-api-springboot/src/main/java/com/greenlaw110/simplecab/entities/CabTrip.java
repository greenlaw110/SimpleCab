package com.greenlaw110.simplecab.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cab_trip_view")
public class CabTrip implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "row_num")
    private int id;

    private String medallion;

    @Column(name = "pickup_datetime")
    private Date pickupDate;

    public CabTrip() {}

    public CabTrip(String medallion, Date pickupDate) {
        this.medallion = medallion;
        this.pickupDate = pickupDate;
    }

    public String getMedallion() {
        return medallion;
    }

    public Date getPickupDate() {
        return pickupDate;
    }
}
