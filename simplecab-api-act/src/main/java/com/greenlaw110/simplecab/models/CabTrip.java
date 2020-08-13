package com.greenlaw110.simplecab.models;

import act.db.jpa.JPADao;
import act.test.NotFixture;
import act.util.SimpleBean;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NotFixture
@Entity
@Table(name = "cab_trip_view")
public class CabTrip implements SimpleBean {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "row_num")
    public int id;

    public String medallion;

    @Column(name = "pickup_datetime")
    public Date pickupDate;

    public static class Dao extends JPADao<Integer, CabTrip> {

        public Map<String, Long> countByCabAndPickupDate(String medallion, Date pickupDate) {
            return countTrips(qForCountByCabAndPickupDate(medallion, pickupDate));
        }

        public Map<String, Long> countByCabs(List<String> medallions) {
            return countTrips(qForCountByCabs(medallions));
        }

        private javax.persistence.Query qForCountByCabAndPickupDate(String medallion, Date pickupDate) {
            String sql = "SELECT c.medallion as medallion, count(c) as count FROM CabTrip c WHERE " +
                    "c.medallion = :medallion and " +
                    "date(c.pickupDate) = date(:pickupDate)";
            javax.persistence.Query q = em().createQuery(sql);
            q.setParameter("medallion", medallion);
            q.setParameter("pickupDate", pickupDate);
            return q;
        }

        private javax.persistence.Query qForCountByCabs(List<String> medallions) {
            String sql = "SELECT c.medallion as medallion, count(c) as count FROM CabTrip c \n" +
                    "WHERE c.medallion IN :medallions " +
                    "GROUP BY c.medallion \n";
            javax.persistence.Query q = em().createQuery(sql);
            q.setParameter("medallions", medallions);
            return q;
        }

        private Map<String, Long> countTrips(javax.persistence.Query q) {
            List<Object[]> counts = q.getResultList();
            return counts.stream().collect(Collectors.toMap(a -> (String)a[0], a -> (Long)a[1]));
        }
    }
}
