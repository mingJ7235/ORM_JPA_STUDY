package hellojpa.inheritence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") //DTYPE의 이름. default값은 entity명이다.
public class Album extends Item{

    private String artist;
}
