package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
@Transactional
/*
jpa의 모든 데이터변경은 트랜잭션안에서 이루어진다. 그래서 항상 필요하다. 이 어노테이션은 클래스레벨 또는 메서드레벨에 붙일수있다.
클래스레벨에 붙이지 않을거라면 save, update 메소드에 붙이면 된다. 조회는 데이터변경이 아니니까.
* */
public class JpaItemRepositoryV1 implements ItemRepository {

    //jpa에는 EntityManager라는 의존관계주입을 받아야한다, 스프링이 알아서 만들어서 빈에 넣어놨음
    private final EntityManager em;

    //생성자가 하나이므로 @Autowired를 생략할 수 있다, 생성자를 통해 의존관계 주입
    public JpaItemRepositoryV1(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        //persist -> 영구희 보존하다. ( 지속하다)
        em.persist(item); // persist()를 사용하면 Item 필드를 가지고 sql를 만들어서 insert해준다. 그리고 id값도 가져와서 전달인자에 넣어준다.
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName()); // 객체의 값을 바꾸듯이 set을 이용해서 값을 바꿔주면 알아서 데이터베이스에 적용된다.
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //이렇게만 해줘도 나중에 commit될때 jpa가 sql를 만들어서 db에 날려준다. [commit을 이 메서드(update)가 끝날때 진행된다.]
        //마치 자바 collection을 이용해서 값을 변경하는것처럼 데이터를 변경할 수 있다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id); // 찾으려는 엔티티 클래스,primary key를 넣어준다.
        return Optional.ofNullable(item); //혹시 null일수도 있으니까 Optional.ofNullable() 사용
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        /*
        위의처럼 기본키를 넣는게 아닌 , 조건이  여러가지인 경우 jpa는 sql이 아닌 jpql이라는것을 사용한다.
        jpql은 객체 쿼리 언어라고 한다. jpql은 sql과 거의 비슷한데 테이블을 대상으로 하는것이 아닌 엔티티를 대상으로 한다.
         */
        String jpql = "select i from Item i";
        /*
        여기서 Item은 엔티티객체인 Item.class를 말하는것이고 i는 sql의 as(alias==별칭) 처럼 Item의 별칭설정이다.
        하지만 jpql도 동적쿼리에 약하다.
        아래는 jppql을 이용해서 동적쿼리를 만들때 코드
         */
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        //예전 jdbcTemplate을 이용해서 동적쿼리 짜는것과 비슷하다. 다른건 이름기반(필드이름)의 파라미터를 넣을 수 있다.
        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);

        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName); // 파라미터 넣는법
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();

    }
}