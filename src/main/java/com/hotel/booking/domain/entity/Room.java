package com.hotel.booking.domain.entity;

import com.hotel.booking.domain.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "property")
public class Room {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private RoomType roomType;

    @NotNull
    @Column(nullable = false)
    private BigDecimal pricePerNight;

    @Min(1)
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean available = true;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}
