package com.group1.logistics;

import com.group1.logistics.datastructures.OrderQueue;
import com.group1.logistics.datastructures.RestockStack;
import com.group1.logistics.models.Order;
import com.group1.logistics.models.RestockAction;

public class LinearADTTest {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  MODULE 1 UNIT TESTS: OrderQueue (FIFO) & RestockStack   ");
        System.out.println("==========================================================");

        testOrderQueue();
        testRestockStack();

        System.out.println("\n>>> ALL LINEAR ADT TESTS PASSED!\n");
    }

    private static void testOrderQueue() {
        System.out.println("\n[Testing OrderQueue]");
        OrderQueue q = new OrderQueue(3);
        assert q.isEmpty() : "Initial queue should be empty";
        assert q.size() == 0 : "Initial size should be 0";

        Order o1 = new Order(1001, "Alice", 101, 2, "Manila", 1, 1);
        Order o2 = new Order(1002, "Bob", 102, 1, "Quezon City", 2, 2);
        Order o3 = new Order(1003, "Charlie", 103, 5, "Makati", 3, 3);
        Order o4 = new Order(1004, "David", 104, 1, "Pasig", 1, 4);

        assert q.enqueue(o1) : "Enqueue o1 failed";
        assert q.enqueue(o2) : "Enqueue o2 failed";
        assert q.enqueue(o3) : "Enqueue o3 failed";
        assert q.isFull() : "Queue should be full at capacity 3";
        assert !q.enqueue(o4) : "Enqueue on full queue should return false";

        // Peek
        assert q.peek().getOrderId() == 1001 : "Peek should return front order (1001)";

        // Dequeue 1
        Order dispatched = q.dequeue();
        assert dispatched.getOrderId() == 1001 : "Dequeued should be 1001 (FIFO)";
        assert q.size() == 2 : "Size after 1 dequeue should be 2";

        // Wrap-around enqueue
        assert q.enqueue(o4) : "Wrap-around enqueue of o4 failed";
        assert q.isFull() : "Queue should be full after wrap-around";

        // Test logical FIFO ordering (TC-Q06)
        assert q.get(0).getOrderId() == 1002 : "Logical index 0 should be 1002";
        assert q.get(1).getOrderId() == 1003 : "Logical index 1 should be 1003";
        assert q.get(2).getOrderId() == 1004 : "Logical index 2 should be 1004";

        // Drain
        assert q.dequeue().getOrderId() == 1002;
        assert q.dequeue().getOrderId() == 1003;
        assert q.dequeue().getOrderId() == 1004;
        assert q.isEmpty() : "Queue should be empty";
        assert q.dequeue() == null : "Dequeue on empty should return null safely";

        System.out.println("  -> OrderQueue (wrap-around, boundaries, logical FIFO): PASS");
    }

    private static void testRestockStack() {
        System.out.println("\n[Testing RestockStack]");
        RestockStack s = new RestockStack(3);
        assert s.isEmpty() : "Initial stack should be empty";

        RestockAction a1 = new RestockAction(1, 101, 10, 1);
        RestockAction a2 = new RestockAction(2, 102, 20, 2);
        RestockAction a3 = new RestockAction(3, 103, 30, 3);
        RestockAction a4 = new RestockAction(4, 104, 40, 4);

        assert s.push(a1);
        assert s.push(a2);
        assert s.push(a3);
        assert s.isFull() : "Stack should be full at capacity 3";
        assert !s.push(a4) : "Push on full stack should return false";

        // Peek
        assert s.peek().getActionId() == 3 : "Peek should return newest action (3)";

        // Test display order (newest first, index 0 is top)
        assert s.get(0).getActionId() == 3;
        assert s.get(1).getActionId() == 2;
        assert s.get(2).getActionId() == 1;

        // Pop LIFO
        assert s.pop().getActionId() == 3 : "Pop should return newest action 3";
        assert s.pop().getActionId() == 2;
        assert s.pop().getActionId() == 1;
        assert s.isEmpty();
        assert s.pop() == null : "Pop on empty should return null safely";

        System.out.println("  -> RestockStack (LIFO, newest-first display, overflow/underflow): PASS");
    }
}
