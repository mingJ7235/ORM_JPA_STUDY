package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() {
        Item item = new Item("A"); //이렇게 값이 세팅되어있으면 isNew에서 false가 되므로, persist를 안하고 merge를 한다.
        /**
         * merge는 거의 쓰면 안된다. 데이터의 변경은 변경감지로 사용해야한다.
         */
        itemRepository.save(item);
    }
}