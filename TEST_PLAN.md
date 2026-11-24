# 🧪 Quality Assurance & Test Plan (Sprint 1 to 3)
**Group 1: E-Commerce Fulfillment & Logistics System** · Revision 2

---

## 🎯 Purpose
Prove the application meets every project constraint and survives a live demonstration without crashing. Rule references (BR-xx) point to the business rules in `README.md` §1.4.

## 0. How to Use This Plan

**Status values:** `Not run` · `Pass` · `Fail` · `Blocked`. Rows marked *(v1)* carry the result reported in the first version of this plan; every other row starts as `Not run`.

**Test levels**

| Level | What it covers | How it runs |
| :--- | :--- | :--- |
| **U**: Unit | One class in isolation (queue, stack, BST, sorters) | Console test classes (`LinearADTTest`, `BSTTest`, `SortTest`) |
| **BL**: Business logic | `FulfillmentService` rules BR-01 to BR-09 | `ServiceTest` (no GUI needed) |
| **V / G**: Validation & GUI | Input handling, layout, usability | Manual, using the built JAR |
| **E**: End-to-end | Full operator scenarios and the demo script | Manual, using the built JAR |
| **C**: Compliance & packaging | Spec constraints, JAR, submission package | Scan commands and clean-machine checks |

**Environment:** JDK/JRE 17+, Windows, a 1366×768 display (typical lab laptop), the JAR built from a recorded commit. Log the commit and date for each run in §10.

---

## 1. Module 1: ArrayQueue (Level U)

