package jpabook.jpashop.service;

import java.util.List;
import javax.transaction.Transactional;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findById(itemId).get();

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.cancel();
    }

    public List<Order> findOrders() {
        return orderRepository.findAll();
    }

}
