package hello.itemservice.domain;

import javax.persistence.Column; //persistence 라이브러리를 사용한다.
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity // jpa에서 관리하는 객체라는것을 알리기 위함. [ 테이블이랑 같이 매핑이 되서 관리가 되는 객체]
//@Table(name = "item") 이렇게 테이블명도 지정할수있지만 객체의 이름과 테이블명이 같으면 생략가능하다.
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //pk를 알려줘야한다,어떤 방식으로 id값이 생성되는지 알려준다.
    //identity 전략은 db에서 id값을 넣어주는 전략 autoincrement처럼
    private Long id;

    @Column(name = "item_name", length = 10) //어떤 컬럼과 매핑이 되는지 알려주기 위해 컬럼명을 적어준다, length는 해당 컬럼의 길이 설정
    private String itemName;

    //컬럼명과 필드명이 같으면 @Column을 따로설정할 필요없다.
    private Integer price;
    private Integer quantity;

    //jpa는 public 또는 protected의 접근제어자를 가진 기본생성자가 필수이다.
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}