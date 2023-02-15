package com.econovation.recruit.domain.board;

import com.econovation.recruit.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "navigation_id")
    private Navigation navigation;

    @Column(name = "col_loc")
    private Integer colLoc;

    @Column(name = "col_title")
    private String colTitle;

    @Column(name = "low_loc")
    private Integer lowLoc;

    public void update(Integer colLoc, Integer lowLoc) {
        this.colLoc = colLoc;
        this.lowLoc = lowLoc;
    }
}