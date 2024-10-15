package hello.itemservice.repository.jpa;
import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item,Long> {
    /*
    인터페이스여야함.
    제네릭에는 관리하는 엔티티객체,pk타입을 적어준다.
    이것만으로 JpaRepository 인터페이스가 제공하는 기본기능은 사용할 수 있다.
    추가하고 싶은 기능은 아래에 쿼리메소드 기능을 이용하던지 쿼리 직접실행을 이용하던지해서 적어주면 된다.
     */


    //쿼리 메소드 기능 이용 [ 메소드의 이름을 이용해서 jpql 생성]
    List<Item> findByItemNameLike(String itemName);

    List<Item> findByPriceLessThanEqual(Integer price);

    //아래 쿼리 직접 실행과 같은 기능의 메소드
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    //쿼리 직접 실행
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);
    //@Param 어노테이션은 spring~.data.query 라이브러리 것을 사용해야한다, 마이바티스것을 사용하지말것


}