| Test ID | Scenario | Input / Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-Q01** | Enqueue when space is available | Order #1001, Qty 2 | Returns `true`; order appears at the end of the queue table | **Pass** *(v1)* |
| **TC-Q02** | Dequeue from non-empty queue (FIFO) | Click "Dispatch Next" | Oldest order (#1001) removed and returned | **Pass** *(v1)* |
| **TC-Q03** | Enqueue on full queue (overflow) | Capacity 3, add a 4th order | Returns `false`; "Queue is full" warning | **Pass** *(v1)* |
| **TC-Q04** | Dequeue on empty queue (underflow) | Click "Dispatch" when empty | Returns `null`; "Queue is empty" dialog | **Pass** *(v1)* |
| **TC-Q05** | Circular wrap-around | Fill, dequeue 2, enqueue 2 | Pointers wrap with `(rear + 1) % capacity`, no crash | **Pass** *(v1)* |
| **TC-Q06** | `get(i)` / table order after wrap-around | Capacity 4: enqueue A,B,C,D; dequeue 2; enqueue E,F | `get(0..3)` = C,D,E,F (logical FIFO order, not raw array order) | Not run |
| **TC-Q07** | Peek behavior | Peek on empty; peek on `[A]` | `null`; then A returned and still in the queue (size unchanged) | Not run |
| **TC-Q08** | Capacity of 1 | Enqueue A; enqueue B; dequeue; enqueue B | Second enqueue fails; after the dequeue the next enqueue succeeds | Not run |
| **TC-Q09** | Null element | `enqueue(null)` | Rejected; size unchanged | Not run |
| **TC-Q10** | Long run | 10,000 enqueue/dequeue cycles at capacity 3 | FIFO order never breaks; no index error; count correct throughout | Not run |

## 2. Module 1: ArrayStack (Level U)
*(IDs renamed from TC-S01..S04 in v1: they collided with the sorting tests.)*

| Test ID | Scenario | Input / Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-ST01** | Push restock action | SKU 101, Added 50 | Returns `true`; action on top of the log table | **Pass** *(v1)* |
| **TC-ST02** | Undo last restock (LIFO pop) | Click "Undo Last Restock" | Most recent restock popped; stock reverted | **Pass** *(v1)* |
| **TC-ST03** | Pop on empty stack (underflow) | Click "Undo" with empty stack | Returns `null`; "No actions to undo" message | **Pass** *(v1)* |
| **TC-ST04** | Push on full stack (overflow) | Push beyond capacity | Returns `false`; "Stack is full" warning | **Pass** *(v1)* |
| **TC-ST05** | LIFO order | Push A, B, C; pop three times | C, B, A | Not run |
| **TC-ST06** | Peek behavior | Peek on empty; peek on `[A]` | `null`; then A returned and still on the stack | Not run |
| **TC-ST07** | Display order | Push A, B, C | `get(0)` = C (newest first), `get(2)` = A | Not run |
| **TC-ST08** | Space reuse | Fill, empty, fill again | No false overflow; `top` = -1 after emptying | Not run |

---

## 3. Module 2: InventoryBST (Level U)

| Test ID | Scenario | Input / Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-B01** | Insert unique SKU | SKU 105, "Wireless Mouse" | Added recursively at the correct left/right position | Not run |
| **TC-B02** | Insert duplicate SKU | SKU 105 again | Rejected; "SKU already exists"; tree and size unchanged | Not run |
| **TC-B03** | Search existing SKU | Search 105 | Found; GUI text fields populated | Not run |
| **TC-B04** | Search non-existent SKU | Search 999 | "Product not found" dialog | Not run |
| **TC-B05** | In-order traversal | Call `inOrder()` | Array strictly ascending by SKU ID; length equals `size` | Not run |
| **TC-B06** | Delete Case 1: leaf | Delete a node with no children | Node removed; parent pointer `null` | Not run |
| **TC-B07** | Delete Case 2: one child | Test **both** left-only and right-only child | Node bypassed; child linked to parent | Not run |
| **TC-B08** | Delete Case 3: two children | Delete a node with two children | Replaced by the in-order successor; successor removed | Not run |
| **TC-B09** | Insert into empty tree | First insert | Becomes the root; `size` = 1 | Not run |
| **TC-B10** | Sorted-order insertion (degenerate tree) | Insert 1..50 ascending | Search, in-order and delete still correct; `height()` = 50 | Not run |
| **TC-B11** | Mixed-order insertion | 50, 30, 70, 20, 40, 60, 80 | In-order = 20,30,40,50,60,70,80; height 3 | Not run |
| **TC-B12** | Delete the root | Root with 0, 1, then 2 children (three trees) | Tree remains valid; new root correct in each | Not run |
| **TC-B13** | Delete the only node | Tree with a single node | Tree becomes empty (`root` null, `size` 0); a later insert works | Not run |
| **TC-B14** | Delete a non-existent key | Delete 999 | Returns `false`; tree and size unchanged | Not run |
| **TC-B15** | Case 3, successor is the direct right child | Delete 50 in 50,30,70,60 (70 has no left child) | 70 replaces 50; tree valid | Not run |
| **TC-B16** | Case 3, successor has its own right child | Build a tree where the successor has a right child | That right child is re-linked correctly; no node lost | Not run |
| **TC-B17** | Invariant after deletions | Run B06-B08, B12, B16 | In-order stays strictly ascending; `size` accurate | Not run |
| **TC-B18** | Randomized stress | 1,000 random inserts/deletes, check after every 50 operations | In-order ascending, `size` matches a manual count, nothing lost or duplicated | Not run |
| **TC-B19** | Boundary keys | 0, -1, `Integer.MAX_VALUE` | Handled per BR-01 (0 and negatives rejected as invalid SKUs); no overflow error | Not run |
| **TC-B20** | Search after delete | Delete 105, then search 105 | Not found | Not run |

---

## 4. Module 3: Sorting Engine (Level U)
*(IDs renamed from TC-S01..S03 in v1.)*

| Test ID | Scenario | Input / Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-SO01** | QuickSort by weight | Sort packages by weightKg | Table re-orders from lowest to highest weight | Not run |
| **TC-SO02** | InsertionSort by priority | Sort packages by priority (1-5) | Priority 1 (Express) at the top | Not run |
| **TC-SO03** | Empty and single-element arrays | Both sorters | No exception | Not run |
| **TC-SO04** | Already sorted input | 20 ascending weights | Unchanged; no error | Not run |
| **TC-SO05** | Reverse-sorted input | 20 descending weights | Correctly ascending | Not run |
| **TC-SO06** | All-equal keys | 20 packages with identical weight | Completes quickly; order stable or tie-broken by `sequenceNo` | Not run |
| **TC-SO07** | Stability of InsertionSort | Packages with equal priority in a known arrival order | Equal-priority packages keep arrival order | Not run |
| **TC-SO08** | Two-element arrays | Both orders | Correct in both cases | Not run |
| **TC-SO09** | Ascending/descending toggle | Sort both ways on both algorithms | Each result is the exact reverse (ties aside) | Not run |
| **TC-SO10** | Correctness on large input | 1,000 random packages, both algorithms | Output sorted **and** same elements as input (e.g. package-ID sum unchanged, none lost or duplicated) | Not run |
| **TC-SO11** | QuickSort worst-case input | 20,000 pre-sorted weights | Finishes without `StackOverflowError` and in reasonable time | Not run |

---

## 5. Business Logic: `FulfillmentService` (Level BL)
These protect against the riskiest bugs. Use stock 10 for SKU 101 unless stated.

| Test ID | Rule | Scenario | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-BL01** | BR-03 | Dispatch a valid order, qty 4 | Stock 6; queue shrinks by 1; one `FreightPackage` created with the order's ID, destination and priority | Not run |
| **TC-BL02** | BR-03/04 | Order qty 11 (more than stock) | Order **stays at the front**; stock unchanged; message states the available quantity | Not run |
| **TC-BL03** | BR-03 | Operator rejects the front order after TC-BL02 | Order dequeued and logged as REJECTED; stock unchanged | Not run |
| **TC-BL04** | BR-07 | Enqueue an order, delete its SKU, dispatch | Rejected with "SKU discontinued"; no crash | Not run |
| **TC-BL05** | BR-04 | Order qty exactly equal to stock | Dispatch succeeds, stock 0; the next order for the SKU fails cleanly | Not run |
| **TC-BL06** | BR-06 | Restock +50 (stock 60), then undo with nothing in between | Stock back to 10; entry popped | Not run |
| **TC-BL07** | BR-06 | **Lost-update regression:** restock +50 (60), dispatch qty 5 (55), undo | Stock **5**, not 10 | Not run |
| **TC-BL08** | BR-06 | Restock +50 (60), dispatch qty 58 (2), undo | Undo **refused** ("units already shipped"); stock stays 2; entry **remains** on the stack | Not run |
| **TC-BL09** | BR-05 | Restock for an unknown SKU | Rejected; nothing pushed; no stock change | Not run |
| **TC-BL10** | BR-05 | Restock while the stack is full | Rejected; **stock unchanged** (atomic) | Not run |
| **TC-BL11** | BR-06/07 | Restock SKU 101, delete SKU 101, then undo | Entry discarded with a warning; no crash | Not run |
| **TC-BL12** | BR-06 | Three restocks, three undos | Reverted in reverse order; stock returns to the start value | Not run |
| **TC-BL13** | BR-08 | Packing weight validation | 0, negative, NaN, Infinity and over-limit weights rejected; the order is not lost | Not run |
| **TC-BL14** | BR-04 | Randomized operations (restock/dispatch/undo/delete, 500 steps) | Stock is never negative; BST size and in-order stay consistent | Not run |
| **TC-BL15** | BR-09 | Sort packages | BST unchanged (in-order identical before and after) | Not run |

---

## 6. Input Validation (Level V)
Every case must show an inline or dialog error message, **no stack trace, no state change**.

| Test ID | Field | Bad inputs to try | Expected | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TC-V01** | SKU ID / SKU in an order | empty, spaces only, `abc`, `12.5`, `-5`, `0`, `2147483648`, `+5`, ` 5` (leading space), `1,000` | Rejected, or trimmed if designed to be (be consistent) | Not run |
| **TC-V02** | Quantity (order, restock) | empty, `abc`, `0`, `-1`, `2.5`, `2147483648` | Rejected | Not run |
| **TC-V03** | Price / weight | empty, `abc`, `-1`, `NaN`, `Infinity`, `1e3`, `5d` | Rejected; `Double.parseDouble` happily accepts `NaN`, `Infinity` and `5d`, so check explicitly with `Double.isNaN` / `isInfinite` | Not run |
| **TC-V04** | Text fields (name, customer, destination) | empty, spaces only, 200+ characters, `\|` and commas | Rejected or safely handled; saved and reloaded data is not corrupted | Not run |
| **TC-V05** | Priority | Value outside 1-5 (if the control allows typing) | Rejected | Not run |
| **TC-V06** | Basic validation *(v1: COMP-03)* | Non-numeric SKU/quantity, negative numbers | Handled | **Pass** *(v1)* |

---

## 7. GUI & Usability (Level G)

| Test ID | Check | Expected | Status |
| :--- | :--- | :--- | :---: |
| **TC-G01** | Window fits a 1366×768 screen; resize and maximize | No clipped buttons; tables scroll | Not run |
| **TC-G02** | Switch between all 3 tabs mid-workflow | State preserved; tables stay in sync with the data | Not run |
| **TC-G03** | Table cells | Not editable by double-click | Not run |
| **TC-G04** | Status bar | Queue, restock log, catalog size/height and package counts update after every operation | Not run |
| **TC-G05** | Delete SKU | Confirmation dialog appears; Cancel changes nothing | Not run |
| **TC-G06** | Close the window | Process exits (no leftover Java process in Task Manager) | Not run |
| **TC-G07** | Keyboard use | Tab order logical; Enter submits the focused form | Not run |
| **TC-G08** | Header-click sorting | Clicking a freight-table header runs the custom sorter; the active-sort label updates; **no `TableRowSorter` in the code** | Not run |
| **TC-G09** | Console while using the app | No exceptions or stack traces printed | Not run |

---

## 8. End-to-End Scenarios (Level E)

| Test ID | Scenario | Expected | Status |
| :--- | :--- | :--- | :---: |
| **TC-E01** | Full demo script (README §9.1) from a double-clicked JAR | Completes in 10 minutes with no errors | Not run |
| **TC-E02** | Persistence round trip (if implemented): add data, close, reopen | Catalog (in-order), queued orders and packages restored in the correct order | Not run |
| **TC-E03** | Missing or corrupted data file | App starts with seed data; bad lines skipped; no crash | Not run |
| **TC-E04** | Volume: load 5,000 SKUs and 1,000 packages | UI stays responsive (refresh well under 1 second); sorts complete | Not run |
| **TC-E05** | Sequential-SKU demo data (degenerate tree) | Still correct; status bar shows the large height | Not run |
| **TC-E06** | Run the app twice in a row, and launch from a folder path containing spaces | Both work | Not run |

---

## 9. Compliance & Packaging (Level C)

| Check ID | Requirement | Verification Method | Status |
| :--- | :--- | :--- | :---: |
| **COMP-01** | No forbidden `java.util` usage | Run `Get-ChildItem -Path src,test -Recurse -Filter *.java \| Select-String -Pattern 'java\.util\|Arrays\.\|Collections\.\|\.stream\(\|List\.of\|Map\.of'`. **Expected: zero matches** (only documented exceptions, e.g. `Scanner` in console tests). This covers wildcard imports and fully qualified names, which a name-by-name search misses. | **Pass** *(v1, narrower scan: re-run)* |
| **COMP-02** | Standalone executable JAR | Double-click the JAR (a) on the dev machine with no IDE open, (b) on a clean machine with only a JRE 17+, (c) after extracting the final submission zip | Not run |
| **COMP-03** | Table sorting is our own | Search for `RowSorter` and `setAutoCreateRowSorter`: zero matches | Not run |
| **COMP-04** | Runs on Java 17 | Check a class in the JAR with `javap -v`; **major version 61** (Java 17) | Not run |
| **COMP-05** | Source package is complete | Fresh extract of the source `.zip` → open in NetBeans → *Clean and Build* → app runs; `.form` files present | Not run |
| **COMP-06** | Submission contents | `.jar`, source `.zip`, documentation `.pdf` all present | Not run |
| **COMP-07** | Documentation completeness | System overview, class diagrams, algorithm flowcharts, user guide, and the BR-01 duplicate-key note | Not run |

---

## 10. Sprint Exit Criteria, Defects & Run Log

**Exit criteria**

| Sprint | Must be true to close the sprint |
| :--- | :--- |
| **Sprint 1** | TC-Q01..Q10 and TC-ST01..ST08 pass; API signatures agreed; UI skeleton runs on stub data |
| **Sprint 2** | TC-B01..B20, TC-SO01..SO11 and TC-BL01..BL15 pass; all three tabs are wired to the service; integrated demo done |
| **Sprint 3** | TC-V, TC-G, TC-E and COMP-01..COMP-07 pass; no open Severity 1-2 defects; code freeze held; defense rehearsed twice |

**Defect log** (one row per bug)

| Bug ID | Found in test | Severity (1 crash/data loss · 2 wrong result · 3 cosmetic) | Owner | Status |
| :--- | :--- | :---: | :--- | :--- |
| *(none logged)* | | | | |

**Run log** (one row per full test run)

| Date | Commit / build | Tester | Tests run | Passed | Failed | Notes |
| :--- | :--- | :--- | :---: | :---: | :---: | :--- |
| | | | | | | |
