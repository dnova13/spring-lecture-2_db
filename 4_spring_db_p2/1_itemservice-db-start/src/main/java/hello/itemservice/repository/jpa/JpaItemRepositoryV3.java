package hello.itemservice.repository.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import static hello.itemservice.domain.QItem.*;

@Repository
@Transactional
// 서비스에서 Transactional을 해야하지만 , 여기서는 서비스없이 바로 리포지토리를 테스트하기 위해 사용 + jpa사용하려면 트랜잭션 필요
public class JpaItemRepositoryV3 implements ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;
    /*
    queryDsl 사용하려면 JPAQueryFactory 필요
    JPAQueryFactory 는 queryDsl 것이다. queryDsl 은 결과적으로 jap 의 jpql 을 만들어주는 빌더역할을 한다.
    -> JPAQueryFactory 는 jpa 쿼리 즉 jpql 을 만들어준다.
     */

    public JpaItemRepositoryV3(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
        //JPAQueryFactory 를 밖에서 스프링빈으로 등록 후 주입 받아도 된다.
    }

    @Override
    public Item save(Item item) {
        //jpa 사용
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        //jpa 사용
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());

    }

    @Override
    public Optional<Item> findById(Long id) {
        //jpa 사용
        Item item = em.find(Item.class, id); // 찾으려는 엔티티 클래스,primary key를 넣어준다.
        return Optional.ofNullable(item); //혹시 null일수도 있으니까 Optional.ofNullable() 사용
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        //QueryDsl 사용

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        BooleanBuilder builder = new BooleanBuilder(); //조건을 넣을곳
        if (StringUtils.hasText(itemName)) {
            //itemName 에 텍스트가 존재한다면
            builder.and(item.itemName.like("%" + itemName + "%"));
        }
        if (maxPrice != null) { //loe -> lower or equal 작거나 같다.
            builder.and(item.price.loe(maxPrice));
        }

//        QItem item = new QItem("i"); //매개변수가 alias(별칭)이 된다, 이렇게 꺼내도 되지만
        //QItem.class 내부에 생성되어있는 객체를 사용해도된다. QItem.item 처럼사용
        //하지만 그것도 길수있으므로 on-demand static import 해서 item 으로 사용

        List<Item> result = query.select(item)
                .from(item)
                .where(builder)
                .fetch();

        /*
        fetch()를 해주면 list 가 반환된다.
         */

        return result;
    }
}