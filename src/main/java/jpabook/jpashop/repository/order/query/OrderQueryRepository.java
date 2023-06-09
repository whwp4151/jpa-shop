package jpabook.jpashop.repository.order.query;

import java.util.List;
import jpabook.jpashop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderQueryRepository extends JpaRepository<Order, Long> {

    @Query("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
        + " from Order o"
        + " join o.member m"
        + " join o.delivery d")
    List<OrderQueryDto> findOrderQueryDtos();

    @Query("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
        + " from OrderItem oi"
        + " join oi.item i"
        + " where oi.order.id = :orderId")
    List<OrderItemQueryDto> findOrderItems(Long orderId);

    @Query("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
        + " from OrderItem oi"
        + " join oi.item i"
        + " where oi.order.id in :orderIds")
    List<OrderItemQueryDto> findOrderItemMap(List<Long> orderIds);

    @Query("select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) "
        + " from Order o"
        + " join o.member m"
        + " join o.delivery d"
        + " join o.orderItems oi"
        + " join oi.item i")
    List<OrderFlatDto> findAllByDto_flat();

}
