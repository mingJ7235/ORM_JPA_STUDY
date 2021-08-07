package hellojpa.inheritence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("b")
public class Book extends Item{

    private String author;
    private String isbn;
}
