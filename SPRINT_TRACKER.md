# 📋 Solo Engineering Task Board & Milestones
**E-Commerce Fulfillment & Logistics System (Group 1)**  
*BSIS 2nd Year — Data Structures & Algorithms*

---

## 🚀 Phase 1: Core Primitive ADTs & Algorithmic Unit Tests

| Task ID | Component | Task / Deliverable | Status |
| :--- | :--- | :--- | :---: |
| **P1-01** | Architecture | Project scaffold: packages (`models`, `datastructures`, `sorting`, `service`, `ui`) | `[x] Done` |
| **P1-02** | Models | Create `Order.java`, `RestockAction.java`, `FreightPackage.java`, `Product.java` | `[x] Done` |
| **P1-03** | Linear ADTs | Implement circular array `OrderQueue.java` (FIFO) + bounds check | `[x] Done` |
| **P1-04** | Linear ADTs | Implement primitive array `RestockStack.java` (LIFO) + safe Undo | `[x] Done` |
| **P1-05** | Linear ADTs | Run `LinearADTTest.java` suite to verify Queue & Stack | `[x] Done` |
| **P1-06** | BST Index | Implement `SKUNode.java` and `InventoryBST.java` (recursive insert, search, inOrder) | `[ ] In Progress` |
| **P1-07** | BST Index | Implement full **3-case deletion** in `InventoryBST.java` (Leaf, 1 Child, 2 Children via Successor) | `[ ] Pending` |
| **P1-08** | Dynamic Array| Implement `FreightPackageArray.java` with manual capacity doubling (replacing `ArrayList`) | `[ ] Pending` |
| **P1-09** | Sorters | Implement custom `QuickSort.java` (Hoare/3-Way partition + median-of-three by Weight) | `[ ] Pending` |
| **P1-10** | Sorters | Implement custom stable `InsertionSort.java` (by Delivery Priority 1-5) | `[ ] Pending` |
| **P1-11** | Unit Tests | Run `BSTTest.java` and `SortTest.java` console test suites | `[ ] Pending` |

---

## ⚙️ Phase 2: Service Layer & Business Rules Engine

| Task ID | Component | Task / Deliverable | Status |
| :--- | :--- | :--- | :---: |
| **P2-01** | Service Layer | Implement `OpResult.java` (standardized response object for UI) | `[ ] Pending` |
| **P2-02** | Service Layer | Implement `FulfillmentService.java` owning all 4 structures | `[ ] Pending` |
| **P2-03** | Business Rules | Wire `BR-03` (Peek -> Validate -> Commit dispatch) and `BR-04` (non-negative stock) | `[ ] Pending` |
| **P2-04** | Business Rules | Wire `BR-05` (atomic restock) and `BR-06` (subtraction-based anti-lost-update Undo) | `[ ] Pending` |
| **P2-05** | Service Tests | Run `ServiceTest.java` console suite to verify all business rules without GUI | `[ ] Pending` |

---

## 🖥️ Phase 3: Desktop Swing GUI & Event Integration

| Task ID | Component | Task / Deliverable | Status |
| :--- | :--- | :--- | :---: |
| **P3-01** | UI Core | Build `MainFrame.java` (JTabbedPane with 3 tabs + live bottom status bar) | `[x] Base Done` |
| **P3-02** | Tab 1 Panel | Build `OrdersRestockPanel.java` (Queue form/table, Dispatch/Reject, Restock, Undo) | `[ ] Pending` |
| **P3-03** | Tab 2 Panel | Build `CatalogPanel.java` (Register SKU, Search autofill, In-Order table, Delete) | `[ ] Pending` |
| **P3-04** | Tab 3 Panel | Build `FreightPanel.java` (Parcel table, Sort by Weight, Sort by Priority, Asc/Desc) | `[ ] Pending` |
| **P3-05** | Table Models | Implement 4 `AbstractTableModel` subclasses (strictly zero `java.util.Vector`) | `[ ] Pending` |
| **P3-06** | Header Clicks | Attach `MouseAdapter` to freight table headers calling custom sorters | `[ ] Pending` |

---

## 📦 Phase 4: Hardening, Packaging & Live Defense Prep

| Task ID | Component | Task / Deliverable | Status |
| :--- | :--- | :--- | :---: |
| **P4-01** | Persistence | Implement pipe-delimited CSV `FileStore.java` with balanced seed data [SHOULD] | `[ ] Pending` |
| **P4-02** | Compliance | Run PowerShell `COMP-01` regex scan to confirm ZERO prohibited imports | `[ ] Pending` |
| **P4-03** | Bytecode Audit | Run `javap -v` to confirm compiled bytecode targets Java 17 (`major version: 61`) | `[ ] Pending` |
| **P4-04** | Executable JAR | Build standalone `dist/LogisticsHub.jar` and verify double-click on clean PC | `[x] Base Done` |
| **P4-05** | Documentation | Finalize `Group1_Technical_Documentation.pdf` with UML diagrams & flowcharts | `[ ] Pending` |
| **P4-06** | Live Defense | Rehearse 10-minute solo demonstration using the script in `README.md` §9.2 | `[ ] Pending` |
