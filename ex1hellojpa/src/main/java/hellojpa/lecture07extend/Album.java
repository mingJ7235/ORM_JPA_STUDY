package hellojpa.lecture07extend;

import javax.persistence.Entity;

@Entity
public class Album extends Item{

    private String artist;
}
