package jpabook.jpashop.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderQueryRepository orderQueryRepository;

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

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = orderQueryRepository.findOrderQueryDtos();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = orderQueryRepository.findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = orderQueryRepository.findOrderQueryDtos();

        List<Long> orderIds = result.stream()
            .map(OrderQueryDto::getOrderId)
            .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = orderQueryRepository.findOrderItemMap(orderIds);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
            .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    public List<OrderQueryDto> findAllByDto_flat() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

//        return flats.stream()
//            .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//                Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
//            )).entrySet().stream()
//            .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
//            .collect(Collectors.toList());

        Map<OrderQueryDto, List<OrderItemQueryDto>> groupedOrders = flats.stream()
            .collect(Collectors.groupingBy(
                flat -> new OrderQueryDto(
                    flat.getOrderId(),
                    flat.getName(),
                    flat.getOrderDate(),
                    flat.getOrderStatus(),
                    flat.getAddress()
                ),
                Collectors.mapping(
                    flat -> new OrderItemQueryDto(
                        flat.getOrderId(),
                        flat.getItemName(),
                        flat.getOrderPrice(),
                        flat.getCount()
                    ),
                    Collectors.toList()
                )
            ));

        return groupedOrders.entrySet().stream()
            .map(entry -> {
                OrderQueryDto orderQueryDto = entry.getKey();
                List<OrderItemQueryDto> orderItems = entry.getValue();
                orderQueryDto.setOrderItems(orderItems);
                return orderQueryDto;
            })
            .collect(Collectors.toList());
    }

}